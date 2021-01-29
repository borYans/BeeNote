package com.boryans.beenote.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.boryans.beenote.R
import com.boryans.beenote.application.Application
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.model.Repository
import com.boryans.beenote.model.WeatherDataModel
import com.facebook.login.LoginManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        (activity as AppCompatActivity?)?.setSupportActionBar(weatherToolbar)
        setHasOptionsMenu(true)

        weatherToolbar.inflateMenu(R.menu.weather_toolbar)
        weatherToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.changeLocation -> {

                    Navigation.findNavController(requireView())
                        .navigate(WeatherFragmentDirections.actionWeatherFragmentToMapLocationFragment())
                }
            }
            true
        }


        inspectionRatingInfo.setOnClickListener {
            //navigate to inspectionRatingFragment
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            //refresh UI
        }
    }


    private fun fetchWeather(lat: String, lon: String) {

        val call = (context?.applicationContext as Application).api.getCurrentWeather(
            lat,
            lon,
            Constants.APP_ID
        )
        call.enqueue(object : Callback<WeatherDataModel> {
            override fun onResponse(
                call: Call<WeatherDataModel>?,
                response: Response<WeatherDataModel>?
            ) {
                val weatherResponse = response?.body()
                if (weatherResponse != null) {
                    weatherProgressBar.visibility = View.GONE
                    val conditions = Repository(
                        weatherResponse.main.temp.minus(273.1),
                        weatherResponse.main.humidity,
                        weatherResponse.wind.speed,
                        weatherResponse.clouds.all
                    )
                    inspectionRatingInfo(conditions)
                    currentTemperature.visibility = View.VISIBLE
                    currentTemperature.text = "${
                        conditions.temp?.roundToInt().toString()
                    }${activity?.resources?.getString(R.string.celsius_sign)}"
                    humidity.text = conditions.humid?.toString() + "%"
                    windSpeedTxt.text = conditions.wind?.roundToInt().toString() + "m/s"
                    cloudCoverTxt.text = conditions.clouds?.toString() + "%"

                    nameOfTheCity.text = weatherResponse.name
                    weatherDescriptionTxt.text = weatherResponse.weather[0].description

                } else {
                    Snackbar.make(requireView(), "No added location", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(resources.getColor(R.color.darkBackgroundColor))
                        .setActionTextColor(resources.getColor(R.color.yellowText))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setAction("Add location") {
                            val action =
                                WeatherFragmentDirections.actionWeatherFragmentToAddLocationFragment()
                            Navigation.findNavController(requireView()).navigate(action)
                        }
                        .show()
                }
            }

            override fun onFailure(call: Call<WeatherDataModel>?, t: Throwable?) {
                Toasty.info(
                    requireContext(),
                    activity?.getString(R.string.no_internet_connection)!!,
                    Toast.LENGTH_LONG
                ).show()
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

    private fun inspectionRatingInfo(conditions: Repository) {
        val temp = conditions.temp?.roundToInt()
        val wind = conditions.wind?.roundToInt()
        val humid = conditions.humid
        Log.d("TEMP&&HUMID", "Temperature: $temp Humidity: $wind")
        if (temp != null && wind != null && humid != null) {

            if (humid > 90 || wind >= 8 || temp <= 10) {

                updateInspectionRatingIndex(
                    0,
                    activity?.getString(R.string.inspection_rating_index_very_bad)!!
                )

            } else if (temp in 11..14) {

                updateInspectionRatingIndex(
                    35,
                    activity?.getString(R.string.inspection_rating_index_not_advisable)!!
                )


            } else if (temp in 15..17) {

                updateInspectionRatingIndex(
                    65,
                    activity?.getString(R.string.inspection_rating_index_fair)!!
                )


            } else if (temp in 18..20) {

                updateInspectionRatingIndex(
                    75,
                    activity?.getString(R.string.inspection_rating_index_good)!!
                )


            } else if (temp in 21..23) {

                updateInspectionRatingIndex(
                    85,
                    activity?.getString(R.string.inspection_rating_index_very_good)!!
                )


            } else if (temp in 23..25) {

                updateInspectionRatingIndex(
                    90,
                    activity?.getString(R.string.inspection_rating_index_safe)!!
                )


            } else if (temp > 25) {

                updateInspectionRatingIndex(
                    100,
                    activity?.getString(R.string.inspection_rating_index_safe)!!
                )


            } else {
                updateInspectionRatingIndex(
                    0,
                    activity?.getString(R.string.inspection_rating_index_unknown)!!
                )

            }
        } else {
            Toasty.info(requireContext(), getString(R.string.no_location_added_text), Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateInspectionRatingIndex(progressBar: Int, description: String) {
        inspectionRatingProgress.progress = progressBar
        inspectionRatingIndex.text = inspectionRatingProgress.progress.toString()
        inspectionDescription.text = description
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.weather_toolbar, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changeLocation -> Firebase.auth.signOut()
        }
        return true
    }


}





