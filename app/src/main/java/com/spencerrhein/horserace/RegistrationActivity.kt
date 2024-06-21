package com.spencerrhein.horserace

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class RegistrationActivity : AppCompatActivity() {
    private val registrationModel: RegistrationModel by viewModels()
    private lateinit var redoButton: ImageView
    private lateinit var exitButton: ImageView
    private lateinit var panelController: RegistrationPanel
    private lateinit var textBox : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        textBox = findViewById(R.id.my_text_view)

        redoButton = findViewById(R.id.redobutton)
        exitButton = findViewById(R.id.mainExit)
        panelController = supportFragmentManager
            .findFragmentById(R.id.registrationPanelContainer) as RegistrationPanel
        exitButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }

        redoButton.setOnClickListener {groupsAreLoadedIntoScroll()}
        observerIsEstablishedForSelectedGroup()
        observerIsEstablishedToLoadGroupList()
        observerIsEstablishedForSelectedStudent()
        observerIsEstablishedToLoadStudentsList()
        observerIsEstablishedForSelectedChapter()

    }

    override fun onResume() {
        super.onResume()
        groupListRequestMadeToServer()

    }
    fun registrationActivityIsFinished() {
        if (registrationModel.checkIfAllFieldsRegistered()) {
            val intent = Intent().apply {
                putExtra("student_id", registrationModel.student_id)
                putExtra("student_name", registrationModel.student_name)
                putExtra("group_id", registrationModel.group_id)
                putExtra("group_name", registrationModel.group_name)
                putExtra("chapter_id", registrationModel.chapter_id)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    fun observerIsEstablishedToLoadGroupList(){
        val groupIsReadyToLoadAction = Observer<Boolean> {
            if(it==true){ groupsAreLoadedIntoScroll() }
        }
        registrationModel.groupsAreReadyToLoadLiveData.observe(this, groupIsReadyToLoadAction)
    }
    fun observerIsEstablishedForSelectedGroup(){
        val groupIsSelectedAction = Observer<String> {
            textBox.text = "Group: " + it
            studentsListRequestMadeToServer()
        }
        registrationModel.groupIdIsSelectedLiveData.observe(this, groupIsSelectedAction)
    }
    fun observerIsEstablishedToLoadStudentsList(){
        val studentsIsReadyToLoadAction = Observer<Boolean> {
            studentsAreLoadedIntoScroll()
        }
        registrationModel.studentsIsReadyToLoadLiveData.observe(this, studentsIsReadyToLoadAction)
    }

    fun observerIsEstablishedForSelectedStudent(){
        val studentsIsSelectedAction = Observer<String> {
            val temp = textBox.text.toString()
            textBox.text = temp + " Student: "+it
            chaptersAreLoadedIntoScroll()
        }
        registrationModel.studentIdIsSelectedLiveData.observe(this, studentsIsSelectedAction)
    }
    fun observerIsEstablishedForSelectedChapter(){
        val chapterIsSelectedAction = Observer<String> {
            registrationActivityIsFinished()
        }
        registrationModel.chapterIdIsSelectedLiveData.observe(this, chapterIsSelectedAction)
    }

    fun groupsAreLoadedIntoScroll(){
        panelController.emptyTheScroll()
        panelController.fillTheScrollFromMap(registrationModel.groupMap)
        val size = panelController.getScrollSize()
        for(i in 0 until size) {
            val element = panelController.handleOnScrollElement(i)
            element.setOnClickListener {
                val textbox = it as TextView
                val id = textbox.tag.toString()
                val name = textbox.text.toString()
                registrationModel.group_id = id
                registrationModel.group_name = name
                registrationModel.groupIdIsSelectedLiveData.value = id
            }
        }
    }
    fun studentsAreLoadedIntoScroll(){
        panelController.emptyTheScroll()
        panelController.fillTheScrollFromMap(registrationModel.studentMap)
        val size = panelController.getScrollSize()
        for(i in 0 until size) {
            val element = panelController.handleOnScrollElement(i)
            element.setOnClickListener {
                val textbox = it as TextView
                val id = textbox.tag.toString()
                val name = textbox.text.toString()
                registrationModel.student_id = id
                registrationModel.student_name = name
                registrationModel.studentIdIsSelectedLiveData.value = id
            }
        }
    }

    fun chaptersAreLoadedIntoScroll(){
        val chapterMap = registrationModel.getMapOfChapters()
        panelController.emptyTheScroll()
        panelController.fillTheScrollFromMap(chapterMap)
        val size = panelController.getScrollSize()
        for(i in 0 until size) {
            val element = panelController.handleOnScrollElement(i)
            element.setOnClickListener {
                val id = it.tag.toString()
                registrationModel.chapter_id = id
                registrationModel.chapterIdIsSelectedLiveData.value = id
            }
        }
    }
    fun studentsListRequestMadeToServer(){
        val dataQuest = InformationGetRequest(registrationModel)
        dataQuest.fetchDataAndPostToModel("student")
    }
    fun groupListRequestMadeToServer(){
        val dataQuest = InformationGetRequest(registrationModel)
        dataQuest.fetchDataAndPostToModel("group")
    }
}