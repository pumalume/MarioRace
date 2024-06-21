package com.spencerrhein.horserace

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import java.io.IOException


class HorseRacePositionPost(
    private val horseNumber:Int
) {
    fun postHorsePosition() {
        CoroutineScope(Dispatchers.Main).launch {
            val result = postData()
            if (result != null) {
                Log.d("MainActivity", "Response: $result")
            } else {
                Log.e("MainActivity", "Failed to post data")
            }
        }
    }


    suspend fun postData(): String? {
        val endpointUrl = "https://spencerrhein.com/wp-json/horse-race/v1/update-position"
        return withContext(Dispatchers.IO) {
            val client = getUnsafeOkHttpClient()
            val json = """{"horse_number":$horseNumber}""".trimIndent()
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
                    Log.d("RaceHorse", "Response spencer successful")
                    val responseData = response.body()?.string()  // Get the response data as a string
                    responseData
                } else {
                    Log.e("RaceHorsePost", "Response code: failure")
                    null
                }
            } catch (e: IOException) {
                Log.e("RaceHorse", "Network  spencer error: ${e.message}")
                null
            }
        }
    }
}