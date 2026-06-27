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



package com.neo.ide.handlers

import android.content.Context
import androidx.core.view.GravityCompat
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.ActionItem.Location.EDITOR_FILE_TREE
import com.neo.ide.actions.ActionMenu
import com.neo.ide.actions.ActionsRegistry
import com.neo.ide.actions.internal.DefaultActionsRegistry
import com.neo.ide.activities.editor.EditorHandlerActivity
import com.neo.ide.eventbus.events.filetree.FileClickEvent
import com.neo.ide.eventbus.events.filetree.FileLongClickEvent
import com.neo.ide.events.ExpandTreeNodeRequestEvent
import com.neo.ide.events.FileContextMenuItemClickEvent
import com.neo.ide.events.ListProjectFilesRequestEvent
import com.neo.ide.fragments.sheets.OptionsListFragment
import com.neo.ide.models.SheetOption
import com.neo.ide.utils.ApkInstaller
import com.neo.ide.utils.InstallationResultHandler
import com.neo.ide.utils.flashError
import com.unnamed.b.atv.model.TreeNode
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode.MAIN
import java.io.File

/**
 * Handles events related to files in filetree.
 *
 * @author Akash Yadav
 */
@Suppress("unused")
class FileTreeActionHandler : BaseEventHandler() {

  private var lastHeld: TreeNode? = null

  companion object {

    const val TAG_FILE_OPTIONS_FRAGMENT = "file_options_fragment"
    const val MB_10: Long = 10 * 1024 * 1024
  }

  @Subscribe(threadMode = MAIN)
  fun onFileClicked(event: FileClickEvent) {
    if (!checkIsEditorActivity(event)) {
      logCannotHandle(event)
      return
    }

    if (event.file.isDirectory) {
      return
    }

    val context = event[Context::class.java]!! as EditorHandlerActivity
    context.binding.root.closeDrawer(GravityCompat.START)
    if (event.file.name.endsWith(".apk")) {
      ApkInstaller.installApk(
        context,
        InstallationResultHandler.createEditorActivitySender(context),
        event.file,
        context.installationSessionCallback()
      )
      return
    }

    if (MB_10 < event.file.length()) {
      flashError("File is too big!")
      log.warn(
        "Cannot open {} as it is too big. File size: {} bytes", event.file, event.file.length())
      return
    }

    context.openFile(event.file)
  }

  @Subscribe(threadMode = MAIN)
  fun onFileLongClicked(event: FileLongClickEvent) {
    if (!checkIsEditorActivity(event)) {
      logCannotHandle(event)
      return
    }

    this.lastHeld = event[TreeNode::class.java]
    val context = event[Context::class.java]!! as EditorHandlerActivity
    createFileOptionsFragment(context, event.file)
      .show(context.supportFragmentManager, TAG_FILE_OPTIONS_FRAGMENT)
  }

  private fun createFileOptionsFragment(
    context: EditorHandlerActivity,
    file: File
  ): OptionsListFragment {
    val fragment = OptionsListFragment()
    val registry = ActionsRegistry.getInstance()
    val actions = registry.getActions(EDITOR_FILE_TREE)
    val data = ActionData()
    data.apply {
      put(Context::class.java, context)
      put(File::class.java, file)
      put(TreeNode::class.java, lastHeld)
    }

    for (action in actions.values) {

      check(action !is ActionMenu) { "File tree actions do not support action menus" }

      action.prepare(data)
      if (!action.enabled || !action.visible) {
        continue
      }

      fragment.addOption(
        SheetOption(action.id, action.icon, action.label, file).apply { this.extra = data }
      )
    }

    return fragment
  }

  @Subscribe(threadMode = MAIN)
  internal fun onFileOptionClicked(event: FileContextMenuItemClickEvent) {
    val option = event.option
    if (option.extra !is ActionData) {
      return
    }

    val data = option.extra!! as ActionData
    val registry = ActionsRegistry.getInstance() as DefaultActionsRegistry
    val action = registry.findAction(EDITOR_FILE_TREE, option.id)

    checkNotNull(action) {
      "Invalid FileContextMenuItemClickEvent received. No action item registered with id '${option.id}'"
    }

    registry.executeAction(action, data)
  }

  private fun requestFileListing() {
    EventBus.getDefault().post(ListProjectFilesRequestEvent())
  }

  private fun requestExpandHeldNode() {
    requestExpandNode(lastHeld!!)
  }

  private fun requestExpandNode(node: TreeNode) {
    EventBus.getDefault().post(ExpandTreeNodeRequestEvent(node))
  }
}
