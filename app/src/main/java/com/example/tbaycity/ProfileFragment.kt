package com.example.tbaycity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.android.material.progressindicator.CircularProgressIndicator

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var editIcon: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var updateProfileButton: Button
    private lateinit var circularProgressIndicator: CircularProgressIndicator // Add this line

    private var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val pickImageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImage.setImageURI(uri)
            imageUri = uri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImage)
        editIcon = view.findViewById(R.id.editIcon)
        nameEditText = view.findViewById(R.id.name)
        emailEditText = view.findViewById(R.id.profileEmail)
        updateProfileButton = view.findViewById(R.id.updateProfileButton)
        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator) // Initialize the CircularProgressIndicator

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        downloadImage(profileImage)
        editIcon.setOnClickListener { pickImage() }
        updateProfileButton.setOnClickListener { updateProfile() }

        return view
    }

    private fun pickImage() {
        pickImageContract.launch("image/*")
    }

    private fun updateProfile() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()

        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            nameEditText.requestFocus()
            return
        }

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            emailEditText.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Enter a valid email"
            emailEditText.requestFocus()
            return
        }

        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            val documentReference = db.collection("Users").document(userId)
            val userData = HashMap<String, Any>()
            userData["userName"] = name
            userData["userEmail"] = email

            // Show CircularProgressIndicator
            circularProgressIndicator.visibility = View.VISIBLE

            if (imageUri != null) {
                uploadImageToStorage(userId) { success ->
                    if (success) {
                        updateUserInfoInDatabase(documentReference, userData)
                    } else {
                        Toast.makeText(activity, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                        circularProgressIndicator.visibility = View.GONE // Hide CircularProgressIndicator
                    }
                }
            } else {
                updateUserInfoInDatabase(documentReference, userData)
            }
        }
    }

    private fun uploadImageToStorage(userId: String, callback: (Boolean) -> Unit) {
        val storageRef = storage.reference.child("users/$userId/profile.jpg")
        imageUri?.let {
            storageRef.putFile(it)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        }
    }

    private fun updateUserInfoInDatabase(documentReference: DocumentReference, userData: HashMap<String, Any>) {
        documentReference.update(userData)
            .addOnSuccessListener {
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                circularProgressIndicator.visibility = View.GONE // Hide CircularProgressIndicator
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                circularProgressIndicator.visibility = View.GONE // Hide CircularProgressIndicator
            }
    }

    private fun downloadImage(profileIcon: ImageView) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("users/$userId/profile.jpg")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions().transform(RoundedCorners(16))) // 16 is the corner radius
                .into(profileIcon)
        }.addOnFailureListener {
            // Handle failure here
            Toast.makeText(activity, "Failed to load profile image", Toast.LENGTH_SHORT).show()
        }
    }
}
