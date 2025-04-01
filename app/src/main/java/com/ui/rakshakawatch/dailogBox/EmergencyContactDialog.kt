package com.ui.rakshakawatch.dailogBox

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.R

class EmergencyContactDialog(private val onContactAdded: (String, String, String) -> Unit) : DialogFragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var emgName: EditText
    private lateinit var emgPhone: EditText
    private lateinit var btnEmgSave: Button
    private lateinit var btnEmgCancel: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_contact, null)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emgName = view.findViewById(R.id.emgName)
        emgPhone = view.findViewById(R.id.emgPhone)
        btnEmgSave = view.findViewById(R.id.btnEmgSave)
        btnEmgCancel = view.findViewById(R.id.btnEmgCancel)

        val dialog = Dialog(requireContext())
        dialog.setContentView(view)

        btnEmgSave.setOnClickListener {
            val name = emgName.text.toString().trim()
            val phone = emgPhone.text.toString().trim()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                saveEmergencyContact(name, phone)
            } else {
                Toast.makeText(requireContext(), "Please enter both name and phone number", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmgCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    private fun saveEmergencyContact(name: String, phone: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val contactData = hashMapOf(
            "name" to name,
            "phone" to phone
        )

        db.collection("users").document(uid)
            .collection("emergencyContacts") // Save in sub-collection
            .add(contactData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Contact Saved!", Toast.LENGTH_SHORT).show()
                onContactAdded("", name, phone) // Update UI
                dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}