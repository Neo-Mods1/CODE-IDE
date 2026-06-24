package com.neo.ide.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.neo.ide.R
import com.neo.ide.activities.MainActivity
import com.neo.ide.download.SetupState
import com.neo.ide.setup.TerminalSetupActivity

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
                current == 1 && permissionsFragment.allPermissionsGranted() -> viewPager.currentItem = 2
                current == 2 -> startSetup()
            }
        }

        skipBtn.setOnClickListener {
            if (permissionsFragment.allPermissionsGranted()) {
                startMainActivity()
            }
        }

        updateUI(0)
    }

    private fun updateUI(position: Int) {
        pageIndicator.text = "${position + 1} / ${fragments.size}"

        when (position) {
            0 -> {
                nextBtn.text = "Next"
                skipBtn.visibility = View.VISIBLE
            }
            1 -> {
                nextBtn.text = if (permissionsFragment.allPermissionsGranted()) "Next" else "Grant Permissions"
                skipBtn.visibility = View.VISIBLE
            }
            2 -> {
                nextBtn.text = "Start Setup"
                skipBtn.visibility = View.GONE
            }
        }
    }

    private fun startSetup() {
        val args = setupConfigFragment.getSetupArgs()
        val autoInstall = setupConfigFragment.isAutoInstall()

        SetupState.setOnboardingComplete(this, true)

        if (autoInstall) {
            val intent = Intent(this, TerminalSetupActivity::class.java).apply {
                putExtra("setup_args", args)
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
            nextBtn.text = if (permissionsFragment.allPermissionsGranted()) "Next" else "Grant Permissions"
        }
    }
}
