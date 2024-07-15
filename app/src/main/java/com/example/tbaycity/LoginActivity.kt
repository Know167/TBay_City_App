package com.example.tbaycity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailField:EditText;
    private lateinit var passwordField:EditText;
    private lateinit var loginBtn: Button;
    private  lateinit var signupText:TextView;
    private lateinit var firebaseAuth:FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginbtn)
        signupText = findViewById(R.id.signup);
        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener {
            val email = emailField.text.toString();
            val password = passwordField.text.toString();
            if (email.isEmpty()){
                emailField.error = "Email cannot be empty"
            }
            else if (password.isEmpty()) {

                passwordField.error = "Password cannot be empty"
            }
            else{
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Login Successfull",Toast.LENGTH_LONG).show()
                        val intent = Intent(this,HomeActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}
