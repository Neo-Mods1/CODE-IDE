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

package com.neo.ide.templates.impl.tabbedActivity

import com.neo.ide.templates.api.Language
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
import com.neo.ide.templates.api.base.models.Dependency
import com.neo.ide.templates.api.base.modules.android.defaultAppModule
import com.neo.ide.templates.impl.R
import com.neo.ide.templates.impl.base.createRecipe
import com.neo.ide.templates.impl.base.emptyThemesAndColors
import com.neo.ide.templates.impl.base.writeMainActivity
import com.neo.ide.templates.impl.baseProjectImpl
import com.neo.ide.templates.impl.templateAsset

fun tabbedActivityProject() = baseProjectImpl {
  templateName = R.string.template_tabs
  thumb = R.drawable.template_blank_activity_tabs
  defaultAppModule {
    recipe = createRecipe {
      sources {
        writeMainActivity(this, ktSrc = ::tabbedActivitySrcKt,
          javaSrc = ::tabbedActivitySrcJava)
      }

      res {
        copyAssetsRecursively(templateAsset("tabbed", "res"), mainResDir())

        putStringRes("tab_text_1", "Tab 1")
        putStringRes("tab_text_2", "Tab 2")
        putStringRes("tab_text_3", "Tab 3")

        emptyThemesAndColors()
      }

      if (data.language == Language.Kotlin) {
        tabbedActivityProjectKt()
      } else {
        tabbedActivityProjectJava()
      }
    }
  }
}

fun AndroidModuleTemplateBuilder.tabbedActivityProjectKt() {
  executor.apply {
    addDependency(Dependency.AndroidX.LifeCycle_LiveData_Ktx)
    addDependency(Dependency.AndroidX.LifeCycle_ViewModel_Ktx)

    sources {
      writeKtSrc("${data.packageName}.ui.main", "SectionsPagerAdapter",
        source = ::tabbedPagerAdapterSrcKt)
      writeKtSrc("${data.packageName}.ui.main", "PageViewModel",
        source = ::tabbedPageViewModelSrcKt)
      writeKtSrc("${data.packageName}.ui.main", "PlaceholderFragment",
        source = ::tabbedPlaceholderFragmentSrcKt)
    }
  }
}

fun AndroidModuleTemplateBuilder.tabbedActivityProjectJava() {
  executor.apply {
    addDependency(Dependency.AndroidX.LifeCycle_LiveData)
    addDependency(Dependency.AndroidX.LifeCycle_ViewModel)

    sources {
      writeJavaSrc("${data.packageName}.ui.main", "SectionsPagerAdapter",
        source = ::tabbedPagerAdapterSrcJava)
      writeJavaSrc("${data.packageName}.ui.main", "PageViewModel",
        source = ::tabbedPageViewModelSrcJava)
      writeJavaSrc("${data.packageName}.ui.main", "PlaceholderFragment",
        source = ::tabbedPlaceholderFragmentSrcJava)
    }
  }
}
