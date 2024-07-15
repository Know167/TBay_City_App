package com.example.tbaycity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var signupBtn: Button
    private lateinit var loginText: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        emailField = findViewById(R.id.signup_email)
        passwordField = findViewById(R.id.signup_password)
        confirmPasswordField = findViewById(R.id.signup_confirm_password)
        signupBtn = findViewById(R.id.signup_btn)
        loginText = findViewById(R.id.login)

        signupBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (email.isEmpty()) {
                emailField.error = "Email cannot be empty"
            } else if (password.isEmpty()) {
                passwordField.error = "Password cannot be empty"
            } else if (confirmPassword.isEmpty()) {
                confirmPasswordField.error = "Confirm Password cannot be empty"
            } else if (password != confirmPassword) {
                confirmPasswordField.error = "Passwords do not match"
            } else {
                createAccount(email, password)
            }
        }

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Sign Up successful.", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Sign Up failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
