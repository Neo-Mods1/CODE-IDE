/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.neo.ide.R

class PermissionsFragment : Fragment() {

    private lateinit var storageStatus: TextView
    private lateinit var storageGrantBtn: Button
    private lateinit var installStatus: TextView
    private lateinit var installGrantBtn: Button
    private lateinit var notificationStatus: TextView
    private lateinit var notificationGrantBtn: Button

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { updatePermissions() }

    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { updatePermissions() }

    private val installPackagesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { updatePermissions() }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { updatePermissions() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_onboarding_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storageStatus = view.findViewById(R.id.permission_storage_status)
        storageGrantBtn = view.findViewById(R.id.permission_storage_grant_btn)
        installStatus = view.findViewById(R.id.permission_install_status)
        installGrantBtn = view.findViewById(R.id.permission_install_grant_btn)
        notificationStatus = view.findViewById(R.id.permission_notification_status)
        notificationGrantBtn = view.findViewById(R.id.permission_notification_grant_btn)

        storageGrantBtn.setOnClickListener { requestStoragePermission() }
        installGrantBtn.setOnClickListener { requestInstallPackagesPermission() }
        notificationGrantBtn.setOnClickListener { requestNotificationPermission() }

        updatePermissions()
    }

    fun allPermissionsGranted(): Boolean {
        if (!isAdded) return false
        return isStoragePermissionGranted() && canRequestPackageInstalls() && isNotificationPermissionGranted()
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun canRequestPackageInstalls(): Boolean {
        return requireContext().packageManager.canRequestPackageInstalls()
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Below Android 13, notifications don't need runtime permission
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                manageStorageLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                manageStorageLauncher.launch(intent)
            }
        } else {
            storagePermissionLauncher.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
        }
    }

    private fun requestInstallPackagesPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        installPackagesLauncher.launch(intent)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun updatePermissions() {
        if (!isAdded) return
        val storageGranted = isStoragePermissionGranted()
        val installGranted = canRequestPackageInstalls()
        val notificationGranted = isNotificationPermissionGranted()

        storageStatus.text = if (storageGranted) "Granted" else "Required"
        storageStatus.setTextColor(ContextCompat.getColor(requireContext(), if (storageGranted) R.color.status_success else R.color.status_error))
        storageGrantBtn.isEnabled = !storageGranted
        storageGrantBtn.text = if (storageGranted) "Granted" else "Grant"

        installStatus.text = if (installGranted) "Granted" else "Required"
        installStatus.setTextColor(ContextCompat.getColor(requireContext(), if (installGranted) R.color.status_success else R.color.status_error))
        installGrantBtn.isEnabled = !installGranted
        installGrantBtn.text = if (installGranted) "Granted" else "Grant"

        notificationStatus.text = if (notificationGranted) "Granted" else "Required"
        notificationStatus.setTextColor(ContextCompat.getColor(requireContext(), if (notificationGranted) R.color.status_success else R.color.status_error))
        notificationGrantBtn.isEnabled = !notificationGranted
        notificationGrantBtn.text = if (notificationGranted) "Granted" else "Grant"
    }

    override fun onResume() {
        super.onResume()
        updatePermissions()
    }
}
