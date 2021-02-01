package com.boryans.beenote.application

import com.boryans.beenote.constants.Constants
import com.boryans.beenote.interfaces.WeatherApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Application: android.app.Application() {

    val api: WeatherApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)


}