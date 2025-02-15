package com.ui.rakshakawatch
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
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


        val goLoginPage = findViewById<LinearLayout>(R.id.goLogin)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val phoneLayout = findViewById<TextInputLayout>(R.id.phoneLayout)

         ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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