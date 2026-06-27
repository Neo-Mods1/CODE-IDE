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

/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neo.ide.templates.impl.bottomNavActivity

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

fun bottomNavActivityProject() = baseProjectImpl {
  templateName = R.string.template_navigation_tabs
  thumb = R.drawable.template_bottom_navigation_activity
  defaultAppModule {
    recipe = createRecipe {
      sources {
        writeMainActivity(this, ktSrc = ::bottomNavActivitySrcKt,
          javaSrc = ::bottomNavActivitySrcJava)
      }

      res {
        copyAssetsRecursively(templateAsset("bottomNav", "res"), mainResDir())

        writeXmlResource("mobile_navigation", NAVIGATION,
          source = ::bottomNavNavigationXmlSrc)

        putStringRes("title_home", "Home")
        putStringRes("title_dashboard", "Dashboard")
        putStringRes("title_notifications", "Notifications")

        emptyThemesAndColors(actionBar = true)
      }

      if (data.language == Kotlin) {
        bottomNavActivityProjectKt()
      } else {
        bottomNavActivityProjectJava()
      }
    }
  }
}

fun AndroidModuleTemplateBuilder.bottomNavActivityProjectKt() {
  executor.apply {
    addDependency(Dependency.AndroidX.Navigation_Ui_Ktx)
    addDependency(Dependency.AndroidX.Navigation_Fragment_Ktx)
    addDependency(Dependency.AndroidX.LifeCycle_LiveData_Ktx)
    addDependency(Dependency.AndroidX.LifeCycle_ViewModel_Ktx)

    sources {
      writeKtSrc("${data.packageName}.ui.dashboard", "DashboardFragment",
        source = ::bottomNavFragmentDashSrcKt)
      writeKtSrc("${data.packageName}.ui.dashboard", "DashboardViewModel",
        source = ::bottomNavModelDashSrcKt)

      writeKtSrc("${data.packageName}.ui.home", "HomeFragment",
        source = ::bottomNavFragmentHomeSrcKt)
      writeKtSrc("${data.packageName}.ui.home", "HomeViewModel",
        source = ::bottomNavModelHomeSrcKt)

      writeKtSrc("${data.packageName}.ui.notifications",
        "NotificationsFragment", source = ::bottomNavFragmentNotificationsSrcKt)
      writeKtSrc("${data.packageName}.ui.notifications",
        "NotificationsViewModel", source = ::bottomNavModelNotificationsSrcKt)

    }
  }
}

private fun AndroidModuleTemplateBuilder.bottomNavActivityProjectJava() {
  executor.apply {
    addDependency(Dependency.AndroidX.Navigation_Ui)
    addDependency(Dependency.AndroidX.Navigation_Fragment)
    addDependency(Dependency.AndroidX.LifeCycle_LiveData)
    addDependency(Dependency.AndroidX.LifeCycle_ViewModel)

    sources {
      writeJavaSrc("${data.packageName}.ui.dashboard", "DashboardFragment",
        source = ::bottomNavFragmentDashSrcJava)
      writeJavaSrc("${data.packageName}.ui.dashboard", "DashboardViewModel",
        source = ::bottomNavModelDashSrcJava)

      writeJavaSrc("${data.packageName}.ui.home", "HomeFragment",
        source = ::bottomNavFragmentHomeSrcJava)
      writeJavaSrc("${data.packageName}.ui.home", "HomeViewModel",
        source = ::bottomNavModelHomeSrcJava)

      writeJavaSrc("${data.packageName}.ui.notifications",
        "NotificationsFragment",
        source = ::bottomNavFragmentNotificationsSrcJava)
      writeJavaSrc("${data.packageName}.ui.notifications",
        "NotificationsViewModel", source = ::bottomNavModelNotificationsSrcJava)

    }
  }
}
