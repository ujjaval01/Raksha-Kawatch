package com.ui.rakshakawatch.fragments

import SettingFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ui.rakshakawatch.R

class AboutFragment : Fragment() {
    private lateinit var backArrow: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)

//        hide bottom navBar
        val bottomNav = activity?.findViewById<View>(R.id.bottomNavigation)
        bottomNav?.visibility = View.GONE
        backArrow = view.findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
