import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.ui.rakshakawatch.LoginActivity
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.SignUpActivity


class SettingFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        // Find Buttons
        val btnOpenSignup = view.findViewById<Button>(R.id.btnOpenSignup)
        val btnOpenLogin = view.findViewById<Button>(R.id.btnOpenLogin)
//      val btnOpenContact = view.findViewById<Button>(R.id.btnOpenContact)
        val btnLogout = view.findViewById<Button>(R.id.btnLogoutApp)

        // Open Settings Activity
        btnOpenLogin.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        btnOpenSignup.setOnClickListener {
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            startActivity(intent)
        }

        // Open Contact Fragment
//        btnOpenContact.setOnClickListener {
//            val contactFragment = SettingFragment()
//            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, contactFragment) // Change 'fragment_container' to your actual container ID
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }

        // Logout Button
        btnLogout.setOnClickListener {
            showToast("Logout button clicked")
            logoutUser()
        }

        return view
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
