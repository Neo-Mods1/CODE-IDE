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

package com.neo.ide.project

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

object RecentProjectsManager {

    private const val PREFS_NAME = "recent_projects"
    private const val KEY_PROJECTS = "projects"
    private const val KEY_LAST_PROJECT = "last_project"
    private const val MAX_RECENT = 20

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun addProject(context: Context, path: String) {
        val current = getRecentProjects(context).toMutableList()
        current.remove(path)
        current.add(0, path)

        val trimmed = if (current.size > MAX_RECENT) current.take(MAX_RECENT) else current

        val jsonArray = JSONArray()
        trimmed.forEach { jsonArray.put(it) }

        prefs(context).edit()
            .putString(KEY_PROJECTS, jsonArray.toString())
            .putString(KEY_LAST_PROJECT, path)
            .apply()
    }

    fun getRecentProjects(context: Context): List<String> {
        val json = prefs(context).getString(KEY_PROJECTS, null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { array.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getLastProject(context: Context): String? =
        prefs(context).getString(KEY_LAST_PROJECT, null)

    fun removeProject(context: Context, path: String) {
        val current = getRecentProjects(context).toMutableList()
        current.remove(path)

        val jsonArray = JSONArray()
        current.forEach { jsonArray.put(it) }

        prefs(context).edit()
            .putString(KEY_PROJECTS, jsonArray.toString())
            .apply()
    }

    fun clearAll(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
