package com.neo.ide.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.neo.ide.R

class SetupConfigFragment : Fragment() {

    private lateinit var sdkDropdown: AutoCompleteTextView
    private lateinit var jdkDropdown: AutoCompleteTextView
    private lateinit var autoInstallSwitch: MaterialSwitch
    private lateinit var installGitCheckBox: CheckBox
    private lateinit var installOpenSshCheckBox: CheckBox
    private lateinit var networkStatus: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_onboarding_setup_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sdkDropdown = view.findViewById(R.id.sdk_version_dropdown)
        jdkDropdown = view.findViewById(R.id.jdk_version_dropdown)
        autoInstallSwitch = view.findViewById(R.id.auto_install_switch)
        installGitCheckBox = view.findViewById(R.id.install_git_checkbox)
        installOpenSshCheckBox = view.findViewById(R.id.install_openssh_checkbox)
        networkStatus = view.findViewById(R.id.network_status)

        val sdkVersions = arrayOf("34.0.4", "34.0.3", "34.0.1", "34.0.0", "33.0.1")
        val jdkVersions = arrayOf("21", "17", "11")

        sdkDropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sdkVersions))
        sdkDropdown.setText(sdkVersions[0], false)

        jdkDropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, jdkVersions))
        jdkDropdown.setText(jdkVersions[0], false)

        updateNetworkStatus()
    }

    private fun updateNetworkStatus() {
        if (!isAdded) return
        val cm = requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val connected = activeNetwork?.isConnectedOrConnecting == true

        networkStatus.text = if (connected) "Connected" else "No connection"
        networkStatus.setTextColor(ContextCompat.getColor(requireContext(), if (connected) R.color.status_success else R.color.status_error))
    }

    fun getSetupArgs(): String {
        val sdk = sdkDropdown.text.toString()
        val jdk = jdkDropdown.text.toString()
        val autoInstall = autoInstallSwitch.isChecked
        val git = installGitCheckBox.isChecked
        val openssh = installOpenSshCheckBox.isChecked

        return "--sdk $sdk --jdk $jdk ${if (git) "--with-git" else ""} ${if (openssh) "--with-openssh" else ""} ${if (autoInstall) "--assume-yes" else ""}"
    }

    fun isAutoInstall(): Boolean = autoInstallSwitch.isChecked
}
