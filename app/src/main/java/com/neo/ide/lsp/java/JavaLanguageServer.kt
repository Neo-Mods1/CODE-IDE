/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                    CODE-IDE • NeoMods                      ║
 * ║                  Advanced Android IDE Project              ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 *  (っ◔◡◔)っ ♥
 *
 *  Developer         • NeoMods
 *  Telegram Contact  • @NeoModsDev
 *  Telegram Channel  • https://t.me/NeoModsChannel
 *
 * ──────────────────────────────────────────────────────────────
 *  PROJECT NOTICE
 * ──────────────────────────────────────────────────────────────
 *
 *  This source file is part of the CODE-IDE project.
 *
 *  Unauthorized copying, extraction, redistribution,
 *  mirroring, downloading, modification, or reuse of
 *  CODE-IDE source files is NOT permitted without
 *  explicit permission from the developer.
 *
 *  The application may expose certain components in
 *  read-only mode for educational or preview purposes,
 *  however this DOES NOT grant permission to reuse
 *  or redistribute the source code.
 *
 *  If you need access to the original source code,
 *  implementation details, licensing, or collaboration,
 *  please contact the developer directly.
 *
 *  © NeoMods — All Rights Reserved
 * ──────────────────────────────────────────────────────────────
 */


package com.neo.ide.lsp.java

import androidx.annotation.RestrictTo
import com.neo.ide.eventbus.events.editor.DocumentChangeEvent
import com.neo.ide.eventbus.events.editor.DocumentCloseEvent
import com.neo.ide.eventbus.events.editor.DocumentOpenEvent
import com.neo.ide.eventbus.events.editor.DocumentSelectedEvent
import com.neo.ide.javac.services.fs.CacheFSInfoSingleton
import com.neo.ide.javac.services.fs.CachingJarFileSystemProvider.clearCache
import com.neo.ide.javac.services.fs.CachingJarFileSystemProvider.clearCachesForPaths
import com.neo.ide.lsp.api.ILanguageClient
import com.neo.ide.lsp.api.ILanguageServer
import com.neo.ide.lsp.api.IServerSettings
import com.neo.ide.lsp.internal.model.CachedCompletion
import com.neo.ide.lsp.java.actions.JavaCodeActionsMenu
import com.neo.ide.lsp.java.compiler.JavaCompilerService
import com.neo.ide.lsp.java.compiler.SourceFileManager
import com.neo.ide.lsp.java.models.JavaServerSettings
import com.neo.ide.lsp.java.providers.CodeFormatProvider
import com.neo.ide.lsp.java.providers.CompletionProvider
import com.neo.ide.lsp.java.providers.DefinitionProvider
import com.neo.ide.lsp.java.providers.JavaDiagnosticProvider
import com.neo.ide.lsp.java.providers.JavaSelectionProvider
import com.neo.ide.lsp.java.providers.ReferenceProvider
import com.neo.ide.lsp.java.providers.SignatureProvider
import com.neo.ide.lsp.java.providers.snippet.JavaSnippetRepository.init
import com.neo.ide.lsp.java.utils.AnalyzeTimer
import com.neo.ide.lsp.java.utils.CancelChecker.Companion.isCancelled
import com.neo.ide.lsp.models.CodeFormatResult
import com.neo.ide.lsp.models.CompletionParams
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.models.DefinitionParams
import com.neo.ide.lsp.models.DefinitionResult
import com.neo.ide.lsp.models.DiagnosticResult
import com.neo.ide.lsp.models.ExpandSelectionParams
import com.neo.ide.lsp.models.FailureType
import com.neo.ide.lsp.models.FormatCodeParams
import com.neo.ide.lsp.models.LSPFailure
import com.neo.ide.lsp.models.ReferenceParams
import com.neo.ide.lsp.models.ReferenceResult
import com.neo.ide.lsp.models.SignatureHelp
import com.neo.ide.lsp.models.SignatureHelpParams
import com.neo.ide.lsp.util.LSPEditorActions
import com.neo.ide.models.Range
import com.neo.ide.projects.FileManager.getActiveDocumentCount
import com.neo.ide.projects.IProjectManager.Companion.getInstance
import com.neo.ide.projects.IWorkspace
import com.neo.ide.projects.ModuleProject
import com.neo.ide.utils.DocumentUtils
import com.neo.ide.utils.VMUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.Objects

