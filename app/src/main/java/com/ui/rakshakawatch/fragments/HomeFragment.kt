package com.ui.rakshakawatch.fragments

import MapFragment
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ui.rakshakawatch.R
import java.io.IOException
import java.util.Locale

class HomeFragment : Fragment() {

    private val CALL_PERMISSION_REQUEST = 1
    private val LOCATION_PERMISSION_REQUEST = 1001
    private lateinit var currentLocation: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var homeProfileImage: ImageView // Profile Image in HomeFragment
    private lateinit var homeProfileText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)




        val btnSOS = view.findViewById<ImageView>(R.id.btnSOS)
        Glide.with(this).asGif().load(R.drawable.gif_sos).into(btnSOS)


        homeProfileImage = view.findViewById(R.id.homeProfileImage)
        homeProfileText = view.findViewById(R.id.homeProfileText)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fetchProfileImage() // Fetch and display profile image
        fetchProfileText()  // Fetch and display profile text


        // Initialize location provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Find Views
        val mapLocation = view.findViewById<LinearLayout>(R.id.mapLocation)

        // call btn
        val btnPolice = view.findViewById<Button>(R.id.btnPolice)
        val btnWomenHelp = view.findViewById<Button>(R.id.btnWomenHelp)
        val btnFire = view.findViewById<Button>(R.id.btnFire)
        val btnAmbulance = view.findViewById<Button>(R.id.btnAmbulance)
        val btnRailway = view.findViewById<Button>(R.id.btnRailway)
        val btnRoadAcc = view.findViewById<Button>(R.id.btnRoadAcc)
        val btnChildHelp = view.findViewById<Button>(R.id.btnChildHelp)
        val btnCyberCrime = view.findViewById<Button>(R.id.btnCyberCrime)
        val btnTrainAcc = view.findViewById<Button>(R.id.btnTrainAcc)
        val btnDemo2 = view.findViewById<Button>(R.id.btnDemo2)
        val btnDemo3 = view.findViewById<Button>(R.id.btnDemo3)
        val btnDemo4 = view.findViewById<Button>(R.id.btnDemo4)

        val profile = view.findViewById<LinearLayout>(R.id.profile)
        currentLocation = view.findViewById(R.id.currentLocation)

        // Fetch current location
        getUserLocation()

        profile.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .addToBackStack(null)
                .commit()
        }


        btnPolice.setOnClickListener { makeCall("112") }
        btnWomenHelp.setOnClickListener { makeCall("1091") }
        btnFire.setOnClickListener { makeCall("101") }
        btnAmbulance.setOnClickListener { makeCall("108") }
        btnRailway.setOnClickListener { makeCall("139") }
        btnRoadAcc.setOnClickListener { makeCall("1073") }
        btnChildHelp.setOnClickListener { makeCall("1098") }
        btnCyberCrime.setOnClickListener { makeCall("1930") }
        btnTrainAcc.setOnClickListener { makeCall("1072") }
        btnDemo2.setOnClickListener { makeCall("9690020293") }
        btnDemo3.setOnClickListener { makeCall("9690020293") }
        btnDemo4.setOnClickListener { makeCall("9690020293") }

        mapLocation.setOnClickListener {
            val mapFragment = MapFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack(null)
                .commit()
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
                    homeProfileText.text = userName // Display username
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onResume() {
        super.onResume()
        fetchProfileImage() // Refresh profile image when HomeFragment is opened
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
                            .into(homeProfileImage) // Set image into ImageView
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }




    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                getAddressFromLocation(location.latitude, location.longitude)
            } else {
                currentLocation.text = "Unable to fetch location"
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1)
            } else {
                geocoder.getFromLocation(latitude, longitude, 1)
            }

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val cityName = address.locality ?: address.adminArea ?: address.subLocality ?: "Unknown Location"
                currentLocation.text = cityName

                // Save location in SharedPreferences
                val sharedPref = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("current_location", cityName).apply()
            } else {
                currentLocation.text = "Address not found"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            currentLocation.text = "Error fetching address"
        }
    }

    private fun makeCall(number: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$number")
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), CALL_PERMISSION_REQUEST)
        } else {
            startActivity(callIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CALL_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "Call permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Call permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    currentLocation.text = "Permission Denied!"
                }
            }
        }
    }
}
