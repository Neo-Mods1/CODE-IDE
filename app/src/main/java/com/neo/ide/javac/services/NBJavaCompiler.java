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



/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.neo.ide.javac.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.function.Consumer;
import openjdk.tools.javac.comp.AttrContext;
import openjdk.tools.javac.comp.Env;
import openjdk.tools.javac.main.JavaCompiler;
import openjdk.tools.javac.tree.JCTree;
import openjdk.tools.javac.util.Context;
import openjdk.tools.javac.util.List;
import openjdk.tools.javac.util.Pair;

/**
 * @author lahvac
 */
public class NBJavaCompiler extends JavaCompiler {

  private final CancelService cancelService;
  private Consumer<Env<AttrContext>> desugarCallback;
  private boolean desugaring;

  public static void preRegister(Context context) {
    context.put(compilerKey, (Context.Factory<JavaCompiler>) NBJavaCompiler::new);
  }

  public NBJavaCompiler(Context context) {
    super(context);
    cancelService = CancelService.instance(context);
  }

  @Override
  public void processAnnotations(
      List<JCTree.JCCompilationUnit> roots, Collection<String> classnames) {
    if (roots.isEmpty()) {
      super.processAnnotations(roots, classnames);
    } else {
      setOrigin(roots.head.sourcefile.toUri().toString());
      try {
        super.processAnnotations(roots, classnames);
      } finally {
        setOrigin("");
      }
    }
  }

  private void setOrigin(String origin) {
    fileManager.handleOption("apt-origin", Collections.singletonList(origin).iterator());
  }

  @Override
  protected void desugar(
      Env<AttrContext> env, Queue<Pair<Env<AttrContext>, JCTree.JCClassDecl>> results) {
    boolean prevDesugaring = desugaring;
    try {
      desugaring = true;
      super.desugar(env, results);
    } finally {
      desugaring = prevDesugaring;
    }
  }

  public void setDesugarCallback(Consumer<Env<AttrContext>> callback) {
    this.desugarCallback = callback;
  }

  void maybeInvokeDesugarCallback(Env<AttrContext> env) {
    if (desugaring && desugarCallback != null) {
      desugarCallback.accept(env);
    }
  }
}
