package com.zireddinismayilov.languagetranslationapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.zireddinismayilov.languagetranslationapp.R
import com.zireddinismayilov.languagetranslationapp.databinding.ActivitySplashBinding
import java.util.Timer
import java.util.TimerTask

class SpScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        supportActionBar?.hide()
        goToMain()
    }

    private fun goToMain() {
        Handler().postDelayed(Runnable {
            val intent = Intent(this@SpScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

}