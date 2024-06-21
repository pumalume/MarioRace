package com.spencerrhein.horserace

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import androidx.core.view.isNotEmpty
import androidx.core.view.setMargins
import androidx.core.view.size


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegistrationPanel : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var main : View
    private lateinit var scroller : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        main = inflater.inflate(R.layout.scroll_view, container, false)
        scroller = main.findViewById(R.id.scroll_view)
        return main
    }

    fun getScrollSize():Int{return scroller.size}

    fun handleOnScrollElement(index:Int):TextView{
        val textview = scroller.getChildAt(index) as TextView
        return textview
    }


    fun addElementToView(displayString:String, stringID:String) {
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(15)
        val tempText = TextView(context)
        tempText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tempText.layoutParams = lp
        tempText.setPadding(20, 10, 20, 10) // Adjust padding as needed
        tempText.text = displayString
        tempText.tag = stringID
        tempText.setTextColor(Color.BLACK)
        tempText.textSize = 30f

        tempText.setBackgroundResource(R.drawable.text_box_with_border) // Set thin black border
        scroller.addView(tempText)
    }

    fun emptyTheScroll(){
        if(scroller.isNotEmpty()) {
            scroller.removeAllViews()
        }
    }

    fun fillTheScrollFromMap(map:MutableMap<String,String>){
        for (key in map.keys){
            addElementToView(map[key]!!, key)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationPanel().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}