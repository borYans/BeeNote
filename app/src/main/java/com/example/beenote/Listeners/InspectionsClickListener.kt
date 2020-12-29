package com.example.beenote.Listeners

import android.view.View

interface InspectionsClickListener {
    fun onInspectionClick(id: String, position:Int, v:View)
    fun onInspectionLongCLick(position: String)
}