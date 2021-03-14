package com.boryans.beenote.application

import android.app.Application
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.api.WeatherApi
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@HiltAndroidApp
class Application: Application() {

}