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
import com.bumptech.glide.Glide

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class LoginActivity : AppCompatActivity() {
    private lateinit var emailField:EditText;
    private lateinit var passwordField:EditText;
    private lateinit var loginBtn: Button;
    private  lateinit var signupText:TextView;
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseAuth:FirebaseAuth;
    private lateinit var firebaseFirestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginbtn)
        signupText = findViewById(R.id.signup);
        storage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance();
        var loadingAlert = LoadingAlert(this)
        firebaseFirestore = FirebaseFirestore.getInstance()
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
                loadingAlert.startAlertDialog()
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->

                    if(task.isSuccessful){
                        Toast.makeText(this,"Login Successfull",Toast.LENGTH_LONG).show()
                        getUserData(firebaseAuth,firebaseFirestore){
                                user -> user?.let {

                            val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)

                            val myEdit = sharedPreferences.edit()

                            myEdit.putString("name",it.name)
                            myEdit.putString("email",it.email)
                            myEdit.apply()
                            downloadImage{
                                loadingAlert.dismissAlertDialog()
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        }

                    }
                    else{
                        loadingAlert.dismissAlertDialog()
                        Toast.makeText(this,"Email or Password is wrong",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        signupText.setOnClickListener {
            // Navigate to SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun getUserData(auth: FirebaseAuth, firestore: FirebaseFirestore, callback:(com.example.tbaycity.User?)->Unit){
        val uid = auth.currentUser?.uid
        val db = Firebase.firestore
        if(uid.toString().isNotBlank()){
            if (uid != null) {
                db.collection("Users").document(uid).get().addOnSuccessListener{ document->
                    if(document!=null && document.exists()){
                        val userName = document.getString("userName")
                        val userEmail = document.getString("userEmail")
                        val userProfile = document.getString("profileImageUrl")
                        val user = User(uid.toString(),userName.toString(),userEmail.toString(),userProfile.toString())

                        callback(user)
                    }
                    else{
                        callback(null)
                    }

                }
            }
        }
    }
    fun downloadImage(callback: () -> Unit){
        val userId = firebaseAuth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("users/$userId/profile.jpg")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            Log.d("profile",uri.toString())
            myEdit.putString("profileImageUrl", uri.toString())
            myEdit.apply()
            callback()
        }.addOnFailureListener{
            callback()
        }
    }
}
