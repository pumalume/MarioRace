package com.spencerrhein.horserace

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ConceptLoader(location: String) {
    private val location = "L$location"
    val theSounds: MutableMap<String, Uri> = mutableMapOf()
    val theImages: MutableMap<String, Drawable> = mutableMapOf()

    suspend fun loadAudioAsync(): MutableMap<String, Uri> = withContext(Dispatchers.IO) {
        val myPath = Environment.getExternalStorageDirectory().path + "/Music/" + location + "/"
        File(myPath).walkBottomUp().forEach {
            if (it.isFile) {
                val u = Uri.parse(it.toString())
                val s = it.toString().substringAfterLast("/").dropLast(4)
                theSounds[s] = u
            }
        }
        theSounds
    }

    suspend fun loadDrawablesAsync(): MutableMap<String, Drawable> = withContext(Dispatchers.IO) {
        val myPath = Environment.getExternalStorageDirectory().path + "/Pictures/" + location + "/"
        File(myPath).walkBottomUp().forEach {
            if (it.isFile) {
                val d = Drawable.createFromPath(it.absolutePath)!!
                val s = it.toString().substringAfterLast("/").dropLast(4)
                theImages[s] = d
            }
        }
        theImages
    }
}
