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



package com.neo.ide.lsp.java.models;

import androidx.annotation.NonNull;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Configuration for the java language server.
 *
 * @author Akash Yadav
 */
public class JavaServerConfiguration {

  private Set<Path> classPaths;
  private Set<Path> sourceDirs;

  public JavaServerConfiguration() {
    this(Collections.emptySet(), Collections.emptySet());
  }

  public JavaServerConfiguration(Set<Path> classPaths, Set<Path> sourcePaths) {
    this.classPaths = classPaths;
    this.sourceDirs = sourcePaths;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClassPaths());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    JavaServerConfiguration that = (JavaServerConfiguration) o;
    return Objects.equals(getClassPaths(), that.getClassPaths());
  }

  @NonNull
  @Override
  public String toString() {
    return "JavaServerConfiguration{" + "classPaths=" + classPaths + '}';
  }

  public Set<Path> getClassPaths() {
    return classPaths;
  }

  public JavaServerConfiguration setClassPaths(Set<Path> classPaths) {
    this.classPaths = classPaths;
    return this;
  }

  public Set<Path> getSourceDirs() {
    return sourceDirs;
  }
}
