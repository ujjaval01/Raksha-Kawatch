package com.ui.rakshakawatch.fragments

import MapFragment
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
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
import com.ui.rakshakawatch.sosBackend.ApiClient
import com.ui.rakshakawatch.sosBackend.SosRequest
import com.ui.rakshakawatch.sosBackend.SosResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class HomeFragment : Fragment() {

    private val CALL_PERMISSION_REQUEST = 1
    private val LOCATION_PERMISSION_REQUEST = 1001

    private lateinit var currentLocation: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var homeProfileImage: ImageView
    private lateinit var homeProfileText: TextView
    private lateinit var btnSOS: ImageView
    private lateinit var sosProgressBar: ProgressBar
    private lateinit var homeFragmentLoader: ProgressBar
    // Flags to manage loading
    private var profileNameLoaded: Boolean = false
    private var profileImageLoaded: Boolean = false
    private var locationLoaded: Boolean = false


    private var pendingPhoneCall: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        activity?.findViewById<View>(R.id.bottomNavigation)?.visibility = View.VISIBLE

        btnSOS = view.findViewById(R.id.btnSOS)
        Glide.with(this).asGif().load(R.drawable.gif_sos).into(btnSOS)
        btnSOS.setOnClickListener { sendSOSAlert() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        sosProgressBar = view.findViewById(R.id.sosProgressBar)
        homeFragmentLoader = view.findViewById(R.id.homeFragmentLoader)
        homeProfileImage = view.findViewById(R.id.homeProfileImage)
        homeProfileText = view.findViewById(R.id.homeProfileText)
        currentLocation = view.findViewById(R.id.currentLocation)

        // Show loader and initialize flags
        homeFragmentLoader.visibility = View.VISIBLE
        profileNameLoaded = false
        profileImageLoaded = false
        locationLoaded = false

        fetchProfileImage()
        fetchProfileText()
        getUserLocation()

        view.findViewById<LinearLayout>(R.id.profile).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.mapLocation).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapFragment())
                .addToBackStack(null)
                .commit()
        }

        val emergencyButtons = mapOf(
            R.id.btnPolice to "112",
            R.id.btnWomenHelp to "1091",
            R.id.btnFire to "101",
            R.id.btnAmbulance to "108",
            R.id.btnRailway to "139",
            R.id.btnRoadAcc to "1073",
            R.id.btnChildHelp to "1098",
            R.id.btnCyberCrime to "1930",
            R.id.btnTrainAcc to "1072",
            R.id.btnDemo2 to "9690020293",
            R.id.btnDemo3 to "9690020293",
            R.id.btnDemo4 to "9690020293"
        )

        emergencyButtons.forEach { (id, number) ->
            view.findViewById<Button>(id)?.setOnClickListener { makeCall(number) }
        }

        return view
    }

    @SuppressLint("MissingPermission")
    private fun sendSOSAlert() {
        sosProgressBar.visibility = View.VISIBLE

        val uid = firebaseAuth.currentUser?.uid ?: return

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val mapsUrl = "https://www.google.com/maps?q=$latitude,$longitude"

                val message = """
                    🚨 SOS Alert! 🚨
                    
                    I am in danger and need immediate help.
                    My Live Location: $mapsUrl
                    
                    Please help me immediately!
                """.trimIndent()

                db.collection("users").document(uid)
                    .collection("emergencyContacts")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (doc in documents) {
                            val phone = doc.getString("phone") ?: continue
                            val sosRequest = SosRequest(to = phone, message = message)

                            ApiClient.instance.sendSos(sosRequest)
                                .enqueue(object : Callback<SosResponse> {
                                    override fun onResponse(call: Call<SosResponse>, response: Response<SosResponse>) {
                                        // Optional: handle response
                                    }

                                    override fun onFailure(call: Call<SosResponse>, t: Throwable) {
                                        Toast.makeText(requireContext(), "Failed to send SOS to $phone", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }

                        sosProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "SOS Sent to All Guardians!", Toast.LENGTH_SHORT).show()
                    }

                    .addOnFailureListener {
                        sosProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error fetching contacts", Toast.LENGTH_SHORT).show()
                    }

            } else {
                sosProgressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<View>(R.id.bottomNavigation)?.visibility = View.VISIBLE
        fetchProfileImage()
    }

    private fun makeCall(number: String) {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            pendingPhoneCall = number
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST
            )
            Toast.makeText(requireContext(), "Call permission is required.", Toast.LENGTH_SHORT).show()
        } else {
            try {
                startActivity(callIntent)
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Permission error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchProfileText() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                homeProfileText.text = document.getString("name") ?: "User"
                profileNameLoaded = true
                checkIfDataLoaded()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                profileNameLoaded = true
                checkIfDataLoaded()
            }
    }

    private fun fetchProfileImage() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val base64String = document.getString("profilePicBase64")
                if (!base64String.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    Glide.with(this).load(bitmap).circleCrop().into(homeProfileImage)
                }
                profileImageLoaded = true
                checkIfDataLoaded()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                profileImageLoaded = true
                checkIfDataLoaded()
            }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                getAddressFromLocation(location.latitude, location.longitude)
            } else {
                currentLocation.text = "Unable to fetch location"
                locationLoaded = true
                checkIfDataLoaded()
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val cityName = addresses?.firstOrNull()?.locality ?: "Unknown Location"
            currentLocation.text = cityName
            requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
                .edit().putString("current_location", cityName).apply()
            locationLoaded = true
            checkIfDataLoaded()
        } catch (e: IOException) {
            currentLocation.text = "Error fetching address"
            locationLoaded = true
            checkIfDataLoaded()
        }
    }

    private fun checkIfDataLoaded() {
        if (profileNameLoaded && profileImageLoaded && locationLoaded) {
            homeFragmentLoader.visibility = View.GONE
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CALL_PERMISSION_REQUEST -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                    pendingPhoneCall?.let {
                        makeCall(it)
                        pendingPhoneCall = null
                    }
                } else {
                    Toast.makeText(requireContext(), "Call permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    currentLocation.text = "Permission Denied!"
                    locationLoaded = true
                    checkIfDataLoaded()
                }
            }
        }
    }
}
