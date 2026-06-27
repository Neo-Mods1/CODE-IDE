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

import java.util.HashMap;
import java.util.Map;
import jdkx.lang.model.element.Element;
import openjdk.source.tree.VariableTree;
import openjdk.source.util.TreePath;
import openjdk.tools.javac.api.JavacTrees;
import openjdk.tools.javac.code.Flags;
import openjdk.tools.javac.code.Symbol;
import openjdk.tools.javac.tree.JCTree;
import openjdk.tools.javac.tree.JCTree.JCVariableDecl;
import openjdk.tools.javac.tree.TreeInfo;
import openjdk.tools.javac.tree.TreeMaker;
import openjdk.tools.javac.util.Context;

/**
 * @author lahvac
 */
public class NBJavacTrees extends JavacTrees {

  private final Map<Element, TreePath> element2paths = new HashMap<>();

  public static void preRegister(Context context) {
    context.put(JavacTrees.class, (Context.Factory<JavacTrees>) NBJavacTrees::new);
  }

  protected NBJavacTrees(Context context) {
    super(context);
  }

  @Override
  public TreePath getPath(Element e) {
    TreePath path = super.getPath(e);
    return path != null ? path : element2paths.get(e);
  }

  @Override
  public Symbol getElement(TreePath path) {
    return TreeInfo.symbolFor((JCTree) path.getLeaf());
  }

  @Override
  protected Copier createCopier(TreeMaker maker) {
    return new Copier(maker) {
      @Override
      public JCTree visitVariable(VariableTree node, JCTree p) {
        JCVariableDecl old = (JCVariableDecl) node;
        JCVariableDecl nue = (JCVariableDecl) super.visitVariable(node, p);
        if (old.sym != null) {
          nue.mods.flags |= old.sym.flags_field & Flags.EFFECTIVELY_FINAL;
        }
        return nue;
      }
    };
  }

  void addPathForElement(Element elem, TreePath path) {
    element2paths.put(elem, path);
  }
}
