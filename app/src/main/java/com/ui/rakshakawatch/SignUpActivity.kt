package com.ui.rakshakawatch
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.ui.rakshakawatch.R.layout.activity_sign_up


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val goLoginPage = findViewById<LinearLayout>(R.id.goLogin)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val phoneLayout = findViewById<TextInputLayout>(R.id.phoneLayout)
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

        backArrow.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        goLoginPage.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radioEmail) {
                emailLayout.visibility = View.VISIBLE
                phoneLayout.visibility = View.GONE
            } else {
                emailLayout.visibility = View.GONE
                phoneLayout.visibility = View.VISIBLE
            }
        }



    }
}