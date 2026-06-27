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
import com.neo.ide.templates.api.Language.Kotlin
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
import com.neo.ide.templates.api.base.models.Dependency
import com.neo.ide.templates.api.base.modules.android.defaultAppModule
import com.neo.ide.templates.api.base.util.AndroidModuleResManager.ResourceType.NAVIGATION
import com.neo.ide.templates.impl.R
import com.neo.ide.templates.impl.base.createRecipe
import com.neo.ide.templates.impl.base.emptyThemesAndColors
import com.neo.ide.templates.impl.base.writeMainActivity
import com.neo.ide.templates.impl.baseProjectImpl
import com.neo.ide.templates.impl.templateAsset
fun navDrawerActivityProject() = baseProjectImpl {
  templateName = R.string.template_navigation_drawer
  thumb = R.drawable.template_blank_activity_drawer
  defaultAppModule {
    recipe = createRecipe {
      sources {
        writeMainActivity(this, ktSrc = ::navDrawerActivitySrcKt,
          javaSrc = ::navDrawerActivitySrcJava)
      }
      res {
        copyAssetsRecursively(templateAsset("navDrawer", "res"), mainResDir())
        writeXmlResource("mobile_navigation", NAVIGATION,
          source = ::navDrawerNavigationXmlSrc)
        putStringRes("navigation_drawer_open", "Open navigation drawer")
        putStringRes("navigation_drawer_close", "Close navigation drawer")
        putStringRes("nav_header_title", "AndroidIDE")
        putStringRes("nav_header_subtitle", "contact@androidide.com")
        putStringRes("nav_header_desc", "Navigation header")
        putStringRes("action_settings", "Settings")
        putStringRes("menu_home", "Home")
        putStringRes("menu_gallery", "Gallery")
        putStringRes("menu_slideshow", "Slideshow")
        emptyThemesAndColors()
      }
      if (data.language == Kotlin) {
        navDrawerActivityProjectKt()
      } else {
        navDrawerActivityProjectJava()
      }
    }
  }
}
private fun AndroidModuleTemplateBuilder.navDrawerActivityProjectJava() {
  executor.apply {
    addDependency(Dependency.AndroidX.Navigation_Ui)
    addDependency(Dependency.AndroidX.Navigation_Fragment)
    addDependency(Dependency.AndroidX.LifeCycle_LiveData)
    addDependency(Dependency.AndroidX.LifeCycle_ViewModel)
    sources {
      writeJavaSrc("${data.packageName}.ui.gallery", "GalleryFragment",
        source = ::galleryFragmentSrcJava)
      writeJavaSrc("${data.packageName}.ui.gallery", "GalleryViewModel",
        source = ::galleryModelSrcJava)
      writeJavaSrc("${data.packageName}.ui.home", "HomeFragment",
        source = ::homeFragmentSrcJava)
      writeJavaSrc("${data.packageName}.ui.home", "HomeViewModel",
        source = ::homeModelSrcJava)
      writeJavaSrc("${data.packageName}.ui.slideshow", "SlideshowFragment",
        source = ::slideshowFragmentSrcJava)
      writeJavaSrc("${data.packageName}.ui.slideshow", "SlideshowViewModel",
        source = ::slideshowModelSrcJava)
    }
  }
}
private fun AndroidModuleTemplateBuilder.navDrawerActivityProjectKt() {
  executor.apply {
    addDependency(Dependency.AndroidX.Navigation_Ui_Ktx)
    addDependency(Dependency.AndroidX.Navigation_Fragment_Ktx)
    addDependency(Dependency.AndroidX.LifeCycle_LiveData_Ktx)
    addDependency(Dependency.AndroidX.LifeCycle_ViewModel_Ktx)
    sources {
      writeKtSrc("${data.packageName}.ui.gallery", "GalleryFragment",
        source = ::galleryFragmentSrcKt)
      writeKtSrc("${data.packageName}.ui.gallery", "GalleryViewModel",
        source = ::galleryModelSrcKt)
      writeKtSrc("${data.packageName}.ui.home", "HomeFragment",
        source = ::homeFragmentSrcKt)
      writeKtSrc("${data.packageName}.ui.home", "HomeViewModel",
        source = ::homeModelSrcKt)
      writeKtSrc("${data.packageName}.ui.slideshow", "SlideshowFragment",
        source = ::slideshowFragmentSrcKt)
      writeKtSrc("${data.packageName}.ui.slideshow", "SlideshowViewModel",
        source = ::slideshowModelSrcKt)
    }
  }
}
