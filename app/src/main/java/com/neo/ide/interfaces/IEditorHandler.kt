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



package com.neo.ide.interfaces

import com.neo.ide.models.Range
import com.neo.ide.models.SaveResult
import com.neo.ide.ui.CodeEditorView
import java.io.File

/**
 * @author Akash Yadav
 */
interface IEditorHandler {
  
  fun findIndexOfEditorByFile(file: File?) : Int
  
  fun getCurrentEditor(): CodeEditorView?
  fun getEditorAtIndex(index: Int) : CodeEditorView?
  fun getEditorForFile(file: File) : CodeEditorView?
  
  fun openFile(file: File) : CodeEditorView? = openFile(file, null)
  fun openFile(file: File, selection: Range?) : CodeEditorView?
  fun openFileAndSelect(file: File, selection: Range?)
  fun openFileAndGetIndex(file: File, selection: Range?) : Int
  
  fun areFilesModified(): Boolean
  fun areFilesSaving(): Boolean

  /**
   * Save all files.
   *
   * @param notify Whether to notify the user about the save event.
   * @param processResources Whether the resources must be generated after the save operation.
   * @param progressConsumer A function which consumes the progress of the save operation.
   * See [saveAllResult] for more details.
   */
  suspend fun saveAll(
    notify: Boolean = true,
    requestSync: Boolean = true,
    processResources: Boolean = false,
    progressConsumer: ((progress: Int, total: Int) -> Unit)? = null
  ) : Boolean

  /**
   * Save all files asynchronously.
   *
   * @param runAfter A callback function which will be run after the files are saved.
   * @see saveAll
   */
  fun saveAllAsync(
    notify: Boolean = true,
    requestSync: Boolean = true,
    processResources: Boolean = false,
    progressConsumer: ((progress: Int, total: Int) -> Unit)? = null,
    runAfter: (() -> Unit)? = null
  )

  /**
   * Save all files and get the [SaveResult].
   *
   * @param progressConsumer A function which consumes the progress of the save operation. The first
   * parameter of the function is the current save progress (saved file count) and the second parameter
   * is the total file count.
   */
  suspend fun saveAllResult(progressConsumer: ((progress: Int, total: Int) -> Unit)? = null) : SaveResult
  suspend fun saveResult(index: Int, result: SaveResult)
  
  fun closeFile(index: Int) = closeFile(index) {}
  fun closeFile(index: Int, runAfter: () -> Unit)
  fun closeAll() = closeAll {}
  fun closeAll(runAfter: () -> Unit)
  fun closeOthers()
}