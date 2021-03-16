package com.boryans.beenote.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boryans.beenote.api.RetrofitInstance
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.constants.Constants.Companion.APP_ID
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.model.WeatherDataModel
import com.boryans.beenote.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseFirestore,
    private val firebaseAuthUser: String?
) : ViewModel() {

    init {
        getCoordinates()
    }

    //Registrations listeners
    var locationListener: ListenerRegistration? = null

    //Live data
    var responseWeather: MutableLiveData<Resource<WeatherDataModel>> = MutableLiveData()
    var coordinate: MutableLiveData<Resource<String>> = MutableLiveData()

    //Location coordinates
    private var latitude: String? = null
    private var longitude: String? = null


    private fun requestWeatherConditions(lat: String, lon: String) {

        val call = RetrofitInstance.api.getCurrentWeather(lat, lon, APP_ID)
        call.enqueue(object : Callback<WeatherDataModel> {
            override fun onResponse(
                call: Call<WeatherDataModel>?,
                response: Response<WeatherDataModel>?
            ) {
                val weatherResponse = response?.body()
                weatherResponse?.let { weather ->
                    responseWeather.postValue(Resource.Success(weather))
                }
            }

            override fun onFailure(call: Call<WeatherDataModel>?, t: Throwable?) {
                responseWeather.postValue(Resource.Error("No internet connection."))
            }
        })
    }


    fun fetchWeather() = viewModelScope.launch {
        try {
            locationListener =
                firebaseAuthUser?.let {
                    firebaseDatabase.collection(USERS)
                        .document(it)
                        .addSnapshotListener { document, error ->
                            document?.let {
                                try {
                                    val latitude = it.data?.get("apiary_latitude").toString()
                                    val longitude = it.data?.get("apiary_longitude").toString()
                                    requestWeatherConditions(latitude, longitude)
                                } catch (e: Exception) {
                                    //log message
                                }
                            }
                        }
                }
        } catch (e: Exception) {
            //log message
        }
    }


    private fun getCoordinates() = viewModelScope.launch {
        try {
            firebaseAuthUser?.let {
                firebaseDatabase.collection(USERS)
                    .document(it)
                    .addSnapshotListener { document, error ->
                        document?.let { coordinates ->
                            if (coordinates.data?.get("apiary_latitude") == null) {
                                coordinate.postValue(Resource.Error("No location added. Add new location."))
                            }
                            latitude = coordinates.data?.get("apiary_latitude").toString()
                            longitude = coordinates.data?.get("apiary_longitude").toString()
                        }
                    }
            }
        } catch (e: Exception) {
            //log message
        }
    }


}

/*
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

        */
