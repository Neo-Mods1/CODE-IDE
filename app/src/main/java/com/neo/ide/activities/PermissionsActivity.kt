/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.neo.ide.R

class PermissionsActivity : AppCompatActivity() {

    private lateinit var storageStatus: TextView
    private lateinit var storageGrantBtn: Button
    private lateinit var installStatus: TextView
    private lateinit var installGrantBtn: Button
    private lateinit var continueBtn: Button

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
        setContentView(R.layout.activity_permissions)

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

        // Storage
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

        // Install packages
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

        // Continue button
        val allGranted = storageGranted && installGranted
        continueBtn.isEnabled = allGranted
        continueBtn.alpha = if (allGranted) 1f else 0.5f
    }

    override fun onResume() {
        super.onResume()
        updateAllPermissions()
    }
}
