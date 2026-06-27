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



package com.neo.ide.lsp.java;

import androidx.annotation.NonNull;
import com.neo.ide.lsp.java.compiler.JavaCompilerService;
import com.neo.ide.projects.ModuleProject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides {@link JavaCompilerService} instances for different {@link ModuleProject}s.
 *
 * @author Akash Yadav
 */
public class JavaCompilerProvider {

  private static JavaCompilerProvider sInstance;
  private final Map<ModuleProject, JavaCompilerService> mCompilers = new ConcurrentHashMap<>();

  private JavaCompilerProvider() {}

  @NonNull
  public static JavaCompilerService get(ModuleProject module) {
    return JavaCompilerProvider.getInstance().forModule(module);
  }

  public static JavaCompilerProvider getInstance() {
    if (sInstance == null) {
      sInstance = new JavaCompilerProvider();
    }

    return sInstance;
  }

  @NonNull
  public synchronized JavaCompilerService forModule(ModuleProject module) {
    // A module instance is set to the compiler only in case the project is initialized or
    // this method was called with other mdoule instance.
    final JavaCompilerService cached = mCompilers.get(module);
    if (cached != null && cached.getModule() != null) {
      return cached;
    }

    final JavaCompilerService newInstance = new JavaCompilerService(module);
    mCompilers.put(module, newInstance);

    return newInstance;
  }

  // TODO This currently destroys all the compiler instances
  //  We must have a method to destroy only the required instance in
  //  JavaLanguageServer.handleFailure(LSPFailure)
  public synchronized void destroy() {
    for (final JavaCompilerService compiler : mCompilers.values()) {
      compiler.destroy();
    }
    mCompilers.clear();
  }
}
