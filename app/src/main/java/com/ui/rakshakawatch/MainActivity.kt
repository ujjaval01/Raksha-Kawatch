package com.ui.rakshakawatch

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.ui.rakshakawatch.databinding.ActivityMainBinding
import com.ui.rakshakawatch.fragments.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())  // Load the default fragment

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            binding.bottomNavigation.menu.setGroupCheckable(0, true, true)  // Enable item selection
            when (item.itemId) {
                R.id.bottom_home -> openFragment(HomeFragment())
                R.id.bottom_emergency_contacts -> openFragment(EmergencyContactsFragment())
                R.id.bottom_tools -> openFragment(ToolsFragment())
                R.id.bottom_setting -> openFragment(SettingPrivacyFragment())
            }
            true
        }


        binding.fab.setOnClickListener {
            if (checkLocationPermission()) {
                // Clear selection from BottomNavigationView
                binding.bottomNavigation.menu.setGroupCheckable(0, true, false)
                for (i in 0 until binding.bottomNavigation.menu.size()) {
                    binding.bottomNavigation.menu.getItem(i).isChecked = false
                }
                binding.bottomNavigation.menu.setGroupCheckable(0, true, true)

                // Open the MapFragment
                openFragment(MapFragment())
            } else {
                requestLocationPermission()
            }
        }






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show()
                openFragment(MapFragment())
            } else {
                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> openFragment(DashboardFragment())
            R.id.nav_profile -> openFragment(ProfileFragment())
            R.id.nav_createComplaint -> openFragment(CreateComplainFragment())
            R.id.nav_viewComplaint -> openFragment(ViewComplainFragment())
            R.id.nav_manageGuardians -> openFragment(ManageGuardianFragment())
            R.id.nav_settingPrivacy -> openFragment(SettingPrivacyFragment())
            R.id.nav_aboutUs -> openFragment(AboutFragment())
            R.id.nav_share -> openFragment(ShareFragment())
            R.id.nav_logout -> openFragment(LogoutFragment())
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.slide_out_right
        )
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
