import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.dailogBox.EmergencyContactDialog

class FragmentTools : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var emergencyContactContainer: GridLayout
    private lateinit var btnAddContact: MaterialButton
    private lateinit var map: ImageView
    private lateinit var loudSiren: ImageView
    private lateinit var quickVideo: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnAddContact = view.findViewById(R.id.btnAddContact)
        emergencyContactContainer = view.findViewById(R.id.emergencyContactContainer)
        map = view.findViewById(R.id.map)
        loudSiren = view.findViewById(R.id.loudSiren)
        quickVideo = view.findViewById(R.id.quickVideo)

        Glide.with(this).asGif().load(R.drawable.gif_map).into(map)
        Glide.with(this).asGif().load(R.drawable.gif_siren).into(loudSiren)
        Glide.with(this).asGif().load(R.drawable.gif_cameraman).into(quickVideo)

        // Add Contact Button Click Listener
        btnAddContact.setOnClickListener {
            val dialog = EmergencyContactDialog { id, name, phone ->
                saveEmergencyContact(id, name, phone)
            }
            dialog.show(parentFragmentManager, "EmergencyContactDialog")
        }

        // Load saved contacts on startup
        loadEmergencyContacts()

        return view
    }

    // Save the emergency contact to Firestore
    private fun saveEmergencyContact(name: String, phone: String, phone1: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val contactRef = db.collection("users").document(userId)
            .collection("emergencyContacts").document() // 🔹 Firestore auto-generates an ID

        val contactData = hashMapOf(
            "id" to contactRef.id, // Store the Firestore-generated ID
            "name" to name,
            "phone" to phone
        )

        contactRef.set(contactData)
            .addOnSuccessListener {
                addEmergencyContactButton(contactRef.id, name, phone)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to save contact", e)
            }
    }


    // Load contacts from Firestore on app startup
    private fun loadEmergencyContacts() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("emergencyContacts")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.getString("id") ?: continue
                    val name = document.getString("name") ?: continue
                    val phone = document.getString("phone") ?: continue
                    addEmergencyContactButton(id, name, phone)
                }
            }
    }

    // Dynamically add a new contact button
    private fun addEmergencyContactButton(id: String, name: String, phone: String) {
        val contactButton = MaterialButton(requireContext()).apply {
            text = name
            textSize = 16f
            setPadding(20, 20, 20, 20)
            setBackgroundResource(R.drawable.rounded_edittext)

            // Clicking the button will open the dialer with the contact's number
            setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
        }

        emergencyContactContainer.addView(contactButton)
    }
}
