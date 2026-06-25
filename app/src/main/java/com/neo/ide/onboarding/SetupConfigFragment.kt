package com.neo.ide.onboarding

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.neo.ide.R
import com.neo.ide.download.ResourceManager
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

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var manifest: ResourceManager.Manifest? = null
    private val selectedResources = mutableMapOf<String, SelectedResource>()

    data class SelectedResource(
        val category: String,
        val name: String,
        val version: String,
        val url: String,
        val size: Long,
        val sha256: String,
        val format: String,
        val destination: String
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

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

        retryBtn.setOnClickListener { fetchManifest() }
        updateNetworkStatus()
        fetchManifest()
    }

    private fun updateNetworkStatus() {
        if (!isAdded) return
        val cm = requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val connected = activeNetwork?.isConnectedOrConnecting == true
        networkStatus.text = if (connected) "Connected" else "No connection"
        networkStatus.setTextColor(ContextCompat.getColor(requireContext(), if (connected) R.color.status_success else R.color.status_error))
    }

    private fun fetchManifest() {
        loadingContainer.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
        resourcesContainer.visibility = View.GONE

        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url(ResourceManager.MANIFEST_URL)
                        .build()
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
                    val body = response.body?.string() ?: throw Exception("Empty body")
                    parseManifest(body)
                }
                manifest = result
                loadingContainer.visibility = View.GONE
                resourcesContainer.visibility = View.VISIBLE
                populateUI(result)
            } catch (e: Exception) {
                loadingContainer.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
                errorText.text = "Failed to fetch resources: ${e.message}"
            }
        }
    }

    private fun parseManifest(jsonStr: String): ResourceManager.Manifest {
        val json = JSONObject(jsonStr)

        val categories = mutableMapOf<String, String>()
        val catsObj = json.optJSONObject("categories")
        if (catsObj != null) {
            for (key in catsObj.keys()) {
                categories[key] = catsObj.getString(key)
            }
        }

        val resources = mutableListOf<ResourceManager.ResourceEntry>()
        val resourcesArray = json.getJSONArray("resources")
        for (i in 0 until resourcesArray.length()) {
            val obj = resourcesArray.getJSONObject(i)
            resources.add(
                ResourceManager.ResourceEntry(
                    name = obj.getString("name"),
                    category = obj.optString("category", "unknown"),
                    version = obj.optString("version", ""),
                    size = obj.optLong("size", 0),
                    sha256 = obj.optString("sha256", ""),
                    format = obj.optString("format", "tar.xz"),
                    url = obj.getString("url"),
                    destination = obj.optString("destination", "{install_dir}/" + obj.getString("name"))
                )
            )
        }

        return ResourceManager.Manifest(
            version = json.optString("version", "1.0"),
            generated = json.optString("generated", ""),
            resources = resources,
            categories = categories
        )
    }

    private fun populateUI(manifest: ResourceManager.Manifest) {
        resourcesContainer.removeAllViews()
        selectedResources.clear()

        // Group resources by category
        val grouped = manifest.resources.groupBy { it.category }
        val inflater = LayoutInflater.from(requireContext())

        val categoryOrder = listOf("cmdline_tools", "platform_tools", "build_tools", "platforms", "ndk")

        for (category in categoryOrder) {
            val resources = grouped[category] ?: continue
            if (resources.isEmpty()) continue

            val itemView = inflater.inflate(R.layout.item_resource_category, resourcesContainer, false)
            val checkbox = itemView.findViewById<CheckBox>(R.id.resource_checkbox)
            val nameText = itemView.findViewById<TextView>(R.id.resource_name)
            val sizeText = itemView.findViewById<TextView>(R.id.resource_size)
            val versionContainer = itemView.findViewById<LinearLayout>(R.id.version_container)
            val versionDropdown = itemView.findViewById<AutoCompleteTextView>(R.id.version_dropdown)

            val categoryLabel = manifest.categories[category] ?: category
            nameText.text = categoryLabel

            // Calculate total size for this category
            val totalSize = resources.sumOf { it.size }
            sizeText.text = formatSize(totalSize)

            if (resources.size == 1) {
                // Single resource - just select it
                val res = resources[0]
                checkbox.isChecked = true
                selectedResources[category] = SelectedResource(
                    category = res.category,
                    name = res.name,
                    version = res.version,
                    url = res.url,
                    size = res.size,
                    sha256 = res.sha256,
                    format = res.format,
                    destination = res.destination
                )
            } else {
                // Multiple versions - show dropdown
                versionContainer.visibility = View.VISIBLE
                val versions = resources.map { it.version }.toTypedArray()
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, versions)
                versionDropdown.setAdapter(adapter)
                versionDropdown.setText(versions[0], false)

                val firstRes = resources[0]
                selectedResources[category] = SelectedResource(
                    category = firstRes.category,
                    name = firstRes.name,
                    version = firstRes.version,
                    url = firstRes.url,
                    size = firstRes.size,
                    sha256 = firstRes.sha256,
                    format = firstRes.format,
                    destination = firstRes.destination
                )

                versionDropdown.setOnItemClickListener { _, _, position, _ ->
                    val selected = resources[position]
                    selectedResources[category] = SelectedResource(
                        category = selected.category,
                        name = selected.name,
                        version = selected.version,
                        url = selected.url,
                        size = selected.size,
                        sha256 = selected.sha256,
                        format = selected.format,
                        destination = selected.destination
                    )
                    // Update size display
                    sizeText.text = formatSize(selected.size)
                }
            }

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    selectedResources.remove(category)
                } else {
                    // Re-add with current selection
                    val version = versionDropdown.text.toString()
                    val res = resources.find { it.version == version } ?: resources[0]
                    selectedResources[category] = SelectedResource(
                        category = res.category,
                        name = res.name,
                        version = res.version,
                        url = res.url,
                        size = res.size,
                        sha256 = res.sha256,
                        format = res.format,
                        destination = res.destination
                    )
                }
            }

            resourcesContainer.addView(itemView)
        }
    }

    private fun formatSize(bytes: Long): String {
        if (bytes <= 0) return "Unknown size"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format("%.1f GB", gb)
            mb >= 1.0 -> String.format("%.1f MB", mb)
            kb >= 1.0 -> String.format("%.1f KB", kb)
            else -> "$bytes B"
        }
    }

    fun getSelectedResources(): List<SelectedResource> {
        return selectedResources.values.toList()
    }

    fun isAutoInstall(): Boolean = autoInstallSwitch.isChecked

    fun getSetupArgs(): String {
        val parts = mutableListOf<String>()
        for ((category, resource) in selectedResources) {
            parts.add("--$category ${resource.version}")
        }
        return parts.joinToString(" ")
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
