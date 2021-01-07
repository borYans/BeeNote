package com.example.beenote.interfaces

import com.example.beenote.model.WeatherDataModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/weather?lat={lat}&lon={lon}&appid={API key}")
    suspend fun getCurrentWeather(@Query("lat") lat:String, @Query("lon") lon:String, @Query("APPID") appId:String): Call<WeatherDataModel>

}