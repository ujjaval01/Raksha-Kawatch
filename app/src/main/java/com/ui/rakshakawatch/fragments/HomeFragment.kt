
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.fragments.ProfileFragment

class HomeFragment : Fragment() {

    private val CALL_PERMISSION_REQUEST = 1
    private lateinit var currentLocation: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the TextView for showing the current location
        currentLocation = view.findViewById(R.id.currentLocation)


        // Retrieve the saved location data from SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val locationName = sharedPref.getString("current_location", "Location not found")
        currentLocation.text = locationName

        // Listen for location updates from MapFragment
        parentFragmentManager.setFragmentResultListener("locationRequestKey", this) { _, bundle ->
            val updatedLocation = bundle.getString("current_location", "Location not found")
            currentLocation.text = updatedLocation
        }

        // Find LinearLayouts and Buttons by their IDs
        val mapLocation = view.findViewById<LinearLayout>(R.id.mapLocation)
        val btnPolice = view.findViewById<Button>(R.id.btnPolice)
        val btnWomenSafety = view.findViewById<Button>(R.id.btnWomenSafety)
        val profile = view.findViewById<ImageView>(R.id.profile)

        profile.setOnClickListener {
            val profileFragment = ProfileFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, profileFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnPolice.setOnClickListener {
            makeCall100()
        }

        btnWomenSafety.setOnClickListener {
            makeCall1090()
        }

        mapLocation.setOnClickListener {
            val mapFragment = MapFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, mapFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }



    private fun makeCall100() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:100")
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), CALL_PERMISSION_REQUEST)
        } else {
            startActivity(callIntent)
        }
    }

    private fun makeCall1090() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:1090")
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), CALL_PERMISSION_REQUEST)
        } else {
            startActivity(callIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CALL_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall100()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
