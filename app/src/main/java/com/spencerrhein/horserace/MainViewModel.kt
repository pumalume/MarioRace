package com.spencerrhein.horserace

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel(){
    var student_id:String=""
    var student_name : String=""
    var group_name : String = ""
    var group_id: String = ""
    var chapter_id: String = ""

    fun registerData(sId:String,sName:String,gId:String,gName:String,cId:String){
        student_name = sName
        student_id = sId
        group_name = gName
        group_id = gId
        chapter_id = cId
    }

}

