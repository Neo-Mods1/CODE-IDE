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


package com.neo.ide.javac.services.visitors

import com.blankj.utilcode.util.ReflectUtils
import openjdk.tools.javac.code.Symbol.ModuleSymbol
import openjdk.tools.javac.comp.Enter
import openjdk.tools.javac.tree.JCTree.JCClassDecl
import openjdk.tools.javac.tree.TreeScanner

/**
 * `Enter.unenter` method is not available in JDK 11. This visitor does the same thing as calling
 * `Enter.unenter` in JDK 17.
 *
 * This is never used in the Android Runtime.
 *
 * @author Akash Yadav
 */
class UnEnter(private val enter: Enter, private val msym: ModuleSymbol) : TreeScanner() {
  override fun visitClassDef(tree: JCClassDecl) {
    val csym = tree.sym ?: return

    val etr = ReflectUtils.reflect(enter)
    ReflectUtils.reflect(etr.field("typeEnvs")).method("remove", csym)

    val chk = etr.field("chk")
    ReflectUtils.reflect(chk).method("removeCompiled", csym)
    ReflectUtils.reflect(chk).method("clearLocalClassNameIndexes", csym)

    ReflectUtils.reflect(etr.field("syms")).method("removeClass", msym, csym.flatname)
    super.visitClassDef(tree)
  }
}
