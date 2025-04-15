package com.ui.rakshakawatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ui.rakshakawatch.R

@Suppress("UNREACHABLE_CODE")
class ChatHelpFragment : Fragment() {

    private lateinit var aiChatBot: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_help, container, false)

        return view
    }



}