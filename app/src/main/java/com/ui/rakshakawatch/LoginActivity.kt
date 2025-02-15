package com.ui.rakshakawatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
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

        val goSignUp = findViewById<LinearLayout>(R.id.goSignUp)
        val radioGroup1 = findViewById<RadioGroup>(R.id.radioGroup1)
        val emailLayout1 = findViewById<TextInputLayout>(R.id.emailLayout1)
        val phoneLayout1 = findViewById<TextInputLayout>(R.id.phoneLayout1)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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