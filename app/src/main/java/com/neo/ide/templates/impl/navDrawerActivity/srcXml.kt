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

package com.neo.ide.templates.impl.navDrawerActivity
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
internal fun AndroidModuleTemplateBuilder.navDrawerNavigationXmlSrc() = """
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mobile_navigation"
    app:startDestination="@+id/nav_home">
    <fragment
        android:id="@+id/nav_home"
        android:name="${data.packageName}.ui.home.HomeFragment"
        android:label="@string/menu_home"
        tools:layout="@layout/fragment_home" />
    <fragment
        android:id="@+id/nav_gallery"
        android:name="${data.packageName}.ui.gallery.GalleryFragment"
        android:label="@string/menu_gallery"
        tools:layout="@layout/fragment_gallery" />
    <fragment
        android:id="@+id/nav_slideshow"
        android:name="${data.packageName}.ui.slideshow.SlideshowFragment"
        android:label="@string/menu_slideshow"
        tools:layout="@layout/fragment_slideshow" />
</navigation>
""".trim()
