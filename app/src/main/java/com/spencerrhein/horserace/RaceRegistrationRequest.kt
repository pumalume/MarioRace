package com.spencerrhein.horserace

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

class RaceRegistrationRequest(val student:String, val group:String) {
    val endpointUrl = "https://spencerrhein.com/wp-json/wp/v2/horserace"

    fun makePostRequest() {
        CoroutineScope(Dispatchers.Main).launch {
            val reply = postData()
        }
    }
    suspend fun postData(): String? {
        return withContext(Dispatchers.IO) {
            val client = getUnsafeOkHttpClient()
            val json = """
                {
                    "horse_number":"$student",
                    "group_id":"$group"
                }
            """.trimIndent()
            Log.d("ServerPostRequest", "URL: $endpointUrl")
            Log.d("ServerPostRequest", "JSON data to be sent: $json") // Log the entire JSON string
            val requestBody =
                RequestBody.create(MediaType.get("application/json; charset=utf-8"), json)
            val request = Request.Builder()
                .url(endpointUrl)
                .addHeader("Authorization", Credentials.basic("spencer", "Maylee08!"))
                .post(requestBody)
                .build()

            try {
                val response: Response = client!!.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("ServerPostRequest", "Response successful")
                    val responseBody = response.body()?.string() // Read response body
                    Log.d("ServerPostRequest", "Response body: $responseBody") // Log response content
                    null
                } else {
                    Log.e("ServerPostRequest", "Response not successful: ${response.code()}")
                    null
                }
            } catch (e: IOException) {
                Log.e("ServerPostRequest", "Network error: ${e.message}")
                null
            }
        }
    }
}