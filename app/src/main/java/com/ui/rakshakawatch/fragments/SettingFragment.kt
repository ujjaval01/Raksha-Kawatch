
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.LoginActivity
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.fragments.AboutFragment
import com.ui.rakshakawatch.fragments.HomeFragment
import com.ui.rakshakawatch.fragments.ProfileFragment
import com.ui.rakshakawatch.fragments.subFragments.DataStorageFragment

class SettingFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var settingProfileImage: ImageView // Profile Image in HomeFragment
    private lateinit var settingProfileText: TextView
    private lateinit var settingLanguageImg: ImageView
    private lateinit var settingLogoutImg: ImageView
    private lateinit var settingDeleteImg: ImageView
    private lateinit var settingDarkImg: ImageView
    private lateinit var about: LinearLayout
    private lateinit var dataStorage: LinearLayout
    private lateinit var deleteAccount: TextView
    private lateinit var progressBar: ProgressBar // Spinner to show loading

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        // Initialize views
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        settingProfileImage = view.findViewById(R.id.settingProfileImage)
        settingProfileText = view.findViewById(R.id.settingProfileText)
        settingLanguageImg = view.findViewById(R.id.settingLanguageImg)
        settingDarkImg = view.findViewById(R.id.settingDarkImg)
        deleteAccount = view.findViewById(R.id.deleteAccount)
        about = view.findViewById(R.id.about)
        dataStorage = view.findViewById(R.id.dataStorage)
        progressBar = view.findViewById(R.id.progressBar) // Spinner reference

        // Initialize progress bar
        progressBar.visibility = View.GONE

        // Setup Glide images (as done previously)
        Glide.with(this).asGif().load(R.drawable.gif_around_the_world).into(settingLanguageImg)
        Glide.with(this).asGif().load(R.drawable.gif_night_mode).into(settingDarkImg)

        // Fetch profile image and text
        fetchProfileImage()
        fetchProfileText()

        // Setup Button listeners
        val btnProfile = view.findViewById<LinearLayout>(R.id.profile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Navigate to Profile Fragment
        btnProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .addToBackStack(null)
                .commit()
        }

        // Navigate to About Fragment
        about.setOnClickListener {
            val helpAndSupport = AboutFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, helpAndSupport)
                .addToBackStack(null)
                .commit()
        }

        // Navigate to Data Storage Fragment
        dataStorage.setOnClickListener {
            val dataStorage = DataStorageFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, dataStorage)
                .addToBackStack(null)
                .commit()
        }

        // Logout Button
        btnLogout.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            btnLogout.isEnabled = false
            logoutUser()
        }

        // Delete Account Button
        deleteAccount.setOnClickListener {
            // Show confirmation dialog before deleting
            showDeleteConfirmationDialog()
        }

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

    override fun onResume() {
        super.onResume()
        activity?.findViewById<View>(R.id.bottomNavigation)?.visibility = View.VISIBLE
        fetchProfileImage()
    }
    private fun fadeOutView(view: View, duration: Long = 500) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
            }
            .start()
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Yes") { _, _ ->
                deleteUserAccount() // Proceed with account deletion
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Do nothing, just dismiss the dialog
            }

        builder.create().show()
    }

    private fun deleteUserAccount() {
        val user = firebaseAuth.currentUser

        if (user != null) {
            // Ask user for password input to re-authenticate (for simplicity)
            val passwordInput = EditText(requireContext())
            passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            AlertDialog.Builder(requireContext())
                .setTitle("Re-enter Password")
                .setMessage("Please re-enter your password to delete your account")
                .setView(passwordInput)
                .setPositiveButton("Confirm") { _, _ ->
                    val password = passwordInput.text.toString()
                    val email = user.email

                    if (!email.isNullOrEmpty() && password.isNotEmpty()) {
                        val credential = EmailAuthProvider.getCredential(email, password)

                        progressBar.visibility = View.VISIBLE

                        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                // Delete Firestore user data
                                deleteFirebaseUserData(user.uid)

                                // Clear local data
                                clearLocalData()

                                // Delete FirebaseAuth user
                                user.delete().addOnCompleteListener { deleteTask ->
                                    progressBar.visibility = View.GONE
                                    if (deleteTask.isSuccessful) {
                                        Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(requireContext(), LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        requireActivity().finish()
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to delete account. Try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), "Re-authentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Email or password missing", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }


    private fun deleteFirebaseUserData(userId: String) {
        // Example: Delete user data from Firestore (adjust if you're using Realtime Database)
        db.collection("users").document(userId).delete().addOnCompleteListener {
            if (it.isSuccessful) {
                println("User data removed from Firebase Firestore")
            } else {
                println("Failed to remove user data from Firebase Firestore")
            }
        }
    }

    private fun clearLocalData() {
        // Clear SharedPreferences or any other local data storage
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // If you have any local databases, clear them here.
    }

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
        // Start fade animation for all settings content (except progress bar)
        val settingsLayout = view?.findViewById<ViewGroup>(R.id.settingLayout) // the root layout of your settings (wrap everything inside a parent with this ID)
        settingsLayout?.let { fadeOutView(it) }

        progressBar.visibility = View.VISIBLE
        progressBar.alpha = 0f
        progressBar.animate().alpha(1f).setDuration(500).start() // Fade-in for spinner


        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        },1000
        )

    }
}
