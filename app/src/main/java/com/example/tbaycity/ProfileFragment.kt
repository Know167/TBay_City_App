package com.example.tbaycity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.FirebaseError
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var editIcon: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var updateProfileButton: Button
    private lateinit var deleteProfileButton: Button
    private lateinit var signOutButton: Button

    private lateinit var imageUri: Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var loadingAlert: LoadingAlert
    private lateinit var userName:String
    private lateinit var userEmail:String
    private lateinit var  userProfileURl:String
    private val takePictureContract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        profileImage.setImageURI(null)
        profileImage.setImageURI(imageUri)
    }

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
        deleteProfileButton = view.findViewById(R.id.deleteProfileButton)
        signOutButton = view.findViewById(R.id.signOutButton)
        loadingAlert = LoadingAlert(requireActivity())
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        imageUri = createImageUri()

        editIcon.setOnClickListener { pickImage() }
        updateProfileButton.setOnClickListener { updateProfile() }
        deleteProfileButton.setOnClickListener { showDeleteConfirmationDialog() }
        signOutButton.setOnClickListener { signOutUser() }
        val uid = auth.currentUser?.uid.toString()
        if(uid.isNotBlank()){
                db.collection("Users").document(uid).get().addOnSuccessListener{ document->
                    if(document!=null && document.exists()){
                        userName = document.getString("userName").toString()
                        userEmail = document.getString("userEmail").toString()
                        userProfileURl = document.getString("profileImageUrl").toString()
                        val user = User(uid,userName,userEmail,userProfileURl)
                        nameEditText.setText(userName)
                        emailEditText.setText(userEmail)
                        Glide.with(requireContext()).load(document.getString("profileImageUrl")).into(profileImage)

                    }
                    else{

                    }

                }

        }
        return view
    }

    private fun pickImage() {
        showImagePickerDialog()
    }
    private fun updateProfile(){
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
        val user = auth.currentUser
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Enter a valid email"
            emailEditText.requestFocus()
            return
        }
        if (user != null) {
            val userId = user.uid
            val documentReference = db.collection("Users").document(userId)
            val userData = HashMap<String, Any>()
            userData["userName"] = name
            userData["userEmail"] = email


            loadingAlert.startAlertDialog()
            if (imageUri != null) {
                uploadImageToStorage(userId) {success, urlimage ->
                    userData["profileImageUrl"] = urlimage.toString() // Add the image URL to userData
                    updateUserInfoInDatabase(documentReference, userData)
                }
            } else {
                userData["profileImageUrl"] = userProfileURl
                updateUserInfoInDatabase(documentReference, userData)
            }
        }
    }
