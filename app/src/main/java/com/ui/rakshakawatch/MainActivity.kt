package com.ui.rakshakawatch

import FragmentTools
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import com.ui.rakshakawatch.fragments.AboutFragment
import com.ui.rakshakawatch.fragments.CreateComplainFragment
import com.ui.rakshakawatch.fragments.DashboardFragment
import com.ui.rakshakawatch.fragments.EmergencyContactsFragment
import HomeFragment
import com.ui.rakshakawatch.fragments.LogoutFragment
import com.ui.rakshakawatch.fragments.ManageGuardianFragment
import com.ui.rakshakawatch.fragments.MapFragment
import com.ui.rakshakawatch.fragments.ProfileFragment
import com.ui.rakshakawatch.fragments.SettingPrivacyFragment
import com.ui.rakshakawatch.fragments.ShareFragment
import com.ui.rakshakawatch.fragments.ViewComplainFragment

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

        setSupportActionBar(binding.toolbar)
        // Drawer Setup
        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        binding.navigationDrawer.setNavigationItemSelectedListener(this)
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())  // Load the default fragment

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            binding.bottomNavigation.menu.setGroupCheckable(0, true, true)

            val fragment = when (item.itemId) {
                R.id.bottom_home -> HomeFragment()
                R.id.bottom_emergency_contacts -> EmergencyContactsFragment()
                R.id.bottom_tools -> FragmentTools()
                R.id.bottom_setting -> SettingPrivacyFragment()
                else -> null
            }

            fragment?.let { openFragment(it) }

            // Add bounce animation to the selected item
            val selectedView = binding.bottomNavigation.findViewById<View>(item.itemId)
            selectedView?.animate()?.scaleX(1.2f)?.scaleY(1.2f)?.setDuration(150)?.withEndAction {
                selectedView.animate().scaleX(1f).scaleY(1f).duration = 150
            }?.start()

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        var isMapActive = false
        binding.fab.setOnClickListener {
            if (checkLocationPermission()) {
                isMapActive = !isMapActive

                // FAB Animation (Rotation)
                binding.fab.animate().rotation(if (isMapActive) 45f else 0f).duration = 300

                // Change FAB color based on the state
                val iconColor = if (isMapActive) R.color.red else R.color.black
                binding.fab.imageTintList = ContextCompat.getColorStateList(this, iconColor)
                binding.fab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_200))

                // Clear selection from BottomNavigationView
                binding.bottomNavigation.menu.setGroupCheckable(0, true, false)
                for (i in 0 until binding.bottomNavigation.menu.size()) {
                    binding.bottomNavigation.menu.getItem(i).isChecked = false
                }
                binding.bottomNavigation.menu.setGroupCheckable(0, true, true)

                binding.bottomNavigation.selectedItemId = R.id.fab
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
            R.anim.scale_rotate_in,
            R.anim.slide_fade_out,
            R.anim.fade_in_scale_up,
            R.anim.slide_down
        )
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
