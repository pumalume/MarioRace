package com.spencerrhein.horserace

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.spencerrhein.horserace.databinding.ActivityPictureBinding

class PictureActivity : AppCompatActivity() {
    private val gameBrain: LaneViewModel by viewModels()
    private var chapter: String? = "L01"
    private var student_id: String? = "01"
    private lateinit var binding: ActivityPictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chapter = intent.getStringExtra("chapter_id") ?: "L01"
        student_id = intent.getStringExtra("student_id") ?: "L01"

        if (!gameBrain.gameBrainIsInitialized) gameBrain.initiateModel(chapter!!, student_id!!.toInt())

        //initializeTheGame()
        setupObservers()
    }
    override fun onResume() {
        super.onResume()
        setupCancelButton()

    }

    private fun setupObservers() {
        gameBrain.concludedGameLiveData.observe(this, Observer {
            closeDownPictureActivity(true)
        })
        gameBrain.cancelGameLiveData.observe(this, Observer {
            closeDownPictureActivity(false)
        })
    }

    private fun setupCancelButton() {
        binding.cancelButton.bringToFront()
        binding.cancelButton.setOnClickListener {
            showDialogFragment("CancelGameDialog")
        }
    }

    private fun showDialogFragment(tag: String) {
        val dialog = CancelGameDialogFragment()
        dialog.show(supportFragmentManager, tag)
    }


    private fun stopGameTimer(){
        val panel = supportFragmentManager.findFragmentById(R.id.gamePanelView) as? GamePanel
        panel?.stopGamePanel()
    }

    private fun closeDownPictureActivity(finished:Boolean) {
        stopGameTimer()
        val chapterTime = gameBrain.chapterInfo.chapterTime
        val chapterClicks = gameBrain.chapterInfo.chapterClicks.toString()
        val intent = Intent().apply {
            putExtra("chapterTime", chapterTime)
            putExtra("chapterClicks", chapterClicks)
        }
        if(finished) setResult(Activity.RESULT_OK, intent)
        else setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }
}
