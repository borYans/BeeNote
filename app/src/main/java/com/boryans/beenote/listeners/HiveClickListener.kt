package com.boryans.beenote.listeners

interface HiveClickListener {
    fun onHiveLongClicked(position: String)
    fun onHiveClicked(position:String)
    fun onAddSignClicked(position: String)
}