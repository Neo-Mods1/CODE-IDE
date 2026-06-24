package com.neo.ide.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.neo.ide.R
import com.neo.ide.download.SetupState
import com.neo.ide.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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
