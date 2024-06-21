package com.spencerrhein.horserace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.security.ProviderInstaller

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var startButton: Button
    private lateinit var changeButton:Button
    private lateinit var studentName: TextView
    private lateinit var chapterText: TextView
    private lateinit var groupName: TextView
    private lateinit var registrationActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var pictureActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ProviderInstaller.installIfNeeded(this)

        // Initialize UI components
        startButton = findViewById(R.id.startRace)
        studentName = findViewById(R.id.studentIdTextView)
        groupName = findViewById(R.id.groupIdTextView)
        chapterText = findViewById(R.id.chapterIdTextView)
        changeButton = findViewById(R.id.changeUserButton)
        changeButton.setOnClickListener{registerUser()}

        startButton.setOnClickListener {
            val chapter_id = mainViewModel.chapter_id
            val student_id = mainViewModel.student_id
            val intent = Intent(this, PictureActivity::class.java)
            intent.putExtra("chapter_id", chapter_id)
            intent.putExtra("student_id", student_id)
            pictureActivityResultLauncher.launch(intent)
        }
        pictureActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { _ ->
            // Empty block - no action needed on PictureActivity finish
        }

        registrationActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val extras = data.extras
                    if (extras != null) {
                        mainViewModel.student_id = extras.getString("student_id")!!
                        mainViewModel.student_name = extras.getString("student_name")!!
                        mainViewModel.group_id = extras.getString("group_id")!!
                        mainViewModel.group_name = extras.getString("group_name")!!
                        mainViewModel.chapter_id = extras.getString("chapter_id")!!
                        RaceRegistrationRequest(mainViewModel.student_id,mainViewModel.group_id).makePostRequest()
                        updateUI()
                    }
                }
            }
        }
        registerUser()
    }

    fun registerUser(){
        val intent = Intent(this, RegistrationActivity::class.java)
        registrationActivityResultLauncher.launch(intent)
    }

    private fun updateUI() {
        studentName.text = "Name: ${mainViewModel.student_name}"
        groupName.text = "Group: ${mainViewModel.group_name}"
        chapterText.text = "Chapter: ${mainViewModel.chapter_id}"
    }
}
