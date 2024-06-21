package com.spencerrhein.horserace

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException


public class InformationGetRequest (val registrationModel:RegistrationModel){
    val studentsUrl = "https://spencerrhein.com/index.php/wp-json/custom/v1/student_list"
    val groupsUrl = "https://spencerrhein.com/index.php/wp-json/custom/v1/group_list"

    fun fetchDataAndPostToModel(dataType:String) {
        var url = ""
        if(dataType=="student") url = studentsUrl
        else url = groupsUrl
        CoroutineScope(Dispatchers.Main).launch {
            val result = getData(url)
            if (result != null) {
                if(dataType=="student") {
                    registrationModel.studentMap = parseJson(result, dataType)
                    registrationModel.studentsIsReadyToLoadLiveData.value=true
                }
                if (dataType=="group") {
                    registrationModel.groupMap = parseJson(result,dataType)
                    registrationModel.groupsAreReadyToLoadLiveData.value = true
                }
            } else {
                Log.e("MainActivity", "Failed to get data")
            }
        }
    }

    private fun parseJson(response: String, dataType: String): MutableMap<String, String> {
        val dataMap = mutableMapOf<String, String>()

        try {
            val jsonArray = JSONArray(response)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString(when (dataType) {
                    "student" -> "student_id"
                    "group" -> "group_id"
                    else -> throw IllegalArgumentException("Unsupported data type: $dataType")
                })

                val name = when (dataType) {
                    "student" -> {
                        val firstName = jsonObject.getString("firstName")
                        val lastName = jsonObject.getString("lastName")
                        "$firstName $lastName"
                    }
                    "group" -> jsonObject.getString("group_name")
                    else -> throw IllegalArgumentException("Unsupported data type: $dataType")
                }

                dataMap[id] = name
            }
        } catch (e: Exception) {
            Log.e("HorseRaceGetData", "Error parsing JSON: ${e.message}")
        }

        return dataMap
    }


    suspend fun getData(endpointUrlGet:String): String? {
        return withContext(Dispatchers.IO) {
            val client = getUnsafeOkHttpClient()
            val request = Request.Builder()
                .url(endpointUrlGet)
                .addHeader("Authorization", Credentials.basic("gulbeyaz.rhein", "Maylee08!"))
                .get()
                .build()
            try {
                val response: Response = client!!.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("Okhttp", "David was successful")
                    response.body()?.string()
                } else {
                    Log.d("Okhttp", "David was not successful")
                    null
                }
            } catch (e: IOException) {
                Log.e("OkHttp", "David Error making OkHttp request:", e)
                null
            }
        }
    }
}
