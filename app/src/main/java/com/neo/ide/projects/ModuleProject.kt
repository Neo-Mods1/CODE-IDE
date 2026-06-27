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

package com.neo.ide.projects

import android.text.TextUtils
import androidx.annotation.RestrictTo
import com.neo.ide.builder.model.IJavaCompilerSettings
import com.neo.ide.javac.services.fs.CacheFSInfoSingleton
import com.neo.ide.lookup.Lookup
import com.neo.ide.projects.android.AndroidModule
import com.neo.ide.projects.classpath.JarFsClasspathReader
import com.neo.ide.projects.util.BootClasspathProvider
import com.neo.ide.tooling.api.models.GradleTask
import com.neo.ide.utils.ClassTrie
import com.neo.ide.utils.DocumentUtils
import com.neo.ide.utils.SourceClassTrie
import com.neo.ide.utils.SourceClassTrie.SourceNode
import com.neo.ide.utils.StopWatch
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * A module project. Base class for [AndroidModule] and [JavaModule].
 *
 * @author Akash Yadav
 */
abstract class ModuleProject(
  name: String,
  description: String,
  path: String,
  projectDir: File,
  buildDir: File,
  buildScript: File,
  tasks: List<GradleTask>
) :
  GradleProject(name, description, path, projectDir, buildDir, buildScript, tasks) {

  abstract val compilerSettings: IJavaCompilerSettings

  companion object {

    private val log = LoggerFactory.getLogger(ModuleProject::class.java)

    @JvmStatic
    val COMPLETION_MODULE_KEY = Lookup.Key<ModuleProject>()
  }

  @JvmField
  val compileJavaSourceClasses = SourceClassTrie()

  @JvmField
  val compileClasspathClasses = ClassTrie()

  /**
   * Get the source directories of this module (non-transitive i.e for this module only).
   *
   * @return The source directories.
   */
  abstract fun getSourceDirectories(): Set<File>

  /**
   * Get the source directories with compile scope. This must include source directories of
   * transitive project dependencies and this module.
   *
   * @return The source directories.
   */
  abstract fun getCompileSourceDirectories(): Set<File>

  /**
   * Get the classpaths for this module project. The returned list always included the
   * `classes.jar`.
   */
  abstract fun getClassPaths(): Set<File>

  /**
   * Get the JAR files for this module. This does not include JAR files of any dependencies.
   *
   * @return The classpaths of this project.
   */
  abstract fun getModuleClasspaths(): Set<File>

  /**
   * Get the classpaths with compile scope. This must include classpaths of transitive project
   * dependencies as well. This includes classpaths for this module as well.
   *
   * @return The source directories.
   */
  abstract fun getCompileClasspaths(): Set<File>

  /**
   * Get the list of module projects with compile scope. This includes transitive module projects as
   * well.
   */
  abstract fun getCompileModuleProjects(): List<ModuleProject>

  /**
   * Find the source root of the given [file].
   *
   * @param file The file to find the source root for.
   * @return The source root (directory) of the given file, or `null` if not found.
   */
  fun findSourceRoot(file: File): Path? {
    return getCompileSourceDirectories().find { file.path.startsWith(it.path) }?.toPath()
  }

  /** Finds the source files and classes from source directories and classpaths and indexes them. */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
  fun indexSourcesAndClasspaths() {
    log.info("Indexing sources and classpaths for project: {}", path)
    indexSources()
    indexClasspaths()
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
  fun indexClasspaths() {

    this.compileClasspathClasses.clear()

    val watch = StopWatch("Indexing classpaths")
    val paths = getCompileClasspaths().filter { it.exists() }

    for (path in paths) {
      // Use 'getCanonicalFile' just to be sure that caches are stored with correct keys
      // See JavacFileManager.getContainer(Path) for more details
      CacheFSInfoSingleton.cache(CacheFSInfoSingleton.getCanonicalFile(path.toPath()))
    }

    val topLevelClasses = JarFsClasspathReader().listClasses(paths).filter { it.isTopLevel }
    topLevelClasses.forEach { this.compileClasspathClasses.append(it.name) }

    watch.log()
    log.debug("Found {} classpaths.", topLevelClasses.size)

    if (this is AndroidModule) {
      BootClasspathProvider.update(bootClassPaths.map { it.path })
    }
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
  fun indexSources() {

    this.compileJavaSourceClasses.clear()

    val watch = StopWatch("Indexing sources")
    var count = 0
    getCompileSourceDirectories().forEach {
      val sourceDir = it.toPath()
      it
        .walk()
        .filter { file -> file.isFile && file.exists() && DocumentUtils.isJavaFile(file.toPath()) }
        .map { file -> file.toPath() }
        .forEach { file ->
          this.compileJavaSourceClasses.append(file, sourceDir)
          count++
        }
    }

    watch.log()
    log.debug("Found {} source files.", count)
  }

  fun getSourceFilesInDir(dir: Path): List<SourceNode> =
    this.compileJavaSourceClasses.getSourceFilesInDir(dir)

  fun packageNameOrEmpty(file: Path?): String {
    if (file == null) {
      return ""
    }

    val sourceNode = searchSourceFileRelatively(file)
    if (sourceNode != null) {
      return sourceNode.packageName
    }

    return ""
  }

  private fun searchSourceFileRelatively(file: Path?): SourceNode? {
    for (source in getCompileSourceDirectories().map(File::toPath)) {
      val relative = source.relativize(file)
      if (relative.pathString.contains("..")) {
        // This is most probably not the one we're expecting
        continue
      }

      var name = relative.pathString.substringBeforeLast(".java")
      name = name.replace('/', '.')

      val node = this.compileJavaSourceClasses.findNode(name)
      if (node != null && node is SourceNode) {
        return node
      }
    }

    return null
  }

  fun suggestPackageName(file: Path): String {
    var dir = file.parent.normalize()
    while (dir != null) {
      for (sibling in getSourceFilesInDir(dir)) {
        if (DocumentUtils.isSameFile(sibling.file, file)) {
          continue
        }
        var packageName: String = packageNameOrEmpty(sibling.file)
        if (TextUtils.isEmpty(packageName.trim { it <= ' ' })) {
          continue
        }
        val relativePath = dir.relativize(file.parent)
        val relativePackage = relativePath.toString().replace(File.separatorChar, '.')
        if (relativePackage.isNotEmpty()) {
          packageName = "$packageName.$relativePackage"
        }
        return packageName
      }
      dir = dir.parent.normalize()
    }
    return ""
  }

  fun listClassesFromSourceDirs(packageName: String): List<SourceNode> {
    return compileJavaSourceClasses
      .findInPackage(packageName)
      .filterIsInstance(SourceNode::class.java)
  }

  open fun isFromThisModule(file: File): Boolean {
    return isFromThisModule(file.toPath())
  }

  open fun isFromThisModule(file: Path): Boolean {
    // TODO This can be probably improved
    return file.startsWith(this.projectDir.toPath())
  }

  override fun toString() = "${javaClass.simpleName}: ${this.path}"
}
