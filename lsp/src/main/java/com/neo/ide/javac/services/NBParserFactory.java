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

import openjdk.tools.javac.parser.JavacParser;
import openjdk.tools.javac.parser.Lexer;
import openjdk.tools.javac.parser.ParserFactory;
import openjdk.tools.javac.parser.ScannerFactory;
import openjdk.tools.javac.parser.Tokens.Comment;
import openjdk.tools.javac.tree.JCTree;
import openjdk.tools.javac.tree.JCTree.JCClassDecl;
import openjdk.tools.javac.tree.JCTree.JCEnhancedForLoop;
import openjdk.tools.javac.tree.JCTree.JCExpression;
import openjdk.tools.javac.tree.JCTree.JCModifiers;
import openjdk.tools.javac.tree.JCTree.JCStatement;
import openjdk.tools.javac.tree.JCTree.JCTypeParameter;
import openjdk.tools.javac.tree.TreeInfo;
import openjdk.tools.javac.util.Context;
import openjdk.tools.javac.util.List;
import openjdk.tools.javac.util.Name;
import openjdk.tools.javac.util.Names;
import openjdk.tools.javac.util.Options;
import openjdk.tools.javac.util.Position;

/**
 * @author lahvac
 */
public class NBParserFactory extends ParserFactory {

  public static final String KEEP_COMMENTS_OVERRIDE = "keepCommentsOverride";
  public static final String KEEP_COMMENTS_OVERRIDE_KEEP = "keep";
  public static final String KEEP_COMMENTS_OVERRIDE_IGNORE = "ignore";

  protected final ScannerFactory scannerFactory;
  protected final Names names;
  protected final CancelService cancelService;

  protected final String keepCommentsOverride;

  public static void preRegister(Context context) {
    context.put(parserFactoryKey, (Context.Factory<ParserFactory>) NBParserFactory::new);
  }

  protected NBParserFactory(Context context) {
    super(context);
    this.scannerFactory = ScannerFactory.instance(context);
    this.names = Names.instance(context);
    this.cancelService = CancelService.instance(context);
    this.keepCommentsOverride = Options.instance(context).get(KEEP_COMMENTS_OVERRIDE);

  }

  @Override
  public JavacParser newParser(
      CharSequence input,
      boolean keepDocComments,
      boolean keepEndPos,
      boolean keepLineMap,
      boolean parseModuleInfo) {
    var keepDocCommentsOverride = keepDocComments;
    if (this.keepCommentsOverride != null) {
      if (KEEP_COMMENTS_OVERRIDE_IGNORE.equals(this.keepCommentsOverride)) {
        keepDocCommentsOverride = false;
      } else if (KEEP_COMMENTS_OVERRIDE_KEEP.equals(this.keepCommentsOverride)) {
        keepDocCommentsOverride = true;
      }
    }

    Lexer lexer = scannerFactory.newScanner(input, keepDocCommentsOverride);
    return new NBJavacParser(
        this, lexer, keepDocCommentsOverride, keepLineMap, keepEndPos, parseModuleInfo,
        cancelService);
  }

  public static class NBJavacParser extends JavacParser {

    private final Names names;
    private final CancelService cancelService;

    public NBJavacParser(
        NBParserFactory fac,
        Lexer S,
        boolean keepDocComments,
        boolean keepLineMap,
        boolean keepEndPos,
        boolean parseModuleInfo,
        CancelService cancelService) {
      super(fac, S, keepDocComments, keepLineMap, keepEndPos, parseModuleInfo);
      this.names = fac.names;
      this.cancelService = cancelService;
    }

    @Override
    protected AbstractEndPosTable newEndPosTable(boolean keepEndPositions) {
      AbstractEndPosTable res = super.newEndPosTable(keepEndPositions);

      if (keepEndPositions) {
        return new EndPosTableImpl(S, this, (SimpleEndPosTable) res);
      }

      return res;
    }

    @Override
    public int getEndPos(JCTree jctree) {
      return TreeInfo.getEndPos(jctree, endPosTable);
    }

    @Override
    public JCStatement parseSimpleStatement() {
      JCStatement result = super.parseSimpleStatement();
      // workaround: if the code looks like:
      // for (name : <collection>) {...}
      // the "name" will be made a type of a variable with name "<error>", with
      // no end position. Inject the end position for the variable:
      if (result instanceof JCEnhancedForLoop) {
        JCEnhancedForLoop tree = (JCEnhancedForLoop) result;
        if (getEndPos(tree.var) == Position.NOPOS) {
          endPosTable.storeEnd(tree.var, getEndPos(tree.var.vartype));
        }
      }
      return result;
    }

    @Override
    protected JCClassDecl classDeclaration(JCModifiers mods, Comment dc) {
      if (cancelService != null) {
        cancelService.abortIfCanceled();
      }
      return super.classDeclaration(mods, dc);
    }

    @Override
    protected JCClassDecl interfaceDeclaration(JCModifiers mods, Comment dc) {
      if (cancelService != null) {
        cancelService.abortIfCanceled();
      }
      return super.interfaceDeclaration(mods, dc);
    }

    @Override
    protected JCClassDecl enumDeclaration(JCModifiers mods, Comment dc) {
      if (cancelService != null) {
        cancelService.abortIfCanceled();
      }
      return super.enumDeclaration(mods, dc);
    }

    @Override
    protected JCTree methodDeclaratorRest(
        int pos,
        JCModifiers mods,
        JCExpression type,
        Name name,
        List<JCTypeParameter> typarams,
        boolean isInterface,
        boolean isVoid,
        boolean isRecord,
        Comment dc) {
      if (cancelService != null) {
        cancelService.abortIfCanceled();
      }
      return super.methodDeclaratorRest(
          pos, mods, type, name, typarams, isInterface, isVoid, isRecord, dc);
    }

    public final class EndPosTableImpl extends AbstractEndPosTable {

      private final Lexer lexer;
      private final SimpleEndPosTable delegate;

      private EndPosTableImpl(Lexer lexer, JavacParser parser, SimpleEndPosTable delegate) {
        super(parser);
        this.lexer = lexer;
        this.delegate = delegate;
      }

      public void resetErrorEndPos() {
        delegate.errorEndPos = Position.NOPOS;
        errorEndPos = delegate.errorEndPos;
      }

      @Override
      public int getEndPos(JCTree jctree) {
        return delegate.getEndPos(jctree);
      }

      @Override
      public void storeEnd(JCTree tree, int endpos) {
        if (endpos >= 0) {
          delegate.storeEnd(tree, endpos);
        }
      }

      @Override
      public int replaceTree(JCTree jctree, JCTree jctree1) {
        return delegate.replaceTree(jctree, jctree1);
      }

      @Override
      protected <T extends JCTree> T to(T t) {
        storeEnd(t, parser.token().endPos);
        return t;
      }

      @Override
      protected <T extends JCTree> T toP(T t) {
        storeEnd(t, lexer.prevToken().endPos);
        return t;
      }

      @Override
      public void setErrorEndPos(int errPos) {
        delegate.setErrorEndPos(errPos);
        errorEndPos = delegate.errorEndPos;
      }
    }
  }
}
