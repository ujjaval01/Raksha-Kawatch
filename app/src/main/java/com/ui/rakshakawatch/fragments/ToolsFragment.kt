
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.dailogBox.EmergencyContactDialog
import com.ui.rakshakawatch.fragments.HomeFragment

class FragmentTools : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var emergencyContactContainer: GridLayout
    private lateinit var btnAddContact: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnAddContact = view.findViewById(R.id.btnAddContact)
        emergencyContactContainer = view.findViewById(R.id.emergencyContactContainer)

        // Add new contact
        btnAddContact.setOnClickListener {
            EmergencyContactDialog(requireContext(), onSave = { name, phone ->
                saveEmergencyContact(name, phone)
            })
        }

        loadEmergencyContacts()

        // Handle system back press inside this fragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
        })

        return view
    }

    private fun formatPhoneNumber(number: String): String {
        return if (number.startsWith("+")) number else "+91$number"
    }

    private fun saveEmergencyContact(name: String, phone: String) {
        val formattedPhone = formatPhoneNumber(phone)  // Format phone number
        val userId = firebaseAuth.currentUser?.uid ?: return
        val contactRef = db.collection("users").document(userId)
            .collection("emergencyContacts").document()

        val contactData = hashMapOf(
            "name" to name,
            "phone" to formattedPhone
        )

        contactRef.set(contactData)
            .addOnSuccessListener {
                addEmergencyContactButton(contactRef.id, name, formattedPhone)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to save contact", e)
            }
    }

    private fun loadEmergencyContacts() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("emergencyContacts")
            .get()
            .addOnSuccessListener { documents ->
                emergencyContactContainer.removeAllViews()  // 🔄 Moved inside success listener

                val phoneNumbers = mutableListOf<String>()
                for (document in documents) {
                    val name = document.getString("name") ?: continue
                    val phone = document.getString("phone") ?: continue
                    val contactId = document.id

                    phoneNumbers.add(formatPhoneNumber(phone))

                    addEmergencyContactButton(contactId, name, phone)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to load contacts", e)
            }
    }


    private fun addEmergencyContactButton(contactId: String, name: String, phone: String) {
        val contactButton = MaterialButton(requireContext()).apply {
            text = name
            textSize = 16f
            setPadding(20, 20, 20, 20)
            setBackgroundResource(R.drawable.rounded_edittext)

            setOnClickListener {
                EmergencyContactDialog(requireContext(), contactId, name, phone, onSave = { newName, newPhone ->
                    updateEmergencyContact(contactId, newName, newPhone)
                }, onDelete = {
                    showDeleteConfirmation(contactId, this)
                })
            }
        }

        emergencyContactContainer.addView(contactButton)
    }

    private fun updateEmergencyContact(contactId: String, name: String, phone: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val contactRef = db.collection("users").document(userId)
            .collection("emergencyContacts").document(contactId)

        val updatedData = hashMapOf(
            "name" to name,
            "phone" to phone
        )

        contactRef.update(updatedData as Map<String, Any>)
            .addOnSuccessListener {
                loadEmergencyContacts() // Refresh to update name on button
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to update contact", e)
            }
    }

    private fun showDeleteConfirmation(contactId: String, contactButton: MaterialButton) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Contact")
        builder.setMessage("Are you sure you want to delete this contact?")

        builder.setPositiveButton("Yes") { _, _ ->
            deleteEmergencyContact(contactId, contactButton)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun deleteEmergencyContact(contactId: String, contactButton: MaterialButton) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val contactRef = db.collection("users").document(userId)
            .collection("emergencyContacts").document(contactId)

        contactRef.delete()
            .addOnSuccessListener {
                emergencyContactContainer.removeView(contactButton)
                Log.d("Firestore", "Contact deleted.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to delete contact", e)
            }
    }
}




