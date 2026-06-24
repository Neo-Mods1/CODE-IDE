package com.neo.ide.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.neo.ide.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.splash_logo)
        val appName = findViewById<TextView>(R.id.splash_app_name)
        val version = findViewById<TextView>(R.id.splash_version)

        val scaleAnim = ScaleAnimation(
            0.5f, 1f, 0.5f, 1f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
        }

        val alphaAnim = AlphaAnimation(0f, 1f).apply {
            duration = 600
        }

        val animSet = AnimationSet(false).apply {
            addAnimation(scaleAnim)
            addAnimation(alphaAnim)
            interpolator = DecelerateInterpolator()
        }

        logo.startAnimation(animSet)

        val textAlpha = AlphaAnimation(0f, 1f).apply {
            startOffset = 400
            duration = 600
        }
        appName.startAnimation(textAlpha)

        val versionAlpha = AlphaAnimation(0f, 1f).apply {
            startOffset = 600
            duration = 600
        }
        version.startAnimation(versionAlpha)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, PermissionsActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, SPLASH_DURATION)
    }
}
