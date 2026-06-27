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

package com.neo.ide.templates.impl.emptyActivity
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
import com.neo.ide.templates.impl.base.baseLayoutContentMain
internal fun emptyLayoutSrc() = baseLayoutContentMain()
internal fun AndroidModuleTemplateBuilder.emptyActivitySrcKt(): String {
  return """
package ${data.packageName}
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ${data.packageName}.databinding.ActivityMainBinding
public class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    
    private val binding: ActivityMainBinding
      get() = checkNotNull(_binding) { "Activity has been destroyed" }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate and get instance of binding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        // set content view to binding's root
        setContentView(binding.root)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
"""
}
internal fun AndroidModuleTemplateBuilder.emptyActivitySrcJava(): String {
  return """
package ${data.packageName};
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import ${data.packageName}.databinding.ActivityMainBinding;
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // set content view to binding's root
        setContentView(binding.getRoot());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
"""
}
