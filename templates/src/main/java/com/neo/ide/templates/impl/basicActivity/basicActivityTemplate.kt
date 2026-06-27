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

package com.neo.ide.templates.impl.basicActivity
import com.neo.ide.templates.api.ProjectTemplate
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
import com.neo.ide.templates.api.base.modules.android.defaultAppModule
import com.neo.ide.templates.api.base.util.AndroidModuleResManager.ResourceType.LAYOUT
import com.neo.ide.templates.api.base.util.SourceWriter
import com.neo.ide.templates.impl.R
import com.neo.ide.templates.impl.base.createRecipe
import com.neo.ide.templates.impl.base.emptyThemesAndColors
import com.neo.ide.templates.impl.base.writeMainActivity
import com.neo.ide.templates.impl.baseProjectImpl
fun basicActivityProject(): ProjectTemplate {
  return baseProjectImpl {
    templateName = R.string.template_basic
    thumb = R.drawable.template_basic_activity
    defaultAppModule {
      recipe = createRecipe {
        sources {
          writeBasicActivitySrc(this)
        }
        res {
          writeBasicActivityLayout()
          emptyThemesAndColors()
        }
      }
    }
  }
}
private fun AndroidModuleTemplateBuilder.writeBasicActivitySrc(
  writer: SourceWriter
) {
  writeMainActivity(writer = writer, ktSrc = ::basicActivitySrcKt,
    javaSrc = ::basicActivitySrcJava)
}
internal fun AndroidModuleTemplateBuilder.writeBasicActivityLayout() {
  res.apply {
    writeXmlResource("activity_main", LAYOUT, source = ::basicActivityLayout)
    writeXmlResource("content_main", LAYOUT, source = ::basicActivityContent)
  }
}
