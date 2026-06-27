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



package com.neo.ide.lsp.java.visitors;

import static com.github.javaparser.utils.PositionUtils.sortByBeginPosition;
import static com.neo.ide.lsp.java.utils.JavaParserUtils.getSimpleName;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.SourcePrinter;
import com.github.javaparser.printer.configuration.ConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.printer.configuration.PrinterConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrettyPrintingVisitor extends DefaultPrettyPrinterVisitor {

  public PrettyPrintingVisitor(PrinterConfiguration configuration) {
    super(configuration);
  }

  public PrettyPrintingVisitor(PrinterConfiguration configuration, SourcePrinter printer) {
    super(configuration, printer);
  }

  @Override
  public void visit(Name n, Void arg) {
    printOrphanCommentsBeforeThisChildNode(n);
    printComment(n.getComment(), arg);
    printer.print(n.getIdentifier());
    printOrphanCommentsEnding(n);
  }

  @Override
  public void visit(SimpleName n, Void arg) {
    printOrphanCommentsBeforeThisChildNode(n);
    printComment(n.getComment(), arg);

    String identifier = n.getIdentifier();
    printer.print(getSimpleName(identifier));
  }

  @Override
  public void visit(ClassOrInterfaceType n, Void arg) {
    printOrphanCommentsBeforeThisChildNode(n);
    printComment(n.getComment(), arg);

    printAnnotations(n.getAnnotations(), false, arg);

    n.getName().accept(this, arg);

    if (n.isUsingDiamondOperator()) {
      printer.print("<>");
    } else {
      printTypeArgs(n, arg);
    }
  }

  protected void printOrphanCommentsBeforeThisChildNode(final Node node) {
    if (!getOption(DefaultPrinterConfiguration.ConfigOption.PRINT_COMMENTS).isPresent()) return;
    if (node instanceof Comment) return;

    Node parent = node.getParentNode().orElse(null);
    if (parent == null) return;
    List<Node> everything = new ArrayList<>(parent.getChildNodes());
    sortByBeginPosition(everything);
    int positionOfTheChild = -1;
    for (int i = 0; i < everything.size(); ++i) { // indexOf is by equality, so this
      // is used to index by identity
      if (everything.get(i) == node) {
        positionOfTheChild = i;
        break;
      }
    }
    if (positionOfTheChild == -1) {
      throw new AssertionError("I am not a child of my parent.");
    }
    int positionOfPreviousChild = -1;
    for (int i = positionOfTheChild - 1; i >= 0 && positionOfPreviousChild == -1; i--) {
      if (!(everything.get(i) instanceof Comment)) positionOfPreviousChild = i;
    }
    for (int i = positionOfPreviousChild + 1; i < positionOfTheChild; i++) {
      Node nodeToPrint = everything.get(i);
      if (!(nodeToPrint instanceof Comment))
        throw new RuntimeException(
            "Expected comment, instead "
                + nodeToPrint.getClass()
                + ". Position of previous child: "
                + positionOfPreviousChild
                + ", position of child "
                + positionOfTheChild);
      nodeToPrint.accept(this, null);
    }
  }

  private Optional<ConfigurationOption> getOption(
      DefaultPrinterConfiguration.ConfigOption cOption) {
    return configuration.get(new DefaultConfigurationOption(cOption));
  }

  protected void printOrphanCommentsEnding(final Node node) {
    if (!getOption(DefaultPrinterConfiguration.ConfigOption.PRINT_COMMENTS).isPresent()) return;

    List<Node> everything = new ArrayList<>(node.getChildNodes());
    sortByBeginPosition(everything);
    if (everything.isEmpty()) {
      return;
    }

    int commentsAtEnd = 0;
    boolean findingComments = true;
    while (findingComments && commentsAtEnd < everything.size()) {
      Node last = everything.get(everything.size() - 1 - commentsAtEnd);
      findingComments = (last instanceof Comment);
      if (findingComments) {
        commentsAtEnd++;
      }
    }
    for (int i = 0; i < commentsAtEnd; i++) {
      everything.get(everything.size() - commentsAtEnd + i).accept(this, null);
    }
  }
}
