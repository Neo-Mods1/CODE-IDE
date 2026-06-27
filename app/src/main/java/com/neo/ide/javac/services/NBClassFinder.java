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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdkx.tools.JavaFileObject;
import openjdk.tools.javac.code.ClassFinder;
import openjdk.tools.javac.code.Flags;
import openjdk.tools.javac.code.Kinds.Kind;
import openjdk.tools.javac.code.Symbol;
import openjdk.tools.javac.code.Symbol.Completer;
import openjdk.tools.javac.code.Symbol.CompletionFailure;
import openjdk.tools.javac.util.Context;
import openjdk.tools.javac.util.JCDiagnostic;
import openjdk.tools.javac.util.JCDiagnostic.DiagnosticInfo;
import openjdk.tools.javac.util.JCDiagnostic.DiagnosticType;
import openjdk.tools.javac.util.JCDiagnostic.SimpleDiagnosticPosition;
import openjdk.tools.javac.util.Log;
import openjdk.tools.javac.util.Names;

/**
 * @author lahvac
 */
public class NBClassFinder extends ClassFinder {

  private final Context context;
  private final Names names;
  private final JCDiagnostic.Factory diagFactory;
  private final Log log;
  private Completer completer;

  public static void preRegister(Context context) {
    context.put(classFinderKey, (Context.Factory<ClassFinder>) NBClassFinder::new);
  }

  public NBClassFinder(Context context) {
    super(context);
    this.context = context;
    this.names = Names.instance(context);
    this.diagFactory = JCDiagnostic.Factory.instance(context);
    this.log = Log.instance(context);
  }

  @Override
  public Completer getCompleter() {
    if (completer == null) {
      try {
        Class.forName("openjdk.tools.javac.model.LazyTreeLoader");
        // patched nb-javac, handles missing java.lang itself:
        completer = super.getCompleter();
      } catch (ClassNotFoundException e) {
        Completer delegate = super.getCompleter();
        completer =
            sym -> {
              delegate.complete(sym);
              if (sym.kind == Kind.PCK
                  && sym.flatName() == names.java_lang
                  && sym.members().isEmpty()) {
                sym.flags_field |= Flags.EXISTS;
                try {
                  Class<?> dcfhClass =
                      Class.forName("openjdk.tools.javac.code.DeferredCompletionFailureHandler");
                  Constructor<CompletionFailure> constr =
                      CompletionFailure.class.getDeclaredConstructor(
                          Symbol.class, Supplier.class, dcfhClass);
                  Object dcfh =
                      dcfhClass.getDeclaredMethod("instance", Context.class).invoke(null, context);
                  throw constr.newInstance(
                      sym,
                      (Supplier<JCDiagnostic>)
                          () ->
                              diagFactory.create(
                                  log.currentSource(),
                                  new SimpleDiagnosticPosition(0),
                                  DiagnosticInfo.of(
                                      DiagnosticType.ERROR,
                                      "compiler",
                                      "cant.resolve",
                                      "package",
                                      "java.lang")),
                      dcfh);
                } catch (ClassNotFoundException
                    | NoSuchMethodException
                    | SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException
                    | InstantiationException ex) {
                  Logger.getLogger(NBClassFinder.class.getName()).log(Level.FINE, null, ex);
                }
              }
            };
      }
    }
    return completer;
  }

  @Override
  protected JavaFileObject preferredFileObject(JavaFileObject a, JavaFileObject b) {
    if (b.getName().toLowerCase(Locale.ROOT).endsWith(".sig")) {
      // do not prefer sources over sig files (unless sources are newer):
      boolean prevPreferSource = preferSource;
      try {
        preferSource = false;
        return super.preferredFileObject(a, b);
      } finally {
        preferSource = prevPreferSource;
      }
    }
    return super.preferredFileObject(a, b);
  }
}
