package com.abhiiscoding.mysticmirror

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object GeminiApiService {
    private const val API_KEY = "AIzaSyCLxpsK4MQleu72HVsu3I_nCLMtseewbvU"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    private val client = OkHttpClient()
    private val gson = Gson()

    fun generateResponse(prompt: String, callback: (String?) -> Unit) {
        val json = """
            {
                "contents": [
                    {
                        "parts": [
                            { "text": "$prompt" }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL?key=$API_KEY")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let { responseBody ->
                    val responseString = responseBody.string()
                    val responseJson = gson.fromJson(responseString, GeminiResponse::class.java)
                    val textResponse = responseJson.candidates?.get(0)?.content?.parts?.get(0)?.text
                    callback(textResponse)
                } ?: callback(null)
            }
        })
    }
}

// Response Data Model
data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?
)

data class Content(
    val parts: List<Part>?
)

data class Part(
    val text: String?
)
