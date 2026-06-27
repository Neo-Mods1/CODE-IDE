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



package com.neo.ide.lsp.java.compiler;

import com.neo.ide.lsp.java.models.CompilationRequest;
import com.neo.ide.lsp.java.parser.ParseTask;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import jdkx.tools.JavaFileObject;

public interface CompilerProvider {
  Path NOT_FOUND = Paths.get("");

  TreeSet<String> publicTopLevelTypes();

  TreeSet<String> packagePrivateTopLevelTypes(String packageName);

  Optional<JavaFileObject> findAnywhere(String className);

  Path findTypeDeclaration(String className);

  Path[] findTypeReferences(String className);

  Path[] findMemberReferences(String className, String memberName);

  default List<String> findQualifiedNames(String simpleName) {
    return findQualifiedNames(simpleName, false);
  }

  List<String> findQualifiedNames(String simpleName, boolean onlyOne);

  ParseTask parse(Path file);

  ParseTask parse(JavaFileObject file);

  default SynchronizedTask compile(Path... files) {
    return compile(Arrays.stream(files).map(SourceFileObject::new).collect(Collectors.toList()));
  }

  default SynchronizedTask compile(Collection<? extends JavaFileObject> sources) {
    return compile(new CompilationRequest(sources));
  }

  SynchronizedTask compile(CompilationRequest request);
}
