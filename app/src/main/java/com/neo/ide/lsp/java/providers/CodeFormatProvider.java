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



package com.neo.ide.lsp.java.providers;

import static com.google.common.collect.Range.closedOpen;

import androidx.annotation.NonNull;
import com.google.common.collect.ImmutableList;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.Replacement;
import com.neo.ide.lsp.api.IServerSettings;
import com.neo.ide.lsp.java.models.JavaServerSettings;
import com.neo.ide.lsp.models.CodeFormatResult;
import com.neo.ide.lsp.models.FormatCodeParams;
import com.neo.ide.lsp.models.IndexedTextEdit;
import com.neo.ide.models.Range;
import com.neo.ide.utils.StopWatch;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Formats Java code using Google Java Format.
 *
 * @author Akash Yadav
 */
public class CodeFormatProvider {

  private static final Logger LOG = LoggerFactory.getLogger(CodeFormatProvider.class);

  private final JavaServerSettings settings;

  public CodeFormatProvider(IServerSettings settings) {
    assert settings instanceof JavaServerSettings;
    this.settings = (JavaServerSettings) settings;
  }

  public CodeFormatResult format(FormatCodeParams params) {
    try {
      final StopWatch watch = new StopWatch("Code formatting");
      final String content = params.getContent().toString();
      final Formatter formatter = new Formatter(settings.getFormatterOptions());

      if (params.getRange() == Range.NONE) {
        String formatted;
        try {
          formatted = formatter.formatSource(content);
        } catch (FormatterException e) {
          e.printStackTrace();
          formatted = content;
        }
        return CodeFormatResult.forWholeContent(content, formatted);
      }

      final Collection<com.google.common.collect.Range<Integer>> ranges =
          getCharRanges(content, params.getRange());

      final ImmutableList<Replacement> replacements =
          formatter.getFormatReplacements(content, ranges);

      watch.log();
      return createResult(replacements);
    } catch (Throwable e) {
      LOG.error("Failed to format code.", e);
      return CodeFormatResult.NONE;
    }
  }

  private CodeFormatResult createResult(final ImmutableList<Replacement> replacements) {
    final CodeFormatResult result = new CodeFormatResult(true);
    for (final Replacement replacement : replacements) {
      final com.google.common.collect.Range<Integer> range = replacement.getReplaceRange();
      final IndexedTextEdit edit = new IndexedTextEdit();
      edit.setNewText(replacement.getReplacementString());
      edit.setStart(range.lowerEndpoint());
      edit.setEnd(range.upperEndpoint());
      result.getIndexedTextEdits().add(edit);
    }
    return result;
  }

  @NonNull
  private Collection<com.google.common.collect.Range<Integer>> getCharRanges(
      final String content, @NonNull final Range range) {

    int start, end;
    if (range == Range.NONE) {
      start = 0;
      end = content.length();
    } else {
      start = range.getStart().requireIndex();
      end = range.getEnd().requireIndex();
    }

    return ImmutableList.of(closedOpen(start, end));
  }
}
