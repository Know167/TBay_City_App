package com.example.tbaycity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupText: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginbtn)
        signupText = findViewById(R.id.signup)

        loginBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            if (email.isEmpty()) {
                emailField.error = "Email cannot be empty"
            } else if (password.isEmpty()) {
                passwordField.error = "Password cannot be empty"
            } else {
                loginUser(email, password)
            }
        }

        signupText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT).show()

                    // Navigate to home screen or next activity
                    navigateToHome()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Optional: Call finish() to close LoginActivity after navigating to HomeActivity
    }
}
