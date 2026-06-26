package com.neo.ide.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.neo.ide.R
import com.neo.ide.activities.MainActivity
import com.neo.ide.app.BaseActivity
import com.neo.ide.download.SetupState
import com.neo.ide.setup.TerminalSetupActivity

class OnboardingActivity : BaseActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var nextBtn: Button
    private lateinit var skipBtn: TextView
    private lateinit var pageIndicator: TextView

    private val allFragments = mutableMapOf<String, Fragment>()
    private var activeFragments = listOf<Fragment>()

    override fun bindLayout(): View {
        return layoutInflater.inflate(R.layout.activity_onboarding, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewPager = findViewById(R.id.onboarding_viewpager)
        nextBtn = findViewById(R.id.onboarding_next_btn)
        skipBtn = findViewById(R.id.onboarding_skip_btn)
        pageIndicator = findViewById(R.id.onboarding_page_indicator)

        buildPages()

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = activeFragments.size
            override fun createFragment(position: Int): Fragment = activeFragments[position]
        }
        viewPager.isUserInputEnabled = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateUI(position)
            }
        })

        nextBtn.setOnClickListener {
            val current = viewPager.currentItem
            if (current < activeFragments.size - 1) {
                viewPager.currentItem = current + 1
            } else {
                startSetup()
            }
        }

        skipBtn.setOnClickListener {
            val current = viewPager.currentItem
            val fragment = activeFragments.getOrNull(current)
            if (fragment is StatisticsFragment) {
                SetupState.setStatisticsSkipped(this, true)
                buildPages()
                viewPager.adapter = object : FragmentStateAdapter(this) {
                    override fun getItemCount() = activeFragments.size
                    override fun createFragment(position: Int): Fragment = activeFragments[position]
                }
                viewPager.currentItem = 0
            }
        }

        updateUI(0)
    }

    private fun buildPages() {
        allFragments.clear()
        val pages = mutableListOf<Fragment>()

        val greeting = GreetingFragment()
        allFragments["greeting"] = greeting
        pages.add(greeting)

        if (!SetupState.isStatisticsSkipped(this)) {
            val stats = StatisticsFragment()
            allFragments["statistics"] = stats
            pages.add(stats)
        }

        if (!SetupState.arePermissionsGranted(this)) {
            val perms = PermissionsFragment()
            allFragments["permissions"] = perms
            pages.add(perms)
        }

        val setup = SetupConfigFragment()
        allFragments["setup"] = setup
        pages.add(setup)

        activeFragments = pages
    }

    private fun isPermissionsGranted(): Boolean {
        return SetupState.arePermissionsGranted(this)
    }

    private fun updateUI(position: Int) {
        val total = activeFragments.size
        pageIndicator.text = "${position + 1} / $total"

        val fragment = activeFragments.getOrNull(position)

        when {
            fragment is StatisticsFragment -> {
                nextBtn.text = "Next"
                skipBtn.visibility = View.VISIBLE
            }
            fragment is PermissionsFragment -> {
                nextBtn.text = if (isPermissionsGranted()) "Next" else "Grant Permissions"
                skipBtn.visibility = View.GONE
            }
            position == total - 1 -> {
                nextBtn.text = "Start Setup"
                skipBtn.visibility = View.GONE
            }
            else -> {
                nextBtn.text = "Next"
                skipBtn.visibility = View.GONE
            }
        }
    }

    private fun startSetup() {
        val setupFragment = allFragments["setup"] as? SetupConfigFragment ?: return
        val autoInstall = setupFragment.isAutoInstall()

        SetupState.setOnboardingComplete(this, true)

        if (autoInstall) {
            val intent = Intent(this, TerminalSetupActivity::class.java).apply {
                putExtra(TerminalSetupActivity.EXTRA_SDK_VERSION, setupFragment.getSelectedSdkVersion())
                putExtra(TerminalSetupActivity.EXTRA_JDK_VERSION, setupFragment.getSelectedJdkVersion())
                putExtra(TerminalSetupActivity.EXTRA_WITH_GIT, setupFragment.isGitSelected())
                putExtra(TerminalSetupActivity.EXTRA_WITH_OPENSSH, setupFragment.isOpensshSelected())
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
        val fragment = activeFragments.getOrNull(viewPager.currentItem)
        if (fragment is PermissionsFragment) {
            nextBtn.text = if (isPermissionsGranted()) "Next" else "Grant Permissions"
        }
    }
}
