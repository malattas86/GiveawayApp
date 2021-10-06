package com.mats.giveawayapp.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.mats.giveawayapp.MyApplication
import com.mats.giveawayapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var test: MyApplication = MyApplication()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
            finish()
        }, 3000)

    }

    override fun onResume() {
        super.onResume()
        test.onMoveToForeground()
    }

    override fun onPause() {
        super.onPause()
        test.onMoveToBackground()
    }
}