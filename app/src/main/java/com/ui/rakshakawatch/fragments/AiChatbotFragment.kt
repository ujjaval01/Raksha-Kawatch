package com.ui.rakshakawatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.ui.rakshakawatch.R
import okhttp3.OkHttpClient

class AiChatbotFragment : Fragment() {

    private val client = OkHttpClient()
    public val apiKey = "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"  // Replace with your valid OpenAI API key
//    val apiKey = BuildConfig.API_KEY

    private lateinit var chatLayout: LinearLayout
    private lateinit var userInput: EditText
    private lateinit var sendButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ai_chatbot, container, false)


        return view
    }

}
