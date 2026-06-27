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

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import jdkx.tools.Diagnostic;
import jdkx.tools.DiagnosticListener;
import jdkx.tools.JavaFileObject;
import openjdk.tools.javac.api.ClientCodeWrapper;
import openjdk.tools.javac.util.JCDiagnostic;

/**
 * @author Akash Yadav
 */
public class DiagnosticListenerImpl implements DiagnosticListener<JavaFileObject> {

  private final Map<JavaFileObject, Diagnostics> source2Errors;
  private final JavaFileObject jfo;
  private volatile List<Diagnostic<? extends JavaFileObject>> partialReparseErrors;
  /** true if the partialReparseErrors contain some non-warning */
  private volatile boolean partialReparseRealErrors;

  private volatile List<Diagnostic<? extends JavaFileObject>> affectedErrors;
  private volatile int currentDelta;

  public DiagnosticListenerImpl(@Nullable final JavaFileObject jfo) {
    this.jfo = jfo;
    this.source2Errors = new HashMap<>();
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> message) {
    if (partialReparseErrors != null) {
      if (this.jfo != null && this.jfo == message.getSource()) {
        partialReparseErrors.add(message);
        if (message.getKind() == Diagnostic.Kind.ERROR) {
          partialReparseRealErrors = true;
        }
      }
    } else {
      Diagnostics errors = getErrors(message.getSource());
      errors.add((int) message.getPosition(), message);
    }
  }

  private Diagnostics getErrors(JavaFileObject file) {
    Diagnostics errors;
    if (isIncompleteClassPath()) {
      //        if (root != null && JavaIndex.hasSourceCache(root.toURL(), false)) {
      //          errors = source2Errors.get(file);
      //          if (errors == null) {
      //            source2Errors.put(file, errors = new Diagnostics());
      //            if (this.jfo != null && this.jfo == file) {
      //              errors.add(0, new IncompleteClassPath(this.jfo));
      //            }
      //          }
      //        } else {
      errors = new Diagnostics();
      if (this.jfo != null && this.jfo == file) {
        errors.add(0, new IncompleteClassPath(this.jfo));
        //          }
      }
    } else {
      errors = source2Errors.get(file);
      if (errors == null) {
        source2Errors.put(file, errors = new Diagnostics());
      }
    }
    return errors;
  }

  private boolean isIncompleteClassPath() {
    return false;
  }

  public final boolean hasPartialReparseErrors() {
    return this.partialReparseErrors != null && partialReparseRealErrors;
  }

  public final void startPartialReparse(int from, int to) {
    if (partialReparseErrors == null) {
      partialReparseErrors = new ArrayList<>();
      Diagnostics errors = getErrors(jfo);
      SortedMap<Integer, Collection<DiagNode>> subMap = errors.subMap(from, to);
      subMap.values().forEach(value -> value.forEach(errors::unlink));
      subMap.clear(); // Remove errors in changed method durring the partial reparse
      Map<Integer, Collection<DiagNode>> tail = errors.tailMap(to);
      this.affectedErrors = new ArrayList<>(tail.size());
      HashSet<DiagNode> tailNodes = new HashSet<>();
      for (Iterator<Map.Entry<Integer, Collection<DiagNode>>> it = tail.entrySet().iterator();
          it.hasNext(); ) {
        tailNodes.addAll(it.next().getValue());
        it.remove();
      }
      DiagNode node = errors.first;
      while (node != null) {
        if (tailNodes.contains(node)) {
          errors.unlink(node);
          final JCDiagnostic diagnostic;
          if (node.diag instanceof ClientCodeWrapper.DiagnosticSourceUnwrapper) {
            diagnostic = ((ClientCodeWrapper.DiagnosticSourceUnwrapper) node.diag).d;
          } else {
            diagnostic = (JCDiagnostic) node.diag;
          }
          if (diagnostic == null) {
            throw new IllegalStateException(
                "diagnostic == null " + mapArraysToLists(Thread.getAllStackTraces())); // NOI18N
          }
          this.affectedErrors.add(new D(diagnostic));
        }
        node = node.next;
      }
    } else {
      this.partialReparseErrors.clear();
    }
    partialReparseRealErrors = false;
  }

  private static <A, B> Map<A, List<B>> mapArraysToLists(final Map<? extends A, B[]> map) {
    final Map<A, List<B>> result = new HashMap<>();
    for (Map.Entry<? extends A, B[]> entry : map.entrySet()) {
      result.put(entry.getKey(), Arrays.asList(entry.getValue()));
    }
    return result;
  }

  public final void endPartialReparse(final int delta) {
    this.currentDelta += delta;
  }

  private static final class D implements Diagnostic {

    private final JCDiagnostic delegate;

    public D(final JCDiagnostic delegate) {
      assert delegate != null;
      this.delegate = delegate;
    }

    @Override
    public Kind getKind() {
      return this.delegate.getKind();
    }

    @Override
    public Object getSource() {
      return this.delegate.getSource();
    }

    @Override
    public long getPosition() {
      return this.delegate.getPosition();
    }

    @Override
    public long getStartPosition() {
      return this.delegate.getStartPosition();
    }

    @Override
    public long getEndPosition() {
      return this.delegate.getEndPosition();
    }

    @Override
    public long getLineNumber() {
      return -1;
    }

    @Override
    public long getColumnNumber() {
      return -1;
    }

    @Override
    public String getCode() {
      return this.delegate.getCode();
    }

    @Override
    public String getMessage(Locale locale) {
      return this.delegate.getMessage(locale);
    }
  }

  private static final class IncompleteClassPath implements Diagnostic<JavaFileObject> {

    private final JavaFileObject file;

    IncompleteClassPath(final JavaFileObject file) {
      this.file = file;
    }

    @Override
    public Kind getKind() {
      return Kind.WARNING;
    }

    @Override
    public JavaFileObject getSource() {
      return file;
    }

    @Override
    public long getPosition() {
      return 0;
    }

    @Override
    public long getStartPosition() {
      return getPosition();
    }

    @Override
    public long getEndPosition() {
      return getPosition();
    }

    @Override
    public long getLineNumber() {
      return getPosition();
    }

    @Override
    public long getColumnNumber() {
      return getPosition();
    }

    @Override
    public String getCode() {
      return "nb.classpath.incomplete"; // NOI18N
    }

    @Override
    public String getMessage(Locale locale) {
      return "Incomplete classpath";
    }
  }

  private static final class Diagnostics extends TreeMap<Integer, Collection<DiagNode>> {
    private DiagNode first;
    private DiagNode last;

    public void add(int pos, Diagnostic<? extends JavaFileObject> diag) {
      Collection<DiagNode> nodes = get((int) diag.getPosition());
      if (nodes == null) {
        put((int) diag.getPosition(), nodes = new ArrayList<>());
      }
      DiagNode node = new DiagNode(last, diag, null);
      nodes.add(node);
      if (last != null) {
        last.next = node;
      }
      last = node;
      if (first == null) {
        first = node;
      }
    }

    private void unlink(DiagNode node) {
      if (node.next == null) {
        last = node.prev;
      } else {
        node.next.prev = node.prev;
      }
      if (node.prev == null) {
        first = node.next;
      } else {
        node.prev.next = node.next;
      }
    }
  }

  private static final class DiagNode {
    private final Diagnostic<? extends JavaFileObject> diag;
    private DiagNode next;
    private DiagNode prev;

    private DiagNode(DiagNode prev, Diagnostic<? extends JavaFileObject> diag, DiagNode next) {
      this.diag = diag;
      this.next = next;
      this.prev = prev;
    }
  }
}
