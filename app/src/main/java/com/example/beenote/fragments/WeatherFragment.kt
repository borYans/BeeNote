package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.beenote.R
import com.example.beenote.constants.Constants
import com.example.beenote.interfaces.WeatherApi
import com.example.beenote.model.ConditionData
import com.example.beenote.model.WeatherDataModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt


class WeatherFragment() : Fragment() {

    //firebase
    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()

    //location
    private var locationListener: ListenerRegistration? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        inspectionRatingInfo.setOnClickListener {
            //navigate to inspectionRatingFragment
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            //refresh UI
        }

    }

    private fun fetchWeather(lat: String, lon: String) {

        val api = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)

        val call = api.getCurrentWeather(lat, lon, Constants.APP_ID)
        call.enqueue(object : Callback<WeatherDataModel> {
            override fun onResponse(
                call: Call<WeatherDataModel>?,
                response: Response<WeatherDataModel>?
            ) {
                val weatherResponse = response?.body()
                weatherProgressBar.visibility = View.GONE
                val conditions = ConditionData(
                    weatherResponse?.main?.temp?.minus(273.1),
                    weatherResponse?.main?.humidity,
                    weatherResponse?.wind?.speed,
                    weatherResponse?.clouds?.all
                )
                inspectionRatingInfo(conditions)
                currentTemperature.visibility = View.VISIBLE
                currentTemperature.text = conditions.temp?.roundToInt().toString() + "°"
                humidity.text = conditions.humid?.toString() + "%"
                windSpeedTxt.text = conditions.wind?.roundToInt().toString() + "m/s"
                cloudCoverTxt.text = conditions.clouds?.toString() + "%"

                nameOfTheCity.text = weatherResponse?.name
                weatherDescriptionTxt.text = weatherResponse?.weather?.get(0)?.description
            }

            override fun onFailure(call: Call<WeatherDataModel>?, t: Throwable?) {
                Toasty.info(requireContext(), "No internet connection.", Toast.LENGTH_LONG).show()
            }
        })

    }

    override fun onResume() {
        super.onResume()

        locationListener =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .addSnapshotListener { document, error ->
                        error?.let {
                            Log.d("ERROR", "Error occured.")
                        }
                        document?.let {
                            val latitude = it.data?.get("apiary_latitude").toString()
                            val longitude = it.data?.get("apiary_longitude").toString()
                            fetchWeather(latitude, longitude)
                        }
                    }
            }
    }

    override fun onStop() {
        super.onStop()
        locationListener?.remove()
    }

    private fun inspectionRatingInfo(conditions: ConditionData) {
        val temp = conditions.temp?.roundToInt()
        val wind = conditions.wind?.roundToInt()
        Log.d("TEMP&&HUMID", "Temperature: $temp Humidity: $wind")

        if (conditions.humid!! > 95 || wind!! >= 7 || temp!! < 10) {

            updateInspectionRatingIndex(0, "Very bad")

        } else if (temp in 10..14 && wind <= 6 && conditions.humid in 80..95  ) {

            updateInspectionRatingIndex(35, "Not safe")


        } else if (temp in 15..17 && wind <= 6 ) {

            updateInspectionRatingIndex(65, "Fair")


        } else if (temp in 18..20 && wind <= 6 ) {

            updateInspectionRatingIndex(75, "Good")


        } else if (temp in 21..23 && wind <= 6) {

            updateInspectionRatingIndex(85, "Very good")


        } else if (temp in 23..25 && wind <= 6 ) {

            updateInspectionRatingIndex(90, "Almost perfect")


        } else if (temp > 25 && wind <= 6 ) {

            updateInspectionRatingIndex(100, "Perfect")


        } else {
            updateInspectionRatingIndex(0, "Unknown")

        }
    }

    private fun updateInspectionRatingIndex(progressBar: Int,  description: String) {
        inspectionRatingProgress.progress = progressBar
        inspectionRatingIndex.text = inspectionRatingProgress.progress.toString()
        inspectionDescription.text = description
    }


}





