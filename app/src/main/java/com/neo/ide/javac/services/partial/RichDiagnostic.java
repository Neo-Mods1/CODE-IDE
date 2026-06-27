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



package com.neo.ide.javac.services.partial;

import java.util.Locale;
import jdkx.tools.Diagnostic;
import openjdk.tools.javac.api.DiagnosticFormatter;
import openjdk.tools.javac.util.JCDiagnostic;

/**
 * @author Akash Yadav
 */
public class RichDiagnostic implements Diagnostic {

  private final JCDiagnostic delegate;
  private final DiagnosticFormatter<JCDiagnostic> formatter;

  public RichDiagnostic(JCDiagnostic delegate, DiagnosticFormatter<JCDiagnostic> formatter) {
    this.delegate = delegate;
    this.formatter = formatter;
  }

  @Override
  public Diagnostic.Kind getKind() {
    return delegate.getKind();
  }

  @Override
  public Object getSource() {
    return delegate.getSource();
  }

  @Override
  public long getPosition() {
    return delegate.getPosition();
  }

  @Override
  public long getStartPosition() {
    return delegate.getStartPosition();
  }

  @Override
  public long getEndPosition() {
    return delegate.getEndPosition();
  }

  @Override
  public long getLineNumber() {
    return delegate.getLineNumber();
  }

  @Override
  public long getColumnNumber() {
    return delegate.getColumnNumber();
  }

  @Override
  public String getCode() {
    return delegate.getCode();
  }

  @Override
  public String getMessage(Locale locale) {
    return formatter.format(delegate, locale);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  JCDiagnostic getDelegate() {
    return delegate;
  }

  public static Diagnostic wrap(Diagnostic d, DiagnosticFormatter<JCDiagnostic> df) {
    if (d instanceof JCDiagnostic) {
      return new RichDiagnostic((JCDiagnostic) d, df);
    } else {
      return d;
    }
  }
}
