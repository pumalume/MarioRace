package com.spencerrhein.horserace

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

class CancelGameDialogFragment : DialogFragment() {

    private val gameBrain: LaneViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle("Cancel Game")
            .setMessage("Are you sure you want to cancel the game?")
            .setPositiveButton("Yes") { _, _ ->
                gameBrain.cancelGameLiveData.value = true
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}
