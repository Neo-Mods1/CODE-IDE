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



package com.neo.ide.lsp.java.providers;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.neo.ide.lsp.api.IServerSettings;
import com.neo.ide.lsp.java.compiler.JavaCompilerService;
import com.neo.ide.lsp.java.compiler.SynchronizedTask;
import com.neo.ide.lsp.java.providers.definition.ErroneousDefinitionProvider;
import com.neo.ide.lsp.java.providers.definition.IJavaDefinitionProvider;
import com.neo.ide.lsp.java.providers.definition.LocalDefinitionProvider;
import com.neo.ide.lsp.java.providers.definition.RemoteDefinitionProvider;
import com.neo.ide.lsp.java.utils.NavigationHelper;
import com.neo.ide.lsp.models.DefinitionParams;
import com.neo.ide.lsp.models.DefinitionResult;
import com.neo.ide.models.Location;
import com.neo.ide.models.Position;
import com.neo.ide.progress.ICancelChecker;
import com.neo.ide.utils.DocumentUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import jdkx.lang.model.element.Element;
import jdkx.lang.model.element.TypeElement;
import jdkx.lang.model.type.TypeKind;
import jdkx.tools.JavaFileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefinitionProvider extends CancelableServiceProvider {

  public static final List<Location> NOT_SUPPORTED = Collections.emptyList();
  private static final Logger LOG = LoggerFactory.getLogger(DefinitionProvider.class);
  private final JavaCompilerService compiler;
  private final IServerSettings settings;
  private Path file;
  private Position position;
  private int line, column;

  public DefinitionProvider(JavaCompilerService compiler, IServerSettings settings,
      ICancelChecker cancelChecker) {
    super(cancelChecker);
    this.compiler = compiler;
    this.settings = settings;
  }

  @NonNull
  public DefinitionResult findDefinition(@NonNull DefinitionParams params) {
    this.file = params.getFile();

    // 1-based line and column index
    this.line = params.getPosition().getLine() + 1;
    this.column = params.getPosition().getColumn() + 1;
    this.position = new Position(this.line, this.column);
    final List<Location> locations = findDefinition();

    LOG.debug("Found {} definitions...", locations.size());
    return new DefinitionResult(locations);
  }

  public List<Location> findDefinition() {
    abortIfCancelled();
    final SynchronizedTask compile = compiler.compile(file);
    abortIfCancelled();
    final Element element =
        compile.get(task -> NavigationHelper.findElement(task, file, line, column, this));

    if (element == null) {
      LOG.error("Cannot find element at line: {} and column: {}", line, column);
      return NOT_SUPPORTED;
    }

    IJavaDefinitionProvider provider = null;

    if (element.asType().getKind() == TypeKind.ERROR) {
      provider = new ErroneousDefinitionProvider(position, file, compiler, settings, this);
    } else if (NavigationHelper.isLocal(element)) {
      provider = new LocalDefinitionProvider(position, file, compiler, settings, this);
    }

    if (provider == null) {
      final String className = className(element);
      if (TextUtils.isEmpty(className)) {
        LOG.error("No class name found for element: {}", element);
        return NOT_SUPPORTED;
      }

      final Optional<JavaFileObject> optional = compiler.findAnywhere(className);
      if (!optional.isPresent()) {
        LOG.error("Cannot find source file for class: {}", className);
        return NOT_SUPPORTED;
      }

      final JavaFileObject jfo = optional.get();
      if (DocumentUtils.isSameFile(Paths.get(jfo.toUri()), file)) {
        provider = new LocalDefinitionProvider(position, file, compiler, settings, this);
      } else {
        provider =
            new RemoteDefinitionProvider(position, file, compiler, settings, this).setOtherFile(jfo);
      }
    }

    return provider.findDefinition(element);
  }

  private String className(Element element) {
    while (element != null) {
      abortIfCancelled();
      if (element instanceof TypeElement) {
        TypeElement type = (TypeElement) element;
        return type.getQualifiedName().toString();
      }
      element = element.getEnclosingElement();
    }
    return "";
  }
}
