package com.boryans.beenote.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.model.Hive
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HiveViewModel: ViewModel() {

    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()

    fun updateHives(hive: Hive) = viewModelScope.launch {
        try {
            authUser?.let {
                db.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .add(hive)
            }
        } catch (e: Exception) {
            //log it
        }
    }
}