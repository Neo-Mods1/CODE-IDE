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

package com.neo.ide.onboarding

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.neo.ide.R
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SetupConfigFragment : Fragment() {

    private lateinit var loadingContainer: LinearLayout
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var retryBtn: View
    private lateinit var resourcesContainer: LinearLayout
    private lateinit var autoInstallSwitch: MaterialSwitch
    private lateinit var networkStatus: TextView
    private lateinit var sdkVersionDropdown: AutoCompleteTextView
    private lateinit var jdkVersionDropdown: AutoCompleteTextView
    private lateinit var ndkVersionDropdown: AutoCompleteTextView

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private var sdkOptions = listOf<ManifestOption>()
    private var jdkOptions = listOf<ManifestOption>()
    private var ndkOptions = listOf<ManifestOption>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_onboarding_setup_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingContainer = view.findViewById(R.id.loading_container)
        errorContainer = view.findViewById(R.id.error_container)
        errorText = view.findViewById(R.id.error_text)
        retryBtn = view.findViewById(R.id.retry_btn)
        resourcesContainer = view.findViewById(R.id.resources_container)
        autoInstallSwitch = view.findViewById(R.id.auto_install_switch)
        networkStatus = view.findViewById(R.id.network_status)
        sdkVersionDropdown = view.findViewById(R.id.sdk_version_dropdown)
        jdkVersionDropdown = view.findViewById(R.id.jdk_version_dropdown)
        ndkVersionDropdown = view.findViewById(R.id.ndk_version_dropdown)

        retryBtn.setOnClickListener { loadManifest() }
        updateNetworkStatus()
        loadManifest()
    }

    private fun updateNetworkStatus() {
        if (!isAdded) return
        val cm = requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val connected = activeNetwork?.isConnectedOrConnecting == true
        networkStatus.text = if (connected) "Connected" else "No connection"
        networkStatus.setTextColor(ContextCompat.getColor(requireContext(), if (connected) R.color.status_success else R.color.status_error))
    }

    private fun loadManifest() {
        loadingContainer.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
        resourcesContainer.visibility = View.GONE

        scope.launch {
            try {
                val resources = fetchManifestResources()
                if (!isAdded) return@launch
                loadingContainer.visibility = View.GONE
                if (resources.isEmpty()) {
                    errorContainer.visibility = View.VISIBLE
                    errorText.text = "No resources found in manifest"
                } else {
                    resourcesContainer.visibility = View.VISIBLE
                    populateSpinners(resources)
                }
            } catch (e: Exception) {
                if (!isAdded) return@launch
                loadingContainer.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
                errorText.text = "Failed to load: ${e.message}"
            }
        }
    }

    private suspend fun fetchManifestResources(): Map<String, List<ManifestOption>> = withContext(Dispatchers.IO) {
        val url = "https://raw.githubusercontent.com/Neo-Mods1/CODE-IDE-resources/main/manifest.json"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) throw Exception("HTTP ${response.code}")

        val body = response.body?.string() ?: throw Exception("Empty body")
        val json = JSONObject(body)

        val versions = json.optJSONObject("versions") ?: return@withContext emptyMap()
        val block = versions.optJSONObject("v1") ?: return@withContext emptyMap()

        val grouped = mutableMapOf<String, MutableList<ManifestOption>>()
        val arr = block.optJSONArray("resources") ?: return@withContext emptyMap()

        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val category = obj.optString("category", "")
            val name = obj.optString("name", obj.optString("tag", "unknown"))
            val version = obj.optString("version", "")
            val sizeBytes = obj.optLong("size", 0)

            val option = ManifestOption(
                name = name,
                category = category,
                version = version,
                sizeBytes = sizeBytes,
                displayLabel = "$name — ${formatSize(sizeBytes)}"
            )

            grouped.getOrPut(category) { mutableListOf() }.add(option)
        }
        grouped
    }

    private fun populateSpinners(grouped: Map<String, List<ManifestOption>>) {
        if (!isAdded) return
        val ctx = requireContext()

        // Platforms — show all available
        sdkOptions = grouped["platforms"] ?: emptyList()
        val sdkLabels = sdkOptions.map { it.displayLabel }
        sdkVersionDropdown.setAdapter(ArrayAdapter(ctx, android.R.layout.simple_dropdown_item_1line, sdkLabels))
        if (sdkLabels.isNotEmpty()) sdkVersionDropdown.setText(sdkLabels[0], false)

        // JDK — show all available
        jdkOptions = grouped["jdk"] ?: emptyList()
        val jdkLabels = jdkOptions.map { it.displayLabel }
        jdkVersionDropdown.setAdapter(ArrayAdapter(ctx, android.R.layout.simple_dropdown_item_1line, jdkLabels))
        if (jdkLabels.isNotEmpty()) jdkVersionDropdown.setText(jdkLabels[0], false)

        // NDK — show all + "Skip NDK" at top
        ndkOptions = grouped["ndk"] ?: emptyList()
        val ndkLabels = mutableListOf("Skip NDK")
        ndkLabels.addAll(ndkOptions.map { it.displayLabel })
        ndkVersionDropdown.setAdapter(ArrayAdapter(ctx, android.R.layout.simple_dropdown_item_1line, ndkLabels))
        ndkVersionDropdown.setText(ndkLabels[0], false)
    }

    fun getSelectedPlatform(): ManifestOption? {
        val pos = sdkVersionDropdown.getListSelection()
        return if (pos >= 0 && pos < sdkOptions.size) sdkOptions[pos] else sdkOptions.firstOrNull()
    }

    fun getSelectedJdk(): ManifestOption? {
        val pos = jdkVersionDropdown.getListSelection()
        return if (pos >= 0 && pos < jdkOptions.size) jdkOptions[pos] else jdkOptions.firstOrNull()
    }

    fun getSelectedNdk(): ManifestOption? {
        val text = ndkVersionDropdown.text.toString()
        if (text.startsWith("Skip")) return null
        val pos = ndkVersionDropdown.getListSelection()
        // position -1 because "Skip NDK" is at index 0 in dropdown but not in ndkOptions
        return if (pos > 0 && (pos - 1) < ndkOptions.size) ndkOptions[pos - 1] else null
    }

    fun isAutoInstall(): Boolean = autoInstallSwitch.isChecked

    private fun formatSize(bytes: Long): String {
        if (bytes <= 0) return "—"
        val mb = bytes / (1024.0 * 1024.0)
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> "%.1f GB".format(gb)
            mb >= 1.0 -> "%.1f MB".format(mb)
            else -> "%d KB".format(bytes / 1024)
        }
    }

    data class ManifestOption(
        val name: String,
        val category: String,
        val version: String,
        val sizeBytes: Long,
        val displayLabel: String
    )

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
