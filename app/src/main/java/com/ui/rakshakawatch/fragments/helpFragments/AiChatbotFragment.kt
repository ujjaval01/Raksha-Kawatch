package com.ui.rakshakawatch.fragments.helpFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ui.rakshakawatch.Adapter.ChatAdapter
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.dataModel.Message
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AiChatbotFragment : Fragment() {

    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var backArrow: ImageView

    // Replace this with your actual OpenRouter API key
    private val API_KEY = "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"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ai_chatbot, container, false)
        val bottomNav = activity?.findViewById<View>(R.id.bottomNavigation)
        bottomNav?.visibility = View.GONE

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        editText = view.findViewById(R.id.messageEditText)
        sendButton = view.findViewById(R.id.sendButton)
        backArrow = view.findViewById(R.id.backArrow)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val input = editText.text.toString().trim()
            if (input.isNotEmpty()) {
                addMessage(input, true)
                editText.setText("")
                getAIResponse(input)
            }
        }
        backArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    return view
}

    private fun addMessage(text: String, isUser: Boolean) {
        messages.add(Message(text, isUser))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun getAIResponse(prompt: String) {
        val client = OkHttpClient()

        val json = """
            {
              "model": "openai/gpt-3.5-turbo",
              "messages": [{"role": "user", "content": "$prompt"}]
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    addMessage("Error: ${e.message}", false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val body = it.string()
                    val jsonObject = JSONObject(body)
                    val reply = jsonObject
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    requireActivity().runOnUiThread {
                        addMessage(reply.trim(), false)
                    }
                }
            }
        })
    }
}
