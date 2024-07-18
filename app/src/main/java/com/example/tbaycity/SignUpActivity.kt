package com.example.tbaycity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class SignUpActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var signupBtn: Button
    private lateinit var loginText: TextView
    private lateinit var nameField: EditText
    private lateinit var profileImageField: ImageView
    private lateinit var profileBtn: Button
    private val PICK_IMAGE = 1
    private lateinit var imageUri: Uri

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private val takePictureContract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        profileImageField.setImageURI(null)
        profileImageField.setImageURI(imageUri)
    }

    private val pickImageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageField.setImageURI(uri)
            imageUri = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        nameField = findViewById(R.id.signup_name)
        emailField = findViewById(R.id.signup_email)
        passwordField = findViewById(R.id.signup_password)
        confirmPasswordField = findViewById(R.id.signup_cpassword)

        profileImageField = findViewById(R.id.signup_profileimg)
        signupBtn = findViewById(R.id.signup_btn)
        loginText = findViewById(R.id.login)

        imageUri = createImageUri()

        val layout: LinearLayout = findViewById(R.id.signup_layout)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
        val params = LinearLayout.LayoutParams(100, 100)
        params.gravity = Gravity.CENTER
        layout.addView(progressBar, params)
        profileImageField.setOnClickListener {
            showImagePickerDialog()
        }

        signupBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()
            val name = nameField.text.toString()
            if (name.isEmpty()) {
                nameField.error = "Name cannot be empty"
            } else if (email.isEmpty()) {
                emailField.error = "Email cannot be empty"
            } else if (password.isEmpty()) {
                passwordField.error = "Password cannot be empty"
            } else if (confirmPassword.isEmpty()) {
                confirmPasswordField.error = "Confirm Password cannot be empty"
            } else if (password != confirmPassword) {
                confirmPasswordField.error = "Passwords do not match"
            } else {
                createAccount(name, email, password)
            }
        }

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(name: String, email: String, password: String) {
        progressBar.visibility= View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    val documentReference = db.collection("Users").document(userId.toString())
                    val userData = HashMap<String, Any>()
                    userData["userName"] = name
                    userData["userEmail"] = email

                    uploadImageToStorage(userId.toString()) { success ->
                        if (success) {
                            documentReference.set(userData).addOnSuccessListener {
                                Toast.makeText(baseContext, "Sign Up successful.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                progressBar.visibility = View.GONE
                                finish()
                            }
                        } else {
                            Toast.makeText(baseContext, "Sign Up failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(baseContext, "Sign Up failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createImageUri(): Uri {
        val image = File(filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(this, "com.example.tbaycity.SignUpActivity", image)
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> takePictureContract.launch(imageUri)
                1 -> pickImageContract.launch("image/*")
            }
        }
        builder.show()
    }

    private fun uploadImageToStorage(userId: String, callback: (Boolean) -> Unit) {
        val storageRef = storage.reference.child("users/$userId/profile.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                callback(true)
                Toast.makeText(this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                callback(false)
                Toast.makeText(this, "Image upload failed.", Toast.LENGTH_LONG).show()
            }
    }
}
