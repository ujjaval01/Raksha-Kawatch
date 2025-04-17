package com.ui.rakshakawatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
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
import com.google.firebase.auth.FirebaseAuth
import com.ui.rakshakawatch.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //creating instance of firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //  store the login status when the user logs in
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()

        binding.loginBtn.setOnClickListener{
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.goSignUp.setOnClickListener {
            val signupIntent = Intent(this, SignUpActivity::class.java)
        }

        val bounceText =findViewById<TextView>(R.id.bounceText)
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
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        bounceText.startAnimation(bounceAnimation)
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