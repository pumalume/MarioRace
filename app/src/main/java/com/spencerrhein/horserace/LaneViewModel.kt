package com.spencerrhein.horserace

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class LaneViewModel : ViewModel() {
    var numOfLanes: Int = 3
    val chapterInfo = GameRecord()
    var student_id: Int = 0
    var gameBrainIsInitialized = false
    lateinit var masterCollectionDrawables: MutableMap<String, Drawable>
    lateinit var masterCollectionSounds: MutableMap<String, Uri>
    val truthArray: MutableMap<String, MutableList<Boolean>> = mutableMapOf()
    var myTargetConcept: String = "-1"
    private val availableConcepts: MutableList<String> = ArrayList(0)
    private val pileOfDiscardedConcepts: MutableList<String> = ArrayList(0)
    private val idealConceptualMap: MutableMap<Int, String> = mutableMapOf()

    fun initiateModel(chapter: String, student: Int) {
        student_id = student
        val conceptLoader = ConceptLoader(chapter)
        loadResources(conceptLoader)
    }

    private fun loadResources(conceptLoader: ConceptLoader) {
        CoroutineScope(Dispatchers.IO).launch {
            masterCollectionDrawables = conceptLoader.loadDrawablesAsync()
            masterCollectionSounds = conceptLoader.loadAudioAsync()
            withContext(Dispatchers.Main) {
                intializeTheTruthArray()
                availableConceptsAreGenerated()
                conceptualMapsAreInitialized()
                schemaIsInitializeToBeginGame()
                gameBrainIsInitialized = true
            }
        }
    }

    fun intializeTheTruthArray(){
        for ((key, _) in masterCollectionDrawables) {
            truthArray.put(key, mutableListOf())
        }
    }
    fun getTargetUri(): Uri? {
        return masterCollectionSounds[myTargetConcept]
    }

    private fun availableConceptsAreGenerated() {
        masterCollectionDrawables.keys.let {
            availableConcepts.addAll(it)
        }
        // Shuffle only if necessary
        if (availableConcepts.size > 1) {
            availableConcepts.shuffle()
        }
    }

    private fun conceptualMapsAreInitialized() {
        for (i in 0 until numOfLanes) {
            idealConceptualMap[i] = "-1"
        }
    }

    private fun conceptIsPulledFromAvailableConcepts(): String {
        return if (availableConcepts.isNotEmpty()) availableConcepts.removeAt(0) else "-1"
    }

    private fun conceptIsRegisteredToIdealMapFromAvailableList(laneTag: Int): Boolean {
        idealConceptualMap[laneTag] = conceptIsPulledFromAvailableConcepts()
        return idealConceptualMap[laneTag] != "-1"
    }

    private fun conceptIsThrownToDiscardPile(laneTag: Int) {
        idealConceptualMap[laneTag]?.let {
            pileOfDiscardedConcepts.add(it)
            idealConceptualMap[laneTag] = "-1"
        }
    }

    fun schemaIsInitializeToBeginGame(): Boolean {
        for (i in 0 until numOfLanes) {
            if (!conceptIsRegisteredToIdealMapFromAvailableList(i)) return false
        }
        return true
    }

    fun idealSchemaIsRecycled() {
        val tempList: MutableList<String> = ArrayList()
        for (i in 0 until numOfLanes) tempList.add(idealConceptualMap[i]!!)
        tempList.shuffle()
        for (i in 0 until numOfLanes) idealConceptualMap[i] = tempList[i]
    }

    private fun conceptIsReturnedToAvailableList(viewId: Int) {
        idealConceptualMap[viewId]?.takeIf { it != "-1" }?.let {
            availableConcepts.add(it)
            availableConcepts.shuffle()
        }
    }

    fun allImagesAreFinished(): Boolean {
        for (i in 0 until numOfLanes) {
            if (idealConceptualMap[i] != "-1") return false
        }
        return true
    }

    fun schemaForImageClickedTrue(viewId: Int) {
        updateSchemaAfterClick(viewId, true)
    }

    fun schemaForImageClickedFalse() {
        chapterInfo.loadConceptIntoResultsArray(myTargetConcept)
        updateSchemaAfterClick()
    }

    private fun updateSchemaAfterClick(viewId: Int? = null, isCorrect: Boolean = false) {
        for (index in 0 until numOfLanes) {
            if (index == viewId) {
                conceptIsThrownToDiscardPile(index)
                conceptIsRegisteredToIdealMapFromAvailableList(index)
            } else {
                if (!isCorrect) {
                    conceptIsReturnedToAvailableList(index)
                    conceptIsRegisteredToIdealMapFromAvailableList(index)
                }
            }
        }
        if (!isCorrect) {
            idealSchemaIsRecycled()
        }
    }

    fun checkGameCompletion(): Boolean {
        return if (allImagesAreFinished()) true else {
            myTargetConceptIsSetFromIdealMap()
            false
        }
    }

    fun getIdealLaneValue(laneTag: Int): String? {
        return idealConceptualMap[laneTag]
    }

    fun logTheTruthValue(truthValue:Boolean){
        val key = myTargetConcept
        truthArray[key]?.add(truthValue)
    }
    fun isTargetConceptTrue(laneTag: Int): Boolean {
        val truthValue = idealConceptualMap[laneTag] == myTargetConcept
        logTheTruthValue(truthValue)
        return truthValue
    }

    fun getRandomConceptFromDiscardedPile(): String {
        val size = pileOfDiscardedConcepts.size
        val x = (0 until size).random()
        return pileOfDiscardedConcepts[x]
    }

    fun myTargetConceptIsSetFromIdealMap() {
        do {
            myTargetConcept = idealConceptualMap[(0 until numOfLanes).random()]!!
        } while (myTargetConcept == "-1")
    }

    fun getTotalClicks(): Int {
        return chapterInfo.chapterClicks
    }

    fun increaseClickedCounter() {
        chapterInfo.chapterClicks++
    }

    fun calculateTotalSeconds(totalTime: String): Int {
        val parts = totalTime.split(":")
        val minutes = parts[0].toInt()
        var seconds = parts[1].toInt()
        seconds += minutes * 60
        return seconds
    }


    val startGameLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val cancelGameLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val concludedGameLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    class GameRecord {
        var chapterTime: String = ""
        var chapterClicks: Int = 0
        val resultsArray: MutableList<String> = ArrayList()
        val truthArray: MutableMap<String, MutableList<Boolean>> = mutableMapOf()
        fun loadConceptIntoResultsArray(answer: String) {
            resultsArray.add(answer)
        }

        fun getFinalResultsArray(): MutableList<String> {
            resultsArray.add(chapterTime)
            resultsArray.add(chapterClicks.toString())
            return resultsArray
        }
    }
}

