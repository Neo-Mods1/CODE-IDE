package com.neo.ide.setup

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.neo.ide.R
import com.neo.ide.download.ResourceManager
import com.neo.ide.download.ResumableDownloader
import com.neo.ide.download.SetupState
import com.neo.ide.activities.MainActivity
import android.content.Intent
import kotlinx.coroutines.*
import org.json.JSONArray

class TerminalSetupActivity : AppCompatActivity() {

    private lateinit var terminalOutput: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var statusText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val resourceManager by lazy { ResourceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal_setup)

        terminalOutput = findViewById(R.id.terminal_output)
        scrollView = findViewById(R.id.terminal_scroll)
        statusText = findViewById(R.id.setup_status_text)

        val selectedResourcesJson = intent.getStringExtra("selected_resources")

        appendOutput("CODE-IDE Setup")
        appendOutput("==============\n\n")

        scope.launch {
            if (selectedResourcesJson != null) {
                runSetupWithSelection(selectedResourcesJson)
            } else {
                runSetupLegacy()
            }
        }
    }

    private suspend fun runSetupWithSelection(jsonStr: String) {
        val resourcesArray = JSONArray(jsonStr)
        if (resourcesArray.length() == 0) {
            appendOutput("No resources selected.\n")
            finishSetup()
            return
        }

        appendOutput("Installing ${resourcesArray.length()} selected resources:\n")
        for (i in 0 until resourcesArray.length()) {
            val obj = resourcesArray.getJSONObject(i)
            val name = obj.getString("name")
            val version = obj.optString("version", "")
            val sizeBytes = obj.optLong("size", 0)
            val sizeMB = sizeBytes / (1024.0 * 1024.0)
            appendOutput("  ${i + 1}. $name v$version (${String.format("%.1f", sizeMB)} MB)\n")
        }
        appendOutput("\n")

        // Build ResourceEntry list from JSON
        val entries = mutableListOf<ResourceManager.ResourceEntry>()
        for (i in 0 until resourcesArray.length()) {
            val obj = resourcesArray.getJSONObject(i)
            entries.add(
                ResourceManager.ResourceEntry(
                    name = obj.getString("name"),
                    category = obj.getString("category"),
                    version = obj.optString("version", ""),
                    size = obj.optLong("size", 0),
                    sha256 = obj.optString("sha256", ""),
                    format = obj.optString("format", "tar.xz"),
                    url = obj.getString("url"),
                    destination = obj.optString("destination", "{install_dir}/${obj.getString("name")}")
                )
            )
        }

        // Filter out already installed
        val toInstall = entries.filter { !resourceManager.isResourceInstalled(it) }

        if (toInstall.isEmpty()) {
            appendOutput("All selected resources already installed.\n")
            finishSetup()
            return
        }

        appendOutput("Need to download ${toInstall.size} resources.\n\n")

        // Download
        val success = resourceManager.downloadResources(
            toInstall,
            onResourceStart = { resource, current, total ->
                appendOutput("[$current/$total] Downloading ${resource.name}...\n")
            },
            onResourceProgress = { resource, progress ->
                val pct = (progress * 100).toInt()
                handler.post {
                    statusText.text = "Downloading ${resource.name}... $pct%"
                }
            },
            onResourceComplete = { resource ->
                appendOutput("  Downloaded ${resource.name}\n")
            },
            onError = { error ->
                appendOutput("ERROR: $error\n")
            }
        )

        if (!success) {
            appendOutput("\nDownload failed. Check your connection and try again.\n")
            updateStatus("Failed")
            return
        }

        appendOutput("\nDownloads complete. Extracting...\n\n")

        // Extract
        for (resource in toInstall) {
            appendOutput("Extracting ${resource.name}...\n")
            val result = resourceManager.extractResource(resource, object : ResourceManager.ExtractionListener {
                override fun onExtractionStart(fileName: String) {}
                override fun onExtractionComplete(destDir: java.io.File) {
                    appendOutput("  Extracted to ${destDir.name}\n")
                }
                override fun onExtractionError(error: String) {
                    appendOutput("  ERROR: $error\n")
                }
            })
            if (result.isFailure) {
                appendOutput("Failed to extract ${resource.name}: ${result.exceptionOrNull()?.message}\n")
            }
        }

        appendOutput("\nSetup complete!\n")
        finishSetup()
    }

    private suspend fun runSetupLegacy() {
        appendOutput("Fetching resource manifest...\n")

        val manifestResult = resourceManager.fetchManifest()
        if (manifestResult.isFailure) {
            appendOutput("ERROR: Failed to fetch manifest: ${manifestResult.exceptionOrNull()?.message}\n")
            appendOutput("Check your internet connection and try again.\n")
            updateStatus("Failed")
            return
        }

        val manifest = manifestResult.getOrThrow()
        appendOutput("Manifest v${manifest.version} (${manifest.resources.size} resources)\n\n")

        val requiredResources = manifest.resources.filter { !resourceManager.isResourceInstalled(it) }

        if (requiredResources.isEmpty()) {
            appendOutput("All resources already installed.\n")
            finishSetup()
            return
        }

        appendOutput("Need to download ${requiredResources.size} resources:\n")
        requiredResources.forEach { res ->
            val sizeMB = res.size / (1024.0 * 1024.0)
            appendOutput("  - ${res.name} (${String.format("%.1f", sizeMB)} MB)\n")
        }
        appendOutput("\n")

        val success = resourceManager.downloadResources(
            requiredResources,
            onResourceStart = { resource, current, total ->
                appendOutput("[$current/$total] Downloading ${resource.name}...\n")
            },
            onResourceProgress = { resource, progress ->
                val pct = (progress * 100).toInt()
                handler.post {
                    statusText.text = "Downloading ${resource.name}... $pct%"
                }
            },
            onResourceComplete = { resource ->
                appendOutput("  Downloaded ${resource.name}\n")
            },
            onError = { error ->
                appendOutput("ERROR: $error\n")
            }
        )

        if (!success) {
            appendOutput("\nDownload failed. Please check your connection and try again.\n")
            updateStatus("Failed")
            return
        }

        appendOutput("\nAll downloads complete. Extracting...\n")

        for (resource in requiredResources) {
            appendOutput("Extracting ${resource.name}...\n")
            val result = resourceManager.extractResource(resource, object : ResourceManager.ExtractionListener {
                override fun onExtractionStart(fileName: String) {}
                override fun onExtractionComplete(destDir: java.io.File) {
                    appendOutput("  Extracted to ${destDir.name}\n")
                }
                override fun onExtractionError(error: String) {
                    appendOutput("  ERROR: $error\n")
                }
            })
            if (result.isFailure) {
                appendOutput("Failed to extract ${resource.name}: ${result.exceptionOrNull()?.message}\n")
            }
        }

        resourceManager.setInstalledVersion(manifest.version)
        appendOutput("\nSetup complete!\n")
        finishSetup()
    }

    private fun finishSetup() {
        SetupState.setSetupComplete(this, true)
        updateStatus("Complete")
        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }

    private fun appendOutput(text: String) {
        handler.post {
            terminalOutput.append(text)
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }

    private fun updateStatus(text: String) {
        handler.post {
            statusText.text = text
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
