package com.spencerrhein.horserace

import android.animation.AnimatorSet
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.animation.doOnEnd
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ingilizceevi.soundplayer.SoundPlayer
import com.spencerrhein.horserace.databinding.FragmentGamePanelBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GamePanel : Fragment() {

    private lateinit var binding: FragmentGamePanelBinding
    private lateinit var horseRacePositionPost: HorseRacePositionPost
    private lateinit var imageControl: ImageController
    private lateinit var timerView: Chronometer
    private lateinit var clicksView: TextView
    private lateinit var playButton: ImageView
    private val imageViewsPanel = mutableListOf<ImageView>()
    private val gameBrain: LaneViewModel by activityViewModels()
    private lateinit var soundPlayer: SoundPlayer

    private lateinit var goSignal: FrameLayout
    private var gameFinished = false
    private val numOfImages = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGamePanelBinding.inflate(inflater, container, false)
        timerView = binding.myTimer
        goSignal = binding.signalCircle
        clicksView = binding.clicksTextview
        playButton = binding.playButton

        binding.root.setOnClickListener(initiatorClick)
        goSignal.setBackgroundResource(R.drawable.red_circle)
        playButton.setOnClickListener { playTarget() }
        soundPlayer = SoundPlayer(requireContext())
        return binding.root
    }

    private val initiatorClick = View.OnClickListener {
        binding.root.setOnClickListener(null)
        timerView.start()
        horseRacePositionPost = HorseRacePositionPost(gameBrain.student_id)
        startGame()
    }

    private fun startGame() {
        imageControl = childFragmentManager.findFragmentById(R.id.laneControllerView) as ImageController
        imageViewsPanel.clear()
        for (i in 0 until numOfImages) {
            imageViewsPanel.add(imageControl.imageFragmentPanel[i].handleOnImageView())
        }
        imageControl.refreshPanelOfImages()
        gameBrain.myTargetConceptIsSetFromIdealMap()
        setOnImageClickListeners()
        binding.root.postDelayed({ playTarget() }, 100)
    }

    private fun playTarget() {
        if (gameBrain.myTargetConcept != "-1") {
            lifecycleScope.launch {
                val u = gameBrain.getTargetUri()
                withContext(Dispatchers.IO) {
                    val p = u?.let { soundPlayer.startSound(it) }
                    p?.setOnCompletionListener { setOnImageClickListeners() }
                }
            }
        }
    }

    private fun setOnImageClickListeners() {
        imageViewsPanel.forEach { it.setOnClickListener(onImageClickListener) }
        goSignal.setBackgroundResource(R.drawable.green_circle)
    }

    private fun nullifyAllImageViewListeners() {
        imageViewsPanel.forEach { it.setOnClickListener(null) }
        goSignal.setBackgroundResource(R.drawable.red_circle)
    }

    private fun dealWithTheClicks() {
        gameBrain.increaseClickedCounter()
        val totalClicksSoFar = gameBrain.getTotalClicks().toString()
        clicksView.text = totalClicksSoFar
    }

    private val onImageClickListener = View.OnClickListener {
        nullifyAllImageViewListeners()
        dealWithTheClicks()
        validateSelection(it.id)
    }

    private fun validateSelection(viewId: Int) {
        if (gameBrain.isTargetConceptTrue(viewId)) {
            targetConceptIsTrue(viewId)
        } else targetIsFalse(viewId)
    }

    private fun targetConceptIsTrue(viewId: Int) {
        myFunLittleFun()
        gameBrain.schemaForImageClickedTrue(viewId)
        if (gameBrain.checkGameCompletion()) {
            gameFinished = true
        }
        val bigAnim = imageControl.imageFragmentPanel[viewId].enlargerAnimatorIsSetForView()
        bigAnim.doOnEnd { allViewsAreFadedOut(gameFinished) }
        bigAnim.start()
    }

    private fun targetIsFalse(viewId: Int) {
        gameBrain.schemaForImageClickedFalse()
        val shakeAnim = imageControl.imageFragmentPanel[viewId].shakerAnimatorIsSetForView()
        shakeAnim.doOnEnd { allViewsAreFadedOut(false) }
        shakeAnim.start()
    }

    private fun setupFadeOutAnimator(finished: Boolean): AnimatorSet {
        val fadeOutControl = AnimatorController(imageControl)
        val fadeOutSet = fadeOutControl.setupFadeOutAnimator()
        fadeOutSet.doOnEnd {
            if (!finished) startAnotherRoundOfFun()
            else {
                stopGamePanel()
                gameBrain.concludedGameLiveData.value = true
            }
        }
        return fadeOutSet
    }

    private fun allViewsAreFadedOut(finished: Boolean) {
        setupFadeOutAnimator(finished).start()
    }

    fun stopGamePanel(): Boolean {
        timerView.stop()
        val totalTime = timerView.text.toString()
        val totalSeconds = gameBrain.calculateTotalSeconds(totalTime)
        if (totalSeconds == 0) return false
        gameBrain.chapterInfo.chapterTime = totalSeconds.toString()
        return totalSeconds != 0
    }

    private fun startAnotherRoundOfFun() {
        imageControl.refreshPanelOfImages()
        playTarget()
    }

    fun myFunLittleFun(){
        horseRacePositionPost.postHorsePosition()
    }
}
