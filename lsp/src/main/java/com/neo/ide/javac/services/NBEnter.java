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

import openjdk.tools.javac.code.Symbol.TypeSymbol;
import openjdk.tools.javac.code.Symtab;
import openjdk.tools.javac.comp.AttrContext;
import openjdk.tools.javac.comp.Enter;
import openjdk.tools.javac.comp.Env;
import openjdk.tools.javac.main.JavaCompiler;
import openjdk.tools.javac.tree.JCTree;
import openjdk.tools.javac.tree.JCTree.JCClassDecl;
import openjdk.tools.javac.tree.TreeInfo;
import openjdk.tools.javac.util.Context;

/**
 * @author lahvac
 */
public class NBEnter extends Enter {

  private final CancelService cancelService;
  private final Symtab syms;
  private final NBJavaCompiler compiler;

  public static void preRegister(Context context) {
    context.put(enterKey, (Context.Factory<Enter>) NBEnter::new);
  }

  public NBEnter(Context context) {
    super(context);
    cancelService = CancelService.instance(context);
    syms = Symtab.instance(context);
    JavaCompiler c = JavaCompiler.instance(context);
    compiler = c instanceof NBJavaCompiler ? (NBJavaCompiler) c : null;
  }

  @SuppressWarnings("unused")
  public void doUnenter(JCTree.JCCompilationUnit cu, JCTree tree) {
    super.unenter(cu, tree);
  }

  @Override
  public Env<AttrContext> getEnv(TypeSymbol sym) {
    Env<AttrContext> env = super.getEnv(sym);
    if (compiler != null) {
      compiler.maybeInvokeDesugarCallback(env);
    }
    return env;
  }

  @Override
  public void visitTopLevel(JCTree.JCCompilationUnit tree) {
    if (TreeInfo.isModuleInfo(tree) && tree.modle == syms.noModule) {
      // workaround: when source level == 8, then visitTopLevel crashes for module-info.java
      return;
    }
    super.visitTopLevel(tree);
  }

  @Override
  public void visitClassDef(JCClassDecl tree) {
    cancelService.abortIfCanceled();
    super.visitClassDef(tree);
  }
}
