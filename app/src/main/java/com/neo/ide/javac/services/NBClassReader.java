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

import static openjdk.tools.javac.jvm.ClassFile.Version.V45_3;

import android.text.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdkx.tools.ForwardingJavaFileObject;
import jdkx.tools.JavaFileObject;
import openjdk.tools.javac.code.ClassFinder.BadClassFile;
import openjdk.tools.javac.code.Symbol;
import openjdk.tools.javac.code.Symbol.ClassSymbol;
import openjdk.tools.javac.jvm.ClassFile;
import openjdk.tools.javac.jvm.ClassFile.Version;
import openjdk.tools.javac.jvm.ClassReader;
import openjdk.tools.javac.resources.CompilerProperties.Warnings;
import openjdk.tools.javac.util.Context;
import openjdk.tools.javac.util.Log;
import openjdk.tools.javac.util.Name;
import org.slf4j.LoggerFactory;

/**
 * @author lahvac
 */
public class NBClassReader extends ClassReader {

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NBClassReader.class);
  private final NBNames nbNames;
  private final Log log;

  public static void preRegister(Context context) {
    context.put(classReaderKey, (Context.Factory<ClassReader>) NBClassReader::new);
  }

  public NBClassReader(Context context) {
    super(context);

    nbNames = NBNames.instance(context);
    log = Log.instance(context);

    NBAttributeReader[] readers = {
        new NBAttributeReader(
            nbNames._org_netbeans_EnclosingMethod, V45_3, CLASS_OR_MEMBER_ATTRIBUTE) {
          public void read(Symbol sym, int attrLen) {
            int newbp = bp + attrLen;
            readEnclosingMethodAttr(sym);
            bp = newbp;
          }
        },
    };

    for (NBAttributeReader r : readers) {
      attributeReaders.put(r.getName(), r);
    }
  }

  @Override
  public BadClassFile badClassFile(final String key, final Object... args) {
    LOG.debug("Bad class file {} {}", key, TextUtils.join(", ", args), new RuntimeException());
    return super.badClassFile(key, args);
  }

  @Override
  public void readClassFile(ClassSymbol c) {
    try {
      super.readClassFile(c);
    } catch (BadClassFile cf) {
      if ("compiler.misc.bad.class.file.header".equals(cf.getDiagnostic().getCode())) {
        JavaFileObject origFile = c.classfile;
        try (InputStream in = origFile.openInputStream()) {
          byte[] data = readFile(in);
          int major = (Byte.toUnsignedInt(data[6]) << 8) + Byte.toUnsignedInt(data[7]);
          int maxMajor = ClassFile.Version.MAX().major;
          if (maxMajor < major) {
            if (log.currentSourceFile() != null) {
              log.warning(0, Warnings.BigMajorVersion(origFile, major, maxMajor));
            }
            data[6] = (byte) (maxMajor >> 8);
            data[7] = (byte) (maxMajor & 0xFF);
            c.classfile =
                new ForwardingJavaFileObject<JavaFileObject>(origFile) {
                  @Override
                  public InputStream openInputStream() {
                    return new ByteArrayInputStream(data);
                  }
                };
            super.readClassFile(c);
            return;
          }
        } catch (IOException ex) {
          Logger.getLogger(NBClassReader.class.getName()).log(Level.FINE, null, ex);
        } finally {
          c.classfile = origFile;
        }
      }
      throw cf;
    }
  }

  static byte[] readFile(final InputStream in) throws IOException {
    byte[] data = new byte[Math.max(in.available(), 256)];
    int off = 0;
    int read;
    while ((read = in.read(data, off, data.length - off)) != (-1)) {
      off += read;
      if (data.length == off) {
        data = Arrays.copyOf(data, 2 * (data.length + in.available()));
      }
    }
    return Arrays.copyOf(data, off);
  }

  private abstract class NBAttributeReader extends AttributeReader {

    private NBAttributeReader(Name name, Version version, Set<AttributeKind> kinds) {
      super(name, version, kinds);
    }

    private Name getName() {
      return name;
    }
  }
}
