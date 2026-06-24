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
import androidx.core.content.ContextCompat
import com.neo.ide.R

class PermissionsActivity : AppCompatActivity() {

    private lateinit var storageStatus: TextView
    private lateinit var storageIcon: ImageView
    private lateinit var storageGrantBtn: Button
    private lateinit var continueBtn: Button

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            updateStorageStatus(true)
        } else {
            updateStorageStatus(false)
            Toast.makeText(this, "Storage permission is required", Toast.LENGTH_LONG).show()
        }
    }

    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            updateStorageStatus(Environment.isExternalStorageManager())
        }
    }

    private val installPackagesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateInstallStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        storageStatus = findViewById(R.id.permission_storage_status)
        storageIcon = findViewById(R.id.permission_storage_icon)
        storageGrantBtn = findViewById(R.id.permission_storage_grant_btn)
        continueBtn = findViewById(R.id.permissions_continue_btn)

        val title = findViewById<TextView>(R.id.permissions_title)
        val subtitle = findViewById<TextView>(R.id.permissions_subtitle)

        title.text = "Permissions Required"
        subtitle.text = "CODE-IDE needs storage access to read and write project files"

        storageGrantBtn.setOnClickListener {
            requestStoragePermission()
        }

        continueBtn.setOnClickListener {
            if (isStoragePermissionGranted()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } else {
                Toast.makeText(this, "Please grant storage permission to continue", Toast.LENGTH_SHORT).show()
            }
        }

        updateStorageStatus(isStoragePermissionGranted())
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
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

    private fun updateStorageStatus(granted: Boolean) {
        if (granted) {
            storageStatus.text = "Granted"
            storageStatus.setTextColor(ContextCompat.getColor(this, R.color.status_success))
            storageIcon.setImageResource(android.R.drawable.ic_secure)
            storageIcon.setColorFilter(ContextCompat.getColor(this, R.color.status_success))
            storageGrantBtn.isEnabled = false
            storageGrantBtn.text = "Granted"
            continueBtn.alpha = 1f
            continueBtn.isEnabled = true
        } else {
            storageStatus.text = "Not Granted"
            storageStatus.setTextColor(ContextCompat.getColor(this, R.color.status_error))
            storageIcon.setImageResource(android.R.drawable.ic_lock_lock)
            storageIcon.setColorFilter(ContextCompat.getColor(this, R.color.status_error))
            storageGrantBtn.isEnabled = true
            storageGrantBtn.text = "Grant Permission"
            continueBtn.alpha = 0.5f
            continueBtn.isEnabled = false
        }
    }

    private fun updateInstallStatus() {
        // Update UI if needed
    }

    override fun onResume() {
        super.onResume()
        updateStorageStatus(isStoragePermissionGranted())
    }
}
