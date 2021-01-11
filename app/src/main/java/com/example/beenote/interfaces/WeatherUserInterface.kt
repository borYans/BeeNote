package com.example.beenote.interfaces

import es.dmoral.toasty.Toasty

interface WeatherUserInterface {
        suspend fun updateUI(temp: String, humid: String, wind: Float, clouds:String)
        fun displayWarningMessage(toastMessage: Toasty)

}