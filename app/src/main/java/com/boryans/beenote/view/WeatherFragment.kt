package com.boryans.beenote.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.boryans.beenote.R
import com.boryans.beenote.api.RetrofitInstance
import com.boryans.beenote.application.Application
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.constants.Constants.Companion.APP_ID
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.model.Repository
import com.boryans.beenote.model.WeatherDataModel
import com.boryans.beenote.util.Resource
import com.boryans.beenote.viewmodels.WeatherViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt


@AndroidEntryPoint
class  WeatherFragment() : Fragment(R.layout.fragment_weather) {


    private val weatherViewModel: WeatherViewModel by activityViewModels()

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

        inspectonRating.setOnClickListener {
            val action = WeatherFragmentDirections.actionWeatherFragmentToInspectionRatingInfoFragment()
            Navigation.findNavController(it).navigate(action)
        }

        swipeRefresh.setOnRefreshListener {
            getCurrentApiaryWeatherConditions()
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        //I will check only one coordinate, if it's null then provide user with snackbar to add new location.
        getCurrentApiaryWeatherConditions()
    }

    override fun onStop() {
        super.onStop()
        weatherViewModel.locationListener?.remove()
    }

    private fun getCurrentApiaryWeatherConditions() {
        weatherViewModel.fetchWeather()
        weatherViewModel.responseWeather.observe(viewLifecycleOwner, { weather->
            when(weather) {
                is Resource.Success -> {
                    weather.let { conditions ->
                        weatherProgressBar.visibility = View.GONE
                        currentTemperature.visibility = View.VISIBLE

                        currentTemperature.text = "${conditions.data?.main?.temp?.minus(273.15)?.roundToInt().toString()}${activity?.resources?.getString(R.string.celsius_sign)}"

                        humidity.text = conditions.data?.main?.humidity.toString() + "%"
                        windSpeedTxt.text = "${conditions.data?.wind?.speed?.roundToInt().toString()} m/s "
                        cloudCoverTxt.text = conditions.data?.clouds?.all.toString() + "%"
                        nameOfTheCity.text = conditions.data?.name
                        weatherDescriptionTxt.text = conditions.data?.weather?.get(0)?.description
                        inspectionRatingInfo(conditions)

                    }
                }
            }

        })
    }

    private fun inspectionRatingInfo(conditions: Resource<WeatherDataModel>) {   // move to WeatherViewModel
        val temp = conditions.data?.main?.temp?.minus(273.15)?.roundToInt()
        val wind = conditions.data?.wind?.speed?.roundToInt()
        val humid = conditions.data?.main?.humidity
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





