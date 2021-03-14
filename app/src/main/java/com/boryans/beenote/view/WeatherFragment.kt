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
            swipeRefresh.isRefreshing = false
            //refresh UI
        }
    }

    override fun onResume() {
        super.onResume()
        weatherViewModel.fetchWeather()
        weatherViewModel.responseWeather.observe(viewLifecycleOwner, { weather->
            when(weather) {
                is Resource.Success -> {
                    weather.let {
                        //populate UI here
                    }
                }
            }

        })


    }

    override fun onStop() {
        super.onStop()
        weatherViewModel.locationListener?.remove()
    }

    private fun inspectionRatingInfo(conditions: Repository) {   // move to WeatherViewModel
        val temp = conditions.temp?.roundToInt()
        val wind = conditions.wind?.roundToInt()
        val humid = conditions.humid
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





