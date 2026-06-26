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
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
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
    private lateinit var installGitCheckBox: CheckBox
    private lateinit var installOpensshCheckBox: CheckBox

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var manifestLoaded = false

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
        sdkVersionDropdown = view.findViewById(R.id.sdk_version_dropdown)
        jdkVersionDropdown = view.findViewById(R.id.jdk_version_dropdown)
        ndkVersionDropdown = view.findViewById(R.id.ndk_version_dropdown)
        installGitCheckBox = view.findViewById(R.id.install_git_checkbox)
        installOpensshCheckBox = view.findViewById(R.id.install_openssh_checkbox)

        retryBtn.setOnClickListener { loadConfig() }
        updateNetworkStatus()
        loadConfig()
    }

    private fun updateNetworkStatus() {
        if (!isAdded) return
        val cm = requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val connected = activeNetwork?.isConnectedOrConnecting == true
        networkStatus.text = if (connected) "Connected" else "No connection"
        networkStatus.setTextColor(ContextCompat.getColor(requireContext(), if (connected) R.color.status_success else R.color.status_error))
    }

    private fun loadConfig() {
        loadingContainer.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
        resourcesContainer.visibility = View.GONE

        scope.launch {
            delay(500)
            manifestLoaded = true
            loadingContainer.visibility = View.GONE
            resourcesContainer.visibility = View.VISIBLE
            populateUI()
        }
    }

    private fun populateUI() {
        val sdkVersions = listOf("36", "35", "34", "33")
        val sdkAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sdkVersions)
        sdkVersionDropdown.setAdapter(sdkAdapter)
        sdkVersionDropdown.setText(sdkVersions[0], false)

        val jdkVersions = listOf("JDK 17 (Stable)", "JDK 21 (Experimental)")
        val jdkAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, jdkVersions)
        jdkVersionDropdown.setAdapter(jdkAdapter)
        jdkVersionDropdown.setText(jdkVersions[0], false)

        val ndkVersions = listOf("r27 (27.0.12077973)", "r26d (26.3.11579264)")
        val ndkAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ndkVersions)
        ndkVersionDropdown.setAdapter(ndkAdapter)
        ndkVersionDropdown.setText(ndkVersions[0], false)
    }

    fun getSelectedSdkVersion(): String {
        return sdkVersionDropdown.text.toString().trim()
    }

    fun getSelectedJdkVersion(): String {
        val text = jdkVersionDropdown.text.toString()
        return when {
            text.contains("21") -> "21"
            else -> "17"
        }
    }

    fun getSelectedNdkVersion(): String {
        val text = ndkVersionDropdown.text.toString()
        return when {
            text.contains("r26") -> "26.3.11579264"
            else -> "27.0.12077973"
        }
    }

    fun isGitSelected(): Boolean = installGitCheckBox.isChecked

    fun isOpensshSelected(): Boolean = installOpensshCheckBox.isChecked

    fun isAutoInstall(): Boolean = autoInstallSwitch.isChecked

    fun buildSetupArguments(): Array<String> {
        val args = mutableListOf<String>()

        args.add("--install-dir")
        args.add(requireContext().filesDir.absolutePath + "/home")

        args.add("--sdk")
        args.add(getSelectedSdkVersion())

        args.add("--jdk")
        args.add(getSelectedJdkVersion())

        args.add("--assume-yes")

        if (isGitSelected()) {
            args.add("--with-git")
        }
        if (isOpensshSelected()) {
            args.add("--with-openssh")
        }

        return args.toTypedArray()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
