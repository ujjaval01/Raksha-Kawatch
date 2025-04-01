
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.LoginActivity
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.fragments.ProfileFragment


class SettingFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var settingProfileImage: ImageView // Profile Image in HomeFragment
    private lateinit var settingProfileText: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        settingProfileImage = view.findViewById(R.id.settingProfileImage)
        settingProfileText = view.findViewById(R.id.settingProfileText)
        // Find Buttons
        val btnProfile = view.findViewById<LinearLayout>(R.id.profile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        fetchProfileImage() // Fetch and display profile image
        fetchProfileText()  // Fetch and display profile text

//      Open Profile Fragment
        btnProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .addToBackStack(null)
                .commit()
        }

        // Logout Button
        btnLogout.setOnClickListener {
            showToast("Logout button clicked")
            logoutUser()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun fetchProfileText() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("name") ?: "User"
                    settingProfileText.text = userName // Display username
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchProfileImage() {
        val uid = firebaseAuth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val base64String = document.getString("profilePicBase64")
                    if (!base64String.isNullOrEmpty()) {
                        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        // Use Glide to set circular image
                        Glide.with(this)
                            .load(bitmap) // Load the actual bitmap
                            .circleCrop() // Apply circle transformation
                            .into(settingProfileImage) // Set image into ImageView
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logoutUser() {
        firebaseAuth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
