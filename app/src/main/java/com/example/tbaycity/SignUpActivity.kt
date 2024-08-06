package com.example.tbaycity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
class SignUpActivity : AppCompatActivity() {

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
        var loadingAlert = LoadingAlert(this)
        imageUri = createImageUri()

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
            }
            else if(password.length < 7){
                passwordField.error = "Password with length less than 7 is not accepted"
            }
            else if (confirmPassword.isEmpty()) {
                confirmPasswordField.error = "Confirm Password cannot be empty"
            } else if (password != confirmPassword) {
                confirmPasswordField.error = "Passwords do not match"
            } else {
                loadingAlert.startAlertDialog()
                createAccount(name, email, password) { success ->
                    loadingAlert.dismissAlertDialog()
                    if (success) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Sign Up failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(name: String, email: String, password: String, callback: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        val documentReference = db.collection("Users").document(userId.toString())
                        val userData = HashMap<String, Any>()
                        userData["userName"] = name
                        userData["userEmail"] = email
                        Log.d("username", userData.toString())

                        uploadImageToStorage(userId.toString()) { imageUrl ->
                            if (imageUrl != null) {
                                userData["profileImageUrl"] = imageUrl
                                documentReference.set(userData).addOnSuccessListener {
                                    saveUserDataToSharedPreferences(name, email, imageUrl) {
                                        callback(true)
                                    }
                                }.addOnFailureListener {
                                    callback(false)
                                }
                            } else {
                                callback(false)
                            }
                        }
                    } else {
                        callback(false)
                        Log.d("Connection", "No Internet Connection")
                    }
                } else {
                    callback(false)
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

    private fun uploadImageToStorage(userId: String, callback: (String?) -> Unit) {
        val storageRef = storage.reference.child("users/$userId/profile.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }.addOnFailureListener {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
                Toast.makeText(this, "Image upload failed.", Toast.LENGTH_LONG).show()
            }
    }
    private fun saveUserDataToSharedPreferences(name: String, email: String, imageUrl: String, callback: () -> Unit) {
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putString("name", name)
        myEdit.putString("email", email)
        myEdit.putString("profileImageUrl", imageUrl)
        myEdit.apply()
        callback()
    }
}
