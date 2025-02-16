package com.ui.rakshakawatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        val continuePass = findViewById<Button>(R.id.continuePass)
        val backBtn = findViewById<ImageView>(R.id.backArrow)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backBtn.setOnClickListener {
            val intent = Intent(this,EmailVerificationActivity::class.java)
            startActivity(intent)
            finish()
        }
        continuePass.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}