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

package com.neo.ide.templates.impl.base
import com.android.aaptcompiler.ConfigDescription
import com.android.aaptcompiler.android.ResTableConfig
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
import com.neo.ide.templates.api.base.util.AndroidModuleResManager.ResourceType.VALUES
internal fun simpleMaterial3Theme(themeName: String, actionBar: Boolean = false
): String {
  return """
<resources xmlns:tools="http://schemas.android.com/tools">
  <!-- Base application theme. -->
  <style name="Base.${themeName}" parent="Theme.Material3.DayNight${if (!actionBar) ".NoActionBar" else ""}">
    <!-- Customize your theme here. -->
    <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
  </style>
  <style name="$themeName" parent="Base.${themeName}" />
</resources>
  """.trim()
}
internal fun AndroidModuleTemplateBuilder.emptyThemesAndColors(
  actionBar: Boolean = false
) {
  val configNight = ConfigDescription().apply {
    uiMode = ResTableConfig.UI_MODE.NIGHT_YES
  }
  res.apply {
    // values
    writeXmlResource("themes", VALUES,
      source = simpleMaterial3Theme(manifest.themeRes, actionBar))
    writeXmlResource("colors", VALUES, source = emptyValuesFile())
    // values-night
    writeXmlResource("themes", VALUES, config = configNight,
      source = simpleMaterial3Theme(manifest.themeRes, actionBar))
    writeXmlResource("colors", VALUES, config = configNight,
      source = emptyValuesFile())
  }
}
