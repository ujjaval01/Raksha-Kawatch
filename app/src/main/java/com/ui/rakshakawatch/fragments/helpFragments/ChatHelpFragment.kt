package com.ui.rakshakawatch.fragments.helpFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.fragments.HomeFragment

class ChatHelpFragment : Fragment() {

    private lateinit var aiChatBot: LinearLayout
    private lateinit var serviceHelp:LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_help, container, false)

        val bottomNav = activity?.findViewById<View>(R.id.bottomNavigation)
        bottomNav?.visibility = View.VISIBLE

        aiChatBot = view.findViewById(R.id.aiChatBot)
        serviceHelp = view.findViewById(R.id.serviceHelp)
        aiChatBot.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AiChatbotFragment())
                .addToBackStack(null)
                .commit()
        }
        serviceHelp.setOnClickListener {
            val url = "https://raksha-kawatch.kesug.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
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
}
