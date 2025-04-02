package com.ui.rakshakawatch

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // declared id's
        val SplashImg = findViewById<ImageView>(R.id.SplashImg)
        Glide.with(this).asGif().load(R.drawable.gif_protection).into(SplashImg)
        val textView = findViewById<TextView>(R.id.runningTxt)
        textView.isSelected = true

        // Use Handler with Looper.getMainLooper() to avoid deprecation
        Handler(Looper.getMainLooper()).postDelayed({
            val auth = FirebaseAuth.getInstance()

            if (auth.currentUser != null) {
                // User is already logged in, open MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not logged in, open LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish() // Close SplashActivity
        }, 2500) // 2.5 seconds delay
    }
}
