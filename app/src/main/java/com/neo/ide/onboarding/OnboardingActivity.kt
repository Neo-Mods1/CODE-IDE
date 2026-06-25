/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.neo.ide.R
import com.neo.ide.activities.MainActivity
import com.neo.ide.download.SetupState
import com.neo.ide.setup.TerminalSetupActivity
import org.json.JSONArray
import org.json.JSONObject

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var nextBtn: Button
    private lateinit var skipBtn: TextView
    private lateinit var pageIndicator: TextView

    private val greetingFragment = GreetingFragment()
    private val permissionsFragment = PermissionsFragment()
    private val setupConfigFragment = SetupConfigFragment()

    private val fragments = listOf(greetingFragment, permissionsFragment, setupConfigFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.onboarding_viewpager)
        nextBtn = findViewById(R.id.onboarding_next_btn)
        skipBtn = findViewById(R.id.onboarding_skip_btn)
        pageIndicator = findViewById(R.id.onboarding_page_indicator)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }
        viewPager.isUserInputEnabled = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateUI(position)
            }
        })

        nextBtn.setOnClickListener {
            val current = viewPager.currentItem
            when {
                current == 0 -> viewPager.currentItem = 1
                current == 1 && isPermissionsGranted() -> viewPager.currentItem = 2
                current == 2 -> startSetup()
            }
        }

        skipBtn.setOnClickListener {
            if (isPermissionsGranted()) {
                startMainActivity()
            }
        }

        updateUI(0)
    }

    private fun isPermissionsGranted(): Boolean {
        return permissionsFragment.isAdded && permissionsFragment.allPermissionsGranted()
    }

    private fun updateUI(position: Int) {
        pageIndicator.text = "${position + 1} / ${fragments.size}"

        when (position) {
            0 -> {
                nextBtn.text = "Next"
                skipBtn.visibility = View.VISIBLE
            }
            1 -> {
                nextBtn.text = if (isPermissionsGranted()) "Next" else "Grant Permissions"
                skipBtn.visibility = View.VISIBLE
            }
            2 -> {
                nextBtn.text = "Start Setup"
                skipBtn.visibility = View.GONE
            }
        }
    }

    private fun startSetup() {
        val selectedResources = setupConfigFragment.getSelectedResources()
        val autoInstall = setupConfigFragment.isAutoInstall()

        if (selectedResources.isEmpty()) {
            Toast.makeText(this, "Select at least one resource to install", Toast.LENGTH_SHORT).show()
            return
        }

        SetupState.setOnboardingComplete(this, true)

        if (autoInstall) {
            val resourcesJson = JSONArray()
            for (res in selectedResources) {
                val obj = JSONObject().apply {
                    put("name", res.name)
                    put("category", res.category)
                    put("version", res.version)
                    put("url", res.url)
                    put("size", res.size)
                    put("sha256", res.sha256)
                    put("format", res.format)
                    put("destination", res.destination)
                }
                resourcesJson.put(obj)
            }

            val intent = Intent(this, TerminalSetupActivity::class.java).apply {
                putExtra("selected_resources", resourcesJson.toString())
            }
            startActivity(intent)
        } else {
            startMainActivity()
        }
        finish()
    }

    private fun startMainActivity() {
        SetupState.setOnboardingComplete(this, true)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (viewPager.currentItem == 1) {
            nextBtn.text = if (isPermissionsGranted()) "Next" else "Grant Permissions"
        }
    }
}
