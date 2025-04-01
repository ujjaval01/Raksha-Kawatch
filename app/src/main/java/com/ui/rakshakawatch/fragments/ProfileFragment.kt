package com.ui.rakshakawatch.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.R
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profileImage: ImageView
    private lateinit var setImage: ImageView
    private lateinit var etName: EditText
    private lateinit var etNumber: EditText
    private lateinit var etNumber2: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDOB: EditText
    private lateinit var etLocation: EditText
    private lateinit var saveData: TextView
    private lateinit var profileLoader: ProgressBar
    private lateinit var profileContent: LinearLayout

    private var selectedImageUri: Uri? = null
    private var selectedBase64Image: String? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val backArrow = view.findViewById<ImageView>(R.id.backArrow)
        profileImage = view.findViewById(R.id.profileImage)
        setImage = view.findViewById(R.id.setImage)
        etName = view.findViewById(R.id.etName)
        etNumber = view.findViewById(R.id.etNumber)
        etNumber2 = view.findViewById(R.id.etNumber2)
        etEmail = view.findViewById(R.id.etEmail)
        etDOB = view.findViewById(R.id.etDOB)
        etLocation = view.findViewById(R.id.etLocation)
        saveData = view.findViewById(R.id.saveData)
        profileLoader = view.findViewById(R.id.profileLoader)
        profileContent = view.findViewById(R.id.profileContent)

        firebaseAuth.currentUser?.let {
            fetchUserData()
        } ?: run {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }

        profileImage.setOnClickListener {
            openGallery()
        }
        saveData.setOnClickListener {
            updateUserData()
        }
        backArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data

            // Use Glide to load the image as a circle
            Glide.with(this)
                .load(selectedImageUri)
                .circleCrop()
                .into(profileImage)

            // Convert selected image to Base64 for storage
            selectedBase64Image = encodeImageToBase64(selectedImageUri!!)
        }
    }

    private fun encodeImageToBase64(uri: Uri): String {
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Compress image
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun fetchUserData() {
        profileLoader.visibility = View.VISIBLE  // Show loader
        profileContent.visibility = View.GONE  // Hide content
        val uid = firebaseAuth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etName.setText(document.getString("name") ?: "")
                    etEmail.setText(document.getString("email") ?: "")
                    etNumber.setText(document.getString("phone") ?: "")
                    etNumber2.setText(document.getString("phone2") ?: "")
                    etDOB.setText(document.getString("dob") ?: "")
                    etLocation.setText(document.getString("location") ?: "")

                    val base64String = document.getString("profilePicBase64")
                    if (!base64String.isNullOrEmpty()) {
                        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        // Use Glide to display the circular image
                        Glide.with(this)
                            .load(bitmap)
                            .circleCrop()
                            .into(profileImage)
                    }
                }
                // Delay to show loader for 1-2 seconds
                profileLoader.animate()
                    .alpha(0f) // Fade out loader
                    .setDuration(1000)
                    .withEndAction {
                        profileLoader.visibility = View.GONE

                        profileContent.alpha = 0f
                        profileContent.visibility = View.VISIBLE
                        profileContent.animate()
                            .alpha(1f) // Fade in profile content
                            .setDuration(1000)
                            .start()
                    }.start() // 1.5 seconds
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                profileLoader.visibility = View.GONE  // Hide loader in case of error
                profileContent.visibility = View.VISIBLE  // Show content
            }
    }

    private fun updateUserData() {
        profileLoader.visibility = View.VISIBLE  // Show loader
        profileContent.visibility = View.GONE  // Hide content
        val uid = firebaseAuth.currentUser?.uid ?: return

        // Fetch existing profile picture before updating
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val existingProfilePic = document.getString("profilePicBase64")

                val updatedData = hashMapOf(
                    "name" to etName.text.toString(),
                    "email" to etEmail.text.toString(),
                    "phone" to etNumber.text.toString(),
                    "phone2" to etNumber2.text.toString(),
                    "dob" to etDOB.text.toString(),
                    "location" to etLocation.text.toString(),
                    "profilePicBase64" to (selectedBase64Image ?: existingProfilePic) // Keep old image if no new image is selected
                )

                db.collection("users").document(uid).set(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile Updated!", Toast.LENGTH_SHORT).show()
                        navigateToHomeFragment()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Update Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        // Animate Loader Disappearance & Profile Content Appearance
        profileLoader.animate()
            .alpha(0f) // Fade out loader
            .setDuration(1000)
            .withEndAction {
                profileLoader.visibility = View.GONE

                profileContent.alpha = 0f
                profileContent.visibility = View.VISIBLE
                profileContent.animate()
                    .alpha(1f) // Fade in profile content
                    .setDuration(1000)
                    .start()
            }.start() // 1.5 seconds
    }


    private fun navigateToHomeFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .addToBackStack(null)
            .commit()
    }

//    private fun uploadProfileImageToFirestore(base64String: String) {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
//
//        val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)
//        userRef.update("profilePicBase64", base64String)
//            .addOnSuccessListener {
//                Toast.makeText(requireContext(), "Profile Picture Updated!", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
}
