package com.ui.rakshakawatch

import FragmentTools
import HomeFragment
import MapFragment
import SettingFragment
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ui.rakshakawatch.databinding.ActivityMainBinding
import com.ui.rakshakawatch.fragments.ChatbotFragment
import com.zagori.bottomnavbar.BottomNavBar

class MainActivity : AppCompatActivity(), LocationListener, OnMapReadyCallback,
    BottomNavBar.OnBottomNavigationListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setBottomNavigationListener(this)
        if (checkLocationPermission()) {
            fetchLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        }
    }
    private fun fetchLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    saveLocationToSharedPrefs(it)
                    updateMapLocation(it)
                }
            }
        }
    }

    private fun saveLocationToSharedPrefs(location: Location) {
        val currentLocation = "${location.latitude}, ${location.longitude}"
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("currentLocation", currentLocation)
        editor.apply()
        Toast.makeText(this, "Location Updated!", Toast.LENGTH_SHORT).show()
    }

    private fun updateMapLocation(location: Location) {
        if (::googleMap.isInitialized) {
            val userLatLng = LatLng(location.latitude, location.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
            googleMap.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))
        }
    }

    override fun onLocationChanged(location: Location) {
        saveLocationToSharedPrefs(location)
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.scale_rotate_in, R.anim.slide_fade_out, R.anim.fade_in_scale_up, R.anim.slide_down)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        if (checkLocationPermission()) {
            googleMap.isMyLocationEnabled = true
            fetchLocation()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem?): Boolean {
        if (menuItem?.itemId == binding.bottomNavigation.id) return true
         when (menuItem?.itemId) {
            R.id.bottom_home -> {
                openFragment(HomeFragment())
            }
            R.id.bottom_chatbot -> openFragment(ChatbotFragment())
            R.id.bottom_tools -> openFragment(FragmentTools())
            R.id.bottom_setting -> openFragment(SettingFragment())
            R.id.bottom_map -> {
                if (checkLocationPermission()) {
                    openFragment(MapFragment())
                } else {
                    requestLocationPermission()
                }
            }
            else -> null
        }
        return true
    }
}
