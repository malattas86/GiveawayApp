package com.mats.giveawayapp.ui.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModelProvider
import com.mats.giveawayapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var content: View
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        content = findViewById(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("MainActivity", "onCreate: I AM RUNNING ON API 12 or higher")
            content.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean =
                    when {
                        mainViewModel.mockDataLoading() -> {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                            finish()
                            true
                        }
                        else -> false
                    }
            })

            // custom exit on splashScreen
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                // custom animation.
                ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_X,
                    0f,
                    splashScreenView.width.toFloat()
                ).apply {
                    duration = 1000
                    // Call SplashScreenView.remove at the end of your custom animation.
                    doOnEnd {
                        splashScreenView.remove()
                    }
                }.also {
                    // Run your animation.
                    it.start()
                }
            }
        }
        else {
            startActivity(Intent(this@MainActivity, SplashActivity::class.java))
            finish()
        }
    }
}
