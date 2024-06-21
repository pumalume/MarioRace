package com.spencerrhein.horserace
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File


class RegistrationModel: ViewModel() {
    var student_name: String = "Mario Bros"
    var student_id: String = ""
    var chapter_id: String = ""
    var group_id:String = ""
    var group_name:String= ""
    var groupMap:MutableMap<String,String> = mutableMapOf()
    var studentMap:MutableMap<String,String> = mutableMapOf()

    fun checkIfAllFieldsRegistered():Boolean{
        return (group_id!="" && student_id!="" &&chapter_id!="")
    }

    fun getMapOfChapters():MutableMap<String,String>{
        val chapterMap = mutableMapOf<String,String>()
        val chapterArray = getListOfChaptersFromDirectory()
        if(chapterArray.isNotEmpty()){
            chapterArray.forEach { el ->
                val key = el.filter { it.isDigit() }
                chapterMap[key] = el
            }
        }
        return chapterMap
    }
    private fun getListOfChaptersFromDirectory(): Array<String> {
        val picturesDir = File(Environment.getExternalStorageDirectory().path + "/Pictures/")
        return if (picturesDir.exists() && picturesDir.isDirectory) {
            val subfolders = picturesDir.listFiles { file -> file.isDirectory }
            subfolders?.map { file -> file.absolutePath.substringAfterLast('/')  }?.toTypedArray() ?: emptyArray()
        } else {
            emptyArray()
        }
    }

    val groupsAreReadyToLoadLiveData:MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val groupIdIsSelectedLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val studentIdIsSelectedLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val studentsIsReadyToLoadLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val chapterIdIsSelectedLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val quitSignal: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val startSignal: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val changeNameSignal: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }


}