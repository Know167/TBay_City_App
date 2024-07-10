package com.example.tbaycity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var emailField:EditText;
    private lateinit var passwordField:EditText;
    private lateinit var loginBtn: Button;
    private  lateinit var signupText:TextView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginbtn)
        signupText = findViewById(R.id.signup);
        loginBtn.setOnClickListener {
            val email = emailField.text.toString();
            val password = passwordField.text.toString();
            if (email.length == 0){
                emailField.error = "Email cannot be empty"
            }
            else if (password.length == 0) {

                passwordField.error = "Password cannot be empty"
            }
            else{
                //Database code to be implemented
            }
        }
    }
}
