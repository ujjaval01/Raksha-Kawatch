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
import com.google.firebase.auth.FirebaseAuth
import com.ui.rakshakawatch.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.continueSignup.setOnClickListener {
            val name = binding.nameSignup.text.toString()
            val phone = binding.phoneLayout.editText?.text.toString()
            val email = binding.emailSignup.editText?.text.toString()
            val password = binding.passwordSignup.text.toString()
            val conformPassword = binding.conformPasswordSignup.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && conformPassword.isNotEmpty()) {
                if (password == conformPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.goLogin.setOnClickListener {
            val loginIntent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }



        val backArrow = findViewById<ImageView>(R.id.backArrow)
        val goLoginPage = findViewById<LinearLayout>(R.id.goLogin)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val phoneLayout = findViewById<TextInputLayout>(R.id.phoneLayout)
        val googleBtn = findViewById<ImageView>(R.id.googleBtnSignup)
        val facebookBtn = findViewById<ImageView>(R.id.facebookBtnSignup)

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