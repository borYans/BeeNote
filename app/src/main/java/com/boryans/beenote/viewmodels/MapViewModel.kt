package com.boryans.beenote.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boryans.beenote.constants.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseFirestore,
    private val firebaseAuthUser: String?
): ViewModel() {


    fun updateLocationCoordinatesToFirebase(apiaryLatitude: String, apiaryLongitude: String) = viewModelScope.launch {
        try {
            firebaseAuthUser?.let {
                firebaseDatabase.collection(Constants.USERS)
                    .document(it)
                    .set(
                        mapOf(
                            "apiary_latitude" to apiaryLatitude,
                            "apiary_longitude" to apiaryLongitude
                        ), SetOptions.merge()
                    )
                    .addOnSuccessListener {
                        //log message
                    }
            }
        } catch (e: Exception){
            //log message
        }
    }



}