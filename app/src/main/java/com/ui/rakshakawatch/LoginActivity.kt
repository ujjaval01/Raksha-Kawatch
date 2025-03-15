package com.ui.rakshakawatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val goSignUp = findViewById<LinearLayout>(R.id.goSignUp)
        val radioGroup1 = findViewById<RadioGroup>(R.id.radioGroup)
        val emailLayout1 = findViewById<TextInputLayout>(R.id.emailLayout)
        val phoneLayout1 = findViewById<TextInputLayout>(R.id.phoneLayout)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val googleBtn = findViewById<ImageView>(R.id.googleBtn)
        val facebookBtn = findViewById<ImageView>(R.id.facebookBtn)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        googleBtn.setOnClickListener {
            Toast.makeText(this, "This feature is on maintenance", Toast.LENGTH_SHORT).show()
        }
        facebookBtn.setOnClickListener {
            Toast.makeText(this, "This feature is on maintenance", Toast.LENGTH_SHORT).show()
        }

        forgotPassword.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        backArrow.setOnClickListener {
            val intent = Intent(this, BlankActivity::class.java)
            startActivity(intent)
        }

        goSignUp.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java )
            startActivity(intent)
            finish()
        }
        radioGroup1.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radioEmail) {
                emailLayout1.visibility = View.VISIBLE
                phoneLayout1.visibility = View.GONE
            } else {
                emailLayout1.visibility = View.GONE
                phoneLayout1.visibility = View.VISIBLE
            }
        }
    }
}