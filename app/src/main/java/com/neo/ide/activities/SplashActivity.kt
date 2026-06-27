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

package com.neo.ide.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.neo.ide.R
import com.neo.ide.app.BaseActivity
import com.neo.ide.download.SetupState
import com.neo.ide.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private val SPLASH_DURATION = 2500L
    override val enableSystemBarTheming = false

    override fun bindLayout(): View {
        return layoutInflater.inflate(R.layout.activity_splash, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lottieAnimation = findViewById<LottieAnimationView>(R.id.splash_lottie)
        val appName = findViewById<TextView>(R.id.splash_app_name)
        val version = findViewById<TextView>(R.id.splash_version)

        lottieAnimation.setAnimation("splash.json")
        lottieAnimation.playAnimation()

        appName.alpha = 0f
        appName.animate()
            .alpha(1f)
            .setStartDelay(500)
            .setDuration(600)
            .start()

        version.alpha = 0f
        version.animate()
            .alpha(1f)
            .setStartDelay(800)
            .setDuration(600)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNext()
        }, SPLASH_DURATION)
    }

    private fun navigateToNext() {
        val destination = when {
            !SetupState.isOnboardingComplete(this) -> OnboardingActivity::class.java
            !SetupState.arePermissionsGranted(this) -> OnboardingActivity::class.java
            !SetupState.isSetupComplete(this) -> OnboardingActivity::class.java
            else -> MainActivity::class.java
        }

        startActivity(Intent(this, destination))
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
