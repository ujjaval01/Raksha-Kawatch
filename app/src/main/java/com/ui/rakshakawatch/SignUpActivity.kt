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
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val goLoginPage = findViewById<LinearLayout>(R.id.goLogin)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val phoneLayout = findViewById<TextInputLayout>(R.id.phoneLayout)
        val googleBtn = findViewById<ImageView>(R.id.googleBtnSignup)
        val facebookBtn = findViewById<ImageView>(R.id.facebookBtnSignup)

        binding.continueSignup.setOnClickListener {
            val name = binding.nameSignup.text.toString()
            val email = binding.emailSignup.text.toString()
            val phone = binding.phoneSignup.text.toString()
            val password = binding.passwordSignup.text.toString()
            val confirmPassword = binding.conformPasswordSignup.text.toString()

            if (name.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    if (emailLayout.visibility == View.VISIBLE && email.isNotEmpty()) {
                        registerUser(name, email, phone, password, true)
                    } else if (phoneLayout.visibility == View.VISIBLE && phone.isNotEmpty()) {
                        registerUser(name, email, phone, password, false)
                    } else {
                        Toast.makeText(this, "Enter Email or Phone", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

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

        goLoginPage.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioEmail) {
                emailLayout.visibility = View.VISIBLE
                phoneLayout.visibility = View.GONE
            } else {
                emailLayout.visibility = View.GONE
                phoneLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun registerUser(name: String, email: String, phone: String, password: String, isEmail: Boolean) {
        val authMethod = if (isEmail) email else phone
        firebaseAuth.createUserWithEmailAndPassword(authMethod, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = firebaseAuth.currentUser?.uid
                    if (uid != null) {
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to if (isEmail) email else "",
                            "phone" to if (!isEmail) phone else "",
                            "password" to password,
                            "dob" to "",
                            "location" to "",
                            "profilePicUrl" to "",
                            "guardianNumber" to ""
                        )

                        db.collection("users").document(uid).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Signup Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