class JavaLanguageServer : ILanguageServer {

  private val completionProvider: CompletionProvider = CompletionProvider()
  private val diagnosticProvider: JavaDiagnosticProvider?
  override var client: ILanguageClient? = null
    private set

  private var _settings: IServerSettings? = null
  private var selectedFile: Path? = null
  private val timer = AnalyzeTimer { analyzeSelected() }
  private var cachedCompletion: CachedCompletion

  val settings: IServerSettings
    get() {
      return _settings ?: JavaServerSettings.getInstance()
        .also { _settings = it }
    }

  override val serverId: String = SERVER_ID

  companion object {

    const val SERVER_ID = "ide.lsp.java"
    private val log = LoggerFactory.getLogger(JavaLanguageServer::class.java)
  }

  init {
    diagnosticProvider = JavaDiagnosticProvider()
    cachedCompletion = CachedCompletion.EMPTY

    applySettings(JavaServerSettings.getInstance())

    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this)
    }

    init()
  }

  override fun shutdown() {
    JavaCompilerProvider.getInstance().destroy()
    SourceFileManager.clearCache()
    CacheFSInfoSingleton.clearCache()
    clearCache()
    EventBus.getDefault().unregister(this)
    timer.cancel()
  }

  override fun connectClient(client: ILanguageClient?) {
    this.client = client
  }

  override fun applySettings(settings: IServerSettings?) {
    this._settings = settings
  }

  override fun setupWorkspace(workspace: IWorkspace) {
    LSPEditorActions.ensureActionsMenuRegistered(JavaCodeActionsMenu)

    // Once we have workspace initialized
    // Destory the NO_MODULE_COMPILER instance
    JavaCompilerService.NO_MODULE_COMPILER.destroy()

    // Clear cached file managers
    SourceFileManager.clearCache()

    // Clear cached JAR file system for R.jar
    // Using the cached instance will result in completions not being updated for updated resources
    // TODO Clearing caches for JAR files ending with '/R.jar' is probably not a good idea
    //    Maybe this could be improved by using data from the AndroidModule workspace model
    clearCachesForPaths { path: String -> path.endsWith("/R.jar") }

    // Clear cached module-specific compilers
    JavaCompilerProvider.getInstance().destroy()

    // Cache classpath locations
    for (subModule in workspace.getSubProjects()) {
      if (subModule !is ModuleProject || subModule.path == workspace.getRootProject().path) {
        continue
      }
      SourceFileManager.forModule(subModule)
    }
    startOrRestartAnalyzeTimer()
  }

  override fun complete(params: CompletionParams?): CompletionResult {
    val compiler = getCompiler(params!!.file)
    if (!settings.completionsEnabled() || !completionProvider.canComplete(params.file)
    ) {
      return CompletionResult.EMPTY
    }

    if (diagnosticProvider!!.isAnalyzing()) {
      log.warn("Cancelling source code analysis due to completion request")
      diagnosticProvider.cancel()
    }

    completionProvider.reset(
      compiler, settings, cachedCompletion
    ) { cachedCompletion: CachedCompletion ->
      updateCachedCompletion(cachedCompletion)
    }

    return completionProvider.complete(params)
  }

  override suspend fun findReferences(params: ReferenceParams): ReferenceResult {
    val compiler = getCompiler(params.file)
    return if (!settings.referencesEnabled()) {
      ReferenceResult(emptyList())
    } else ReferenceProvider(compiler, params.cancelChecker).findReferences(params)
  }

  override suspend fun findDefinition(params: DefinitionParams): DefinitionResult {
    val compiler = getCompiler(params.file)
    return if (!settings.definitionsEnabled()) {
      DefinitionResult(emptyList())
    } else DefinitionProvider(compiler, settings, params.cancelChecker).findDefinition(params)
  }

  override suspend fun expandSelection(params: ExpandSelectionParams): Range {
    val compiler = getCompiler(params.file)
    return if (!settings.smartSelectionsEnabled()) {
      params.selection
    } else JavaSelectionProvider(compiler).expandSelection(params)
  }

  override suspend fun signatureHelp(params: SignatureHelpParams): SignatureHelp {
    val compiler = getCompiler(params.file)
    return if (!settings.signatureHelpEnabled()) {
      SignatureHelp(emptyList(), -1, -1)
    } else SignatureProvider(compiler, params.cancelChecker).signatureHelp(params)
  }

  override suspend fun analyze(file: Path): DiagnosticResult {
    if (!settings.diagnosticsEnabled() || !DocumentUtils.isJavaFile(file)) {
      return DiagnosticResult.NO_UPDATE
    }

    return if (!settings.codeAnalysisEnabled()) {
      DiagnosticResult.NO_UPDATE
    } else diagnosticProvider!!.analyze(file)
  }

  override fun formatCode(params: FormatCodeParams?): CodeFormatResult {
    return CodeFormatProvider(settings).format(params)
  }

  override fun handleFailure(failure: LSPFailure?): Boolean {
    return when (failure!!.type) {
      FailureType.COMPLETION -> {
        if (isCancelled(failure.error)) {
          return true
        }
        JavaCompilerProvider.getInstance().destroy()
        true
      }
    }
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  fun getCompiler(file: Path?): JavaCompilerService {
    if (!DocumentUtils.isJavaFile(file)) {
      return JavaCompilerService.NO_MODULE_COMPILER
    }
    val workspace = getInstance().getWorkspace()
      ?: return JavaCompilerService.NO_MODULE_COMPILER
    val module = workspace.findModuleForFile(file!!) ?: return JavaCompilerService.NO_MODULE_COMPILER
    return JavaCompilerProvider.get(module)
  }

  private fun updateCachedCompletion(cachedCompletion: CachedCompletion) {
    Objects.requireNonNull(cachedCompletion)
    this.cachedCompletion = cachedCompletion
  }

  private fun startOrRestartAnalyzeTimer() {
    if (VMUtils.isJvm()) {
      return
    }
    if (!timer.isStarted) {
      timer.start()
    } else {
      timer.restart()
    }
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  @Suppress("unused")
  fun onContentChange(event: DocumentChangeEvent) {
    if (!DocumentUtils.isJavaFile(event.changedFile)) {
      return
    }

    // TODO Find an alternative to efficiently update changeDelta in JavaCompilerService instance
    JavaCompilerService.NO_MODULE_COMPILER.onDocumentChange(event)
    val module = getInstance()
      .getWorkspace()?.findModuleForFile(event.changedFile, true)
    if (module != null) {
      val compiler = JavaCompilerProvider.get(module)
      compiler.onDocumentChange(event)
    }
    startOrRestartAnalyzeTimer()
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  @Suppress("unused")
  fun onFileSelected(event: DocumentSelectedEvent) {
    selectedFile = event.selectedFile
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  @Suppress("unused")
  fun onFileOpened(event: DocumentOpenEvent) {
    selectedFile = event.openedFile
    startOrRestartAnalyzeTimer()
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  @Suppress("unused")
  fun onFileClosed(event: DocumentCloseEvent) {
    diagnosticProvider?.clearTimestamp(event.closedFile)

    if (getActiveDocumentCount() == 0) {
      selectedFile = null
      timer.cancel()
    }
  }

  private fun analyzeSelected() {
    if (selectedFile == null || client == null) {
      return
    }

    CoroutineScope(Dispatchers.Default).launch {
      val result = analyze(selectedFile!!)
      withContext(Dispatchers.Main) {
        client?.publishDiagnostics(result)
      }
    }
  }
}