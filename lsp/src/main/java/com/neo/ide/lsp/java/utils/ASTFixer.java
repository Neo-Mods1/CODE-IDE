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



package com.neo.ide.lsp.java.utils;

import androidx.annotation.NonNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import openjdk.source.tree.LineMap;
import openjdk.tools.javac.parser.Scanner;
import openjdk.tools.javac.parser.ScannerFactory;
import openjdk.tools.javac.parser.Tokens;
import openjdk.tools.javac.parser.Tokens.TokenKind;
import openjdk.tools.javac.util.Context;
import org.jetbrains.annotations.Contract;

/**
 * @author Akash Yadav
 */
public class ASTFixer {
  public static final String IDENT = "I_N_J_E_C_T_E_D";

  private static final Set<TokenKind> MEMBER_SELECTION_TOKENS =
      ImmutableSet.of(
          Tokens.TokenKind.IDENTIFIER,
          Tokens.TokenKind.LT,
          TokenKind.NEW,
          TokenKind.THIS,
          TokenKind.SUPER,
          TokenKind.CLASS,
          TokenKind.STAR);
  private static final Set<TokenKind> INVALID_SELECTION_SUFFIXES =
      ImmutableSet.of(TokenKind.RBRACE);

  private final Context context;

  public ASTFixer(Context context) {
    this.context = context;
  }

  public StringBuilder fix(CharSequence content) {
    Scanner scanner = ScannerFactory.instance(context).newScanner(content, true);
    List<Edit> edits = new ArrayList<>();
    for (; ; scanner.nextToken()) {
      Tokens.Token token = scanner.token();
      if (token.kind == TokenKind.EOF) {
        break;
      } else if (token.kind == TokenKind.DOT || token.kind == TokenKind.COLCOL) {
        fixMemberSelection(scanner, edits);
      } else if (token.kind == TokenKind.ERROR) {
        int errPos = scanner.errPos();
        if (errPos >= 0 && errPos < content.length()) {
          fixError(scanner, content, edits);
        }
      }
    }
    return Edit.applyInsertions(content, edits);
  }

  private void fixMemberSelection(@NonNull Scanner scanner, List<Edit> edits) {
    Tokens.Token token = scanner.token();
    Tokens.Token nextToken = scanner.token(1);

    LineMap lineMap = scanner.getLineMap();
    int tokenLine = (int) lineMap.getLineNumber(token.pos);
    int nextLine = (int) lineMap.getLineNumber(nextToken.pos);

    if (nextLine > tokenLine) {
      edits.add(Edit.create(token.endPos, IDENT + ";"));
    } else if (!MEMBER_SELECTION_TOKENS.contains(nextToken.kind)) {
      String toInsert = IDENT;
      if (INVALID_SELECTION_SUFFIXES.contains(nextToken.kind)) {
        toInsert = IDENT + ";";
      }
      edits.add(Edit.create(token.endPos, toInsert));
    }
  }

  private void fixError(@NonNull Scanner scanner, @NonNull CharSequence content, List<Edit> edits) {
    int errPos = scanner.errPos();
    if (content.charAt(errPos) == '.' && errPos > 0 && content.charAt(errPos) == '.') {
      if (errPos < content.length() - 1
          && Character.isJavaIdentifierStart(content.charAt(errPos + 1))) {
        edits.add(Edit.create(errPos, IDENT));
      }
    }
  }

  public static class Edit {
    private static final Ordering<Edit> REVERSE_INSERTION =
        Ordering.natural().onResultOf(Edit::getPos).reverse();

    private final int pos;
    private final String text;

    public Edit(int pos, String text) {
      this.pos = pos;
      this.text = text;
    }

    public int getPos() {
      return pos;
    }

    public String getText() {
      return text;
    }

    @NonNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Edit create(int pos, String text) {
      return new Edit(pos, text);
    }

    @NonNull
    public static StringBuilder applyInsertions(CharSequence content, List<Edit> edits) {
      ImmutableList<Edit> reverseEdits = REVERSE_INSERTION.immutableSortedCopy(edits);

      StringBuilder sb = new StringBuilder(content);

      for (Edit edit : reverseEdits) {
        sb.insert(edit.getPos(), edit.getText());
      }
      return sb;
    }
  }
}
