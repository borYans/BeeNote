package com.example.beenote.application

import com.example.beenote.constants.Constants
import com.example.beenote.interfaces.WeatherApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Application: android.app.Application() {

    val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)


}