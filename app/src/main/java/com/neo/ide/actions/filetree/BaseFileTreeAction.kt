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



package com.neo.ide.actions.filetree

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.ActionItem
import com.neo.ide.actions.EditorActivityAction
import com.neo.ide.actions.hasRequiredData
import com.neo.ide.actions.markInvisible
import com.neo.ide.eventbus.events.Event
import com.neo.ide.events.ExpandTreeNodeRequestEvent
import com.neo.ide.events.ListProjectFilesRequestEvent
import com.unnamed.b.atv.model.TreeNode
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * Base class for actions related to the file tree.
 *
 * @author Akash Yadav
 */
abstract class BaseFileTreeAction(
  context: Context,
  @StringRes labelRes: Int? = null,
  @DrawableRes iconRes: Int? = null
) : EditorActivityAction() {

  override var requiresUIThread: Boolean = true
  override var location: ActionItem.Location = ActionItem.Location.EDITOR_FILE_TREE

  init {
    labelRes?.let { label = context.getString(it) }
    iconRes?.let { icon = ContextCompat.getDrawable(context, it) }
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)
    if (!data.hasFileTreeData()) {
      markInvisible()
      return
    }

    visible = true
    enabled = true
  }

  protected open fun ActionData.hasFileTreeData(): Boolean {
    return hasRequiredData(Context::class.java, File::class.java, TreeNode::class.java)
  }

  protected fun ActionData.getTreeNode() : TreeNode? {
    return this[TreeNode::class.java]
  }

  protected fun ActionData.requireTreeNode() : TreeNode {
    return getTreeNode()!!
  }

  protected fun Event.putData(context: Context): Event {
    put(Context::class.java, context)
    return this
  }

  protected fun requestFileListing() {
    EventBus.getDefault().post(ListProjectFilesRequestEvent())
  }

  protected fun requestExpandNode(node: TreeNode) {
    EventBus.getDefault().post(ExpandTreeNodeRequestEvent(node))
  }
}
