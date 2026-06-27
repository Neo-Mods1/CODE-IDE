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

import openjdk.tools.javac.code.Symtab;
import openjdk.tools.javac.code.Types;
import openjdk.tools.javac.tree.JCTree.JCCompilationUnit;
import openjdk.tools.javac.tree.TreeMaker;
import openjdk.tools.javac.util.Context;
import openjdk.tools.javac.util.Names;

/**
 * @author lahvac
 */
public class NBTreeMaker extends TreeMaker {

  private final Names names;
  private final Types types;
  private final Symtab syms;

  protected NBTreeMaker(JCCompilationUnit toplevel, Names names, Types types, Symtab syms) {
    super(toplevel, names, types, syms);
    this.names = names;
    this.types = types;
    this.syms = syms;
  }

  public static void preRegister(Context context) {
    context.put(treeMakerKey, (Context.Factory<TreeMaker>) NBTreeMaker::new);
  }

  protected NBTreeMaker(Context context) {
    super(context);
    this.names = Names.instance(context);
    this.types = Types.instance(context);
    this.syms = Symtab.instance(context);
  }

  @Override
  public TreeMaker forToplevel(JCCompilationUnit toplevel) {
    return new NBTreeMaker(toplevel, names, types, syms);
  }
}
