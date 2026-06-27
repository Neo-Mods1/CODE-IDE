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

package com.neo.ide.templates.impl.bottomNavActivity
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
internal fun AndroidModuleTemplateBuilder.bottomNavModelNotificationsSrcKt() =
  """
package ${data.packageName}.ui.notifications
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class NotificationsViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}
""".trim()
internal fun AndroidModuleTemplateBuilder.bottomNavModelNotificationsSrcJava() =
  """
package ${data.packageName}.ui.notifications;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<String> mText;
    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }
    public LiveData<String> getText() {
        return mText;
    }
}
""".trim()
