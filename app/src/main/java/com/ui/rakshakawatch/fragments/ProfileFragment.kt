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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.R
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profileImage: ImageView
    private lateinit var etName: EditText
    private lateinit var etNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDOB: EditText
    private lateinit var etLocation: EditText
    private lateinit var saveData: TextView

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
        etName = view.findViewById(R.id.etName)
        etNumber = view.findViewById(R.id.etNumber)
        etEmail = view.findViewById(R.id.etEmail)
        etDOB = view.findViewById(R.id.etDOB)
        etLocation = view.findViewById(R.id.etLocation)
        saveData = view.findViewById(R.id.saveData)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            profileImage.setImageURI(selectedImageUri) // Display the selected image

            // Convert selected image to Base64
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
        val uid = firebaseAuth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etName.setText(document.getString("name") ?: "")
                    etEmail.setText(document.getString("email") ?: "")
                    etNumber.setText(document.getString("phone") ?: "")
                    etDOB.setText(document.getString("dob") ?: "")
                    etLocation.setText(document.getString("location") ?: "")

                    val base64String = document.getString("profilePicBase64")
                    if (!base64String.isNullOrEmpty()) {
                        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        profileImage.setImageBitmap(bitmap)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserData() {
        val uid = firebaseAuth.currentUser?.uid ?: return

        val updatedData = hashMapOf(
            "name" to etName.text.toString(),
            "email" to etEmail.text.toString(),
            "phone" to etNumber.text.toString(),
            "dob" to etDOB.text.toString(),
            "location" to etLocation.text.toString()
        )

        // If an image is selected, add it as Base64
        selectedBase64Image?.let {
            updatedData["profilePicBase64"] = it
        }

        db.collection("users").document(uid).set(updatedData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile Updated!", Toast.LENGTH_SHORT).show()
                navigateToHomeFragment()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Update Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHomeFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun uploadProfileImageToFirestore(base64String: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        userRef.update("profilePicBase64", base64String)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile Picture Updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
