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



package com.neo.ide.lsp.java.actions

import com.neo.ide.actions.ActionItem
import com.neo.ide.lsp.actions.IActionsMenuProvider
import com.neo.ide.lsp.java.actions.common.CommentAction
import com.neo.ide.lsp.java.actions.common.FindReferencesAction
import com.neo.ide.lsp.java.actions.common.GoToDefinitionAction
import com.neo.ide.lsp.java.actions.common.OrganizeImportsAction
import com.neo.ide.lsp.java.actions.common.RemoveUnusedImportsAction
import com.neo.ide.lsp.java.actions.common.UncommentAction
import com.neo.ide.lsp.java.actions.diagnostics.AddImportAction
import com.neo.ide.lsp.java.actions.diagnostics.AddThrowsAction
import com.neo.ide.lsp.java.actions.diagnostics.AutoFixImportsAction
import com.neo.ide.lsp.java.actions.diagnostics.CreateMissingMethodAction
import com.neo.ide.lsp.java.actions.diagnostics.FieldToBlockAction
import com.neo.ide.lsp.java.actions.diagnostics.ImplementAbstractMethodsAction
import com.neo.ide.lsp.java.actions.diagnostics.RemoveClassAction
import com.neo.ide.lsp.java.actions.diagnostics.RemoveMethodAction
import com.neo.ide.lsp.java.actions.diagnostics.RemoveUnusedThrowsAction
import com.neo.ide.lsp.java.actions.diagnostics.SuppressUncheckedWarningAction
import com.neo.ide.lsp.java.actions.diagnostics.VariableToStatementAction
import com.neo.ide.lsp.java.actions.generators.GenerateConstructorAction
import com.neo.ide.lsp.java.actions.generators.GenerateMissingConstructorAction
import com.neo.ide.lsp.java.actions.generators.GenerateSettersAndGettersAction
import com.neo.ide.lsp.java.actions.generators.GenerateToStringMethodAction
import com.neo.ide.lsp.java.actions.generators.OverrideSuperclassMethodsAction

/**
 * Java code actions.
 * @author Akash Yadav
 */
object JavaCodeActionsMenu : IActionsMenuProvider {

  override val actions: List<ActionItem> =
    listOf(
      CommentAction(),
      UncommentAction(),
      GoToDefinitionAction(),
      FindReferencesAction(),
      AddImportAction(),
      AutoFixImportsAction(),
      ImplementAbstractMethodsAction(),
      VariableToStatementAction(),
      FieldToBlockAction(),
      RemoveClassAction(),
      RemoveMethodAction(),
      RemoveUnusedThrowsAction(),
      CreateMissingMethodAction(),
      SuppressUncheckedWarningAction(),
      AddThrowsAction(),
      GenerateSettersAndGettersAction(),
      OverrideSuperclassMethodsAction(),
      GenerateMissingConstructorAction(),
      GenerateConstructorAction(),
      GenerateToStringMethodAction(),
      RemoveUnusedImportsAction(),
      OrganizeImportsAction()
    )
}