//    private fun updateProfile() {
//        val name = nameEditText.text.toString().trim()
//        val email = emailEditText.text.toString().trim()
//
//        if (name.isEmpty()) {
//            nameEditText.error = "Name is required"
//            nameEditText.requestFocus()
//            return
//        }
//
//        if (email.isEmpty()) {
//            emailEditText.error = "Email is required"
//            emailEditText.requestFocus()
//            return
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            emailEditText.error = "Enter a valid email"
//            emailEditText.requestFocus()
//            return
//        }
//
//        val user = auth.currentUser
//
//        if (user != null) {
//            val userId = user.uid
//            val documentReference = db.collection("Users").document(userId)
//
//            // Retrieve current user data
//            documentReference.get().addOnSuccessListener { document ->
//                val existingData = document?.data ?: emptyMap<String, Any>()
//                val userData = HashMap<String, Any>()
//
//                if (name != existingData["userName"]) {
//                    userData["userName"] = name
//                }
//
//                if (email != existingData["userEmail"]) {
//                    userData["userEmail"] = email
//                }
//
//                // Show loading alert before starting the profile update process
//                loadingAlert.startAlertDialog()
//
//                // Check if the image has been updated
//                if (::imageUri.isInitialized) {
//                    uploadImageToStorage(userId) { imageURL ->
//                        userData["profileImageUrl"] = imageURL
//                        updateUserInfoInDatabase(documentReference, userData)
//                    }
//                } else {
//                    // No new image provided, use existing image URL
//                    userData["profileImageUrl"] = existingData["profileImageUrl"].toString()
//                    updateUserInfoInDatabase(documentReference, userData)
//                }
//            }
//        }
//    }
//
//
    private fun updateUserInfoInDatabase(documentReference: DocumentReference, userData: HashMap<String, Any>) {
        documentReference.update(userData)
            .addOnSuccessListener {
                val sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("name", userData["userName"].toString())
                editor.putString("email", userData["userEmail"].toString())
                editor.putString("profileImageUrl", userData["profileImageUrl"].toString())
                editor.apply()

                // Dismiss the loading alert after successfully updating user info
                loadingAlert.dismissAlertDialog()
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Dismiss the loading alert in case of failure
                loadingAlert.dismissAlertDialog()
                Toast.makeText(activity, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToStorage(userId: String, callback: (Boolean,String?) -> Unit) {
        val storageRef = storage.reference.child("users/$userId/profile.jpg")
        imageUri?.let {
            storageRef.putFile(it)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        callback(true,uri.toString())
                    }.addOnFailureListener {
                        callback(false, null)
                    }
                }
                .addOnFailureListener {
                    callback(false,null)
                    Toast.makeText(requireContext(), "Image upload failed.", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun downloadImage(profileIcon: ImageView) {
//        val userId = auth.currentUser?.uid ?: return
//        val storageRef = storage.reference.child("users/$userId/profile.jpg")
//
//        storageRef.downloadUrl.addOnSuccessListener { uri ->
//            Glide.with(this)
//                .load(uri)
//                .apply(RequestOptions().transform(RoundedCorners(16))) // 16 is the corner radius
//                .into(profileIcon)
//        }.addOnFailureListener {
//            // Handle failure here
//            Toast.makeText(activity, "Failed to load profile image", Toast.LENGTH_SHORT).show()
//        }
    }
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Profile")
            .setMessage("Are you sure you want to delete your profile? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteUser()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUser() {
        val user = auth.currentUser
        val userId = user?.uid.toString()
        loadingAlert.startAlertDialog()

        if (userId.isNotEmpty() && userId.isNotBlank()) {
            db.collection("Users").document(userId).delete().addOnSuccessListener {
                user?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_LONG).show()
                        loadingAlert.dismissAlertDialog()
                        val sharedPreferences = requireContext().getSharedPreferences("UserData",Context.MODE_PRIVATE)
                        sharedPreferences.edit().clear().apply()
                        val i = Intent(activity, LoginActivity::class.java)
                        startActivity(i)
                        activity?.finish()
                    } else {
                        if (task.exception is FirebaseAuthRecentLoginRequiredException) {
                            reauthenticateUser {
                                deleteUser()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error while deleting user: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            Log.e("Firebase", "Error deleting user", task.exception)
                            loadingAlert.dismissAlertDialog()
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                if (exception is FirebaseAuthRecentLoginRequiredException) {
                    reauthenticateUser {
                        deleteUser()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error while deleting user data: ${exception.message}", Toast.LENGTH_LONG).show()
                    Log.e("Firebase", "Error deleting user data", exception)
                    loadingAlert.dismissAlertDialog()
                }
            }
        }
    }

    private fun signOutUser() {
        auth.signOut()
        User("","","","")
        val sharedPreference  = requireContext().getSharedPreferences("UserData",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.clear()
        editor.remove("name")
        editor.remove("email")
        editor.remove("profileImageUrl")
    editor.commit()
        val i = Intent(requireContext(),LoginActivity::class.java)
        startActivity(i)
        requireActivity().finish()
    }

    private fun reauthenticateUser(onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return

        val input = EditText(requireContext())
        input.hint = "Enter your password"

        AlertDialog.Builder(requireContext())
            .setTitle("Reauthenticate")
            .setMessage("Please re-enter your password to continue.")
            .setView(input)
            .setPositiveButton("Confirm") { _, _ ->
                val password = input.text.toString()
                val credential = EmailAuthProvider.getCredential(email, password)

                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Re-authentication successful", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Re-authentication failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Firebase", "Error re-authenticating user", exception)
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> takePictureContract.launch(imageUri)
                1 -> pickImageContract.launch("image/*")
            }
        }
        builder.show()
    }
    private fun createImageUri(): Uri {
        val image = File(requireContext().filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(requireContext(), "com.example.tbaycity.SignUpActivity", image)
    }

}