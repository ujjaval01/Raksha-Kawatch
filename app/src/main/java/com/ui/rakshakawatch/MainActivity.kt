package com.ui.rakshakawatch

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.ui.rakshakawatch.databinding.ActivityMainBinding
import com.ui.rakshakawatch.fragments.AboutFragment
import com.ui.rakshakawatch.fragments.CreateComplainFragment
import com.ui.rakshakawatch.fragments.DashboardFragment
import com.ui.rakshakawatch.fragments.EmergencyContactsFragment
import com.ui.rakshakawatch.fragments.HomeFragment
import com.ui.rakshakawatch.fragments.LogoutFragment
import com.ui.rakshakawatch.fragments.ManageGuardianFragment
import com.ui.rakshakawatch.fragments.ProfileFragment
import com.ui.rakshakawatch.fragments.SettingPrivacyFragment
import com.ui.rakshakawatch.fragments.ShareFragment
import com.ui.rakshakawatch.fragments.ToolsFragment
import com.ui.rakshakawatch.fragments.ViewComplainFragment


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var fragmentManager:FragmentManager
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val toogle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.nav_open,R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_home -> openFragment(HomeFragment())
                R.id.bottom_emergency_contacts -> openFragment(EmergencyContactsFragment())
                R.id.bottom_tools -> openFragment(ToolsFragment())
                R.id.bottom_setting -> openFragment(SettingPrivacyFragment())
//                R.id.bottom_ -> openFragment(HomeFragment())
            }
            true
        }
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())
        binding.fab.setOnClickListener {
            Toast.makeText(this, "Map Fragment", Toast.LENGTH_SHORT).show()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
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
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()  // This works on all Android versions
        }
    }

    public fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }


}