package com.ui.rakshakawatch

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Ensure findViewById is called after setContentView
        val textView = findViewById<TextView>(R.id.runningTxt)
        textView.isSelected = true

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Use Handler with Looper.getMainLooper() to avoid deprecation
        Handler(Looper.getMainLooper()).postDelayed({
            val iSplash = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(iSplash)
            finish()
        }, 3500)
    }
}