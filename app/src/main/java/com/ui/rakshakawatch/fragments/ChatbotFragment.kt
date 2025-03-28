package com.ui.rakshakawatch.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ui.rakshakawatch.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ChatbotFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        chatLayout = view.findViewById(R.id.chatLayout)
        userInput = view.findViewById(R.id.messageEditText)
        sendButton = view.findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val message = userInput.text.toString().trim()
            if (message.isNotEmpty()) {
                updateChat(message, true)
                userInput.text.clear()
                sendMessageToChatbot(message)
            }
        }
        return view
    }

    private fun sendMessageToChatbot(message: String) {
        val requestBody = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                    {"role": "user", "content": "$message"}
                ]
            }
        """.trimIndent()

        val mediaType = "application/json".toMediaType()
        val body = requestBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    updateChat("Error: ${e.message}", false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        activity?.runOnUiThread {
                            updateChat("Error: ${it.message}", false)
                        }
                        return
                    }

                    val responseData = it.body?.string()
                    try {
                        val responseObject = JSONObject(responseData!!)
                        if (responseObject.has("choices")) {
                            val choicesArray = responseObject.getJSONArray("choices")
                            if (choicesArray.length() > 0) {
                                val firstChoice = choicesArray.getJSONObject(0)
                                val message = firstChoice.getJSONObject("message").getString("content")
                                activity?.runOnUiThread {
                                    updateChat(message, false)
                                }
                            } else {
                                activity?.runOnUiThread {
                                    updateChat("Error: No response from the AI.", false)
                                }
                            }
                        } else {
                            activity?.runOnUiThread {
                                updateChat("Error: Unexpected response format.", false)
                            }
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            updateChat("Error: ${e.message}", false)
                        }
                    }
                }
            }
        })
    }

    private fun updateChat(message: String, isUser: Boolean) {
        val messageView = TextView(requireContext())
        messageView.text = message
        messageView.textSize = 16f
        messageView.setPadding(16, 16, 16, 16)

        if (isUser) {
            messageView.setBackgroundResource(R.drawable.user_message_bg)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 16
            params.topMargin = 8
            params.gravity = Gravity.END
            messageView.layoutParams = params
        } else {
            messageView.setBackgroundResource(R.drawable.bg_ai_message)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginStart = 16
            params.topMargin = 8
            params.gravity = Gravity.START
            messageView.layoutParams = params
        }

        chatLayout.addView(messageView)
    }
}
