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

package com.neo.ide.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.neo.ide.R
import com.neo.ide.app.BaseActivity

class PermissionsActivity : BaseActivity() {

    private lateinit var storageStatus: TextView
    private lateinit var storageGrantBtn: Button
    private lateinit var installStatus: TextView
    private lateinit var installGrantBtn: Button
    private lateinit var continueBtn: Button

    override fun bindLayout(): View {
        return layoutInflater.inflate(R.layout.activity_permissions, null)
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        updateAllPermissions()
    }

    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateAllPermissions()
    }

    private val installPackagesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateAllPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageStatus = findViewById(R.id.permission_storage_status)
        storageGrantBtn = findViewById(R.id.permission_storage_grant_btn)
        installStatus = findViewById(R.id.permission_install_status)
        installGrantBtn = findViewById(R.id.permission_install_grant_btn)
        continueBtn = findViewById(R.id.permissions_continue_btn)

        storageGrantBtn.setOnClickListener {
            requestStoragePermission()
        }

        installGrantBtn.setOnClickListener {
            requestInstallPackagesPermission()
        }

        continueBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        updateAllPermissions()
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun canRequestPackageInstalls(): Boolean {
        return packageManager.canRequestPackageInstalls()
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                manageStorageLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                manageStorageLauncher.launch(intent)
            }
        } else {
            storagePermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun requestInstallPackagesPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        installPackagesLauncher.launch(intent)
    }

    private fun updateAllPermissions() {
        val storageGranted = isStoragePermissionGranted()
        val installGranted = canRequestPackageInstalls()

        if (storageGranted) {
            storageStatus.text = "Granted"
            storageStatus.setTextColor(ContextCompat.getColor(this, R.color.status_success))
            storageGrantBtn.isEnabled = false
            storageGrantBtn.text = "Granted"
        } else {
            storageStatus.text = "Required"
            storageStatus.setTextColor(ContextCompat.getColor(this, R.color.status_error))
            storageGrantBtn.isEnabled = true
            storageGrantBtn.text = "Grant"
        }

        if (installGranted) {
            installStatus.text = "Granted"
            installStatus.setTextColor(ContextCompat.getColor(this, R.color.status_success))
            installGrantBtn.isEnabled = false
            installGrantBtn.text = "Granted"
        } else {
            installStatus.text = "Required"
            installStatus.setTextColor(ContextCompat.getColor(this, R.color.status_error))
            installGrantBtn.isEnabled = true
            installGrantBtn.text = "Grant"
        }

        val allGranted = storageGranted && installGranted
        continueBtn.isEnabled = allGranted
        continueBtn.alpha = if (allGranted) 1f else 0.5f
    }

    override fun onResume() {
        super.onResume()
        updateAllPermissions()
    }
}
