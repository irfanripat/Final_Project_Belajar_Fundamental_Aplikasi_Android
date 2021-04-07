package com.irfan.consumerapp.activity.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.irfan.consumerapp.R
import com.irfan.consumerapp.databinding.ActivitySplashScreenBinding
import com.irfan.consumerapp.activity.main.MainActivity


class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appLogo.animate().apply {
            scaleX(2.2f)
            scaleY(2.2f)
            duration = 350
        }

        binding.tvAppName.animate().apply {
            scaleX(1.5f)
            scaleY(1.5f)
            duration = 350
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 2500)
    }
}