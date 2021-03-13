package com.boryans.beenote.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.util.Resource
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.DateFormat
import java.util.*

class HomeViewModel : ViewModel() {


    //Firebase firestore setup
    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    
    //ccalendar instance
    private val calendar = Calendar.getInstance()


    //Listeners Registrations
    var nucleusHivesListenerRegistration: ListenerRegistration? = null
    var stingsListenerRegistration: ListenerRegistration? = null
    var hivesListenerRegistration: ListenerRegistration? = null
    var inspectedHivesListenerRegistration: ListenerRegistration? = null
    var strongHivesListenerRegistration: ListenerRegistration? = null
    var weakHIvesListenerRegistration: ListenerRegistration? = null
    var lastInspectionDateListenerRegistration: ListenerRegistration? = null
    var reminderNoteListenerRegistration: ListenerRegistration? = null

    
    //Live Data objects
    var taskReminder: MutableLiveData<Resource<String?>> = MutableLiveData()
    var allHives: MutableLiveData<Resource<String?>> = MutableLiveData()
    var currentUser: MutableLiveData<Resource<String>> = MutableLiveData()
    var nucleusHives: MutableLiveData<Resource<String>> = MutableLiveData()
    var weakHives: MutableLiveData<Resource<String>> = MutableLiveData()
    var strongHives: MutableLiveData<Resource<String>> = MutableLiveData()
    var lastInspection: MutableLiveData<Resource<String>> = MutableLiveData()
    var inspectedHivesInPastSevenDays: MutableLiveData<Resource<String>> = MutableLiveData()
    var stings: MutableLiveData<Resource<String>> = MutableLiveData()

    fun updateTaskReminderToFirebase(reminderNote: String) = viewModelScope.launch {
        try {
            authUser?.let {
                db.collection(USERS)
                    .document(it)
                    .set(
                        mapOf(
                            "reminderNote" to reminderNote
                        ), SetOptions.merge()
                    )
            }
        } catch (e: Exception) {
            //log message
        }
    }


    fun getNumberOfNucelusHives(query: String) {

        viewModelScope.launch {
            try {
                nucleusHivesListenerRegistration =
                    authUser?.let {
                        db.collection(Constants.USERS)
                            .document(it)
                            .collection(Constants.HIVES)
                            .whereEqualTo(
                                "hiveStatus",
                                query
                            )
                            .addSnapshotListener { snapshots, error ->

                                snapshots?.let {
                                    Log.d("TAG", "value of nucleus hives: ${it.size()}")
                                    nucleusHives.postValue(Resource.Success(it.size().toString()))
                                }
                            }
                    }
            } catch (e: Exception) {
                //log it
            }
        }
    }


     fun getNumberOfWeakHives(query: String) {

        viewModelScope.launch {
            try {
                weakHIvesListenerRegistration =
                    authUser?.let {
                        db.collection(Constants.USERS)
                            .document(it)
                            .collection(Constants.HIVES)
                            .whereEqualTo(
                                "hiveStatus",
                                query
                                //activity?.getString(R.string.hive_status_radio_btn_weak)
                            )
                            .addSnapshotListener { snapshots, error ->
                                snapshots?.let {
                                    // weakHivesTxt.text = it.size().toString()
                                    weakHives.postValue(Resource.Success(it.size().toString()))

                                }
                            }
                    }
            } catch (e: Exception) {
                //log it
            }
        }
    }

     fun getNumberOfStrongHives(query: String) {

        viewModelScope.launch {
            try {
                strongHivesListenerRegistration =
                    authUser?.let {
                        db.collection(Constants.USERS)
                            .document(it)
                            .collection(Constants.HIVES)
                            .whereEqualTo(
                                "hiveStatus",
                                query
                                // activity?.getString(R.string.hive_status_radio_btn_strong)
                            )
                            .addSnapshotListener { snapshots, error ->
                                snapshots?.let {
                                    //strongHivesText.text = it.size().toString()
                                    strongHives.postValue(Resource.Success(it.size().toString()))
                                }
                            }
                    }
            } catch (e: Exception) {
                //log it
            }
        }

    }

     fun getInspectedHivesInLastSevenDays() {

        viewModelScope.launch {
            try {
                inspectedHivesListenerRegistration =
                    authUser?.let {
                        db.collection(Constants.USERS)
                            .document(it)
                            .collection(Constants.HIVES)
                            .whereGreaterThan("lastInspection", getDateSevenDaysAgo())
                            .addSnapshotListener { documents, error ->
                                documents?.let {
                                    //inspectedHiveTxt.text =
                                    inspectedHivesInPastSevenDays.postValue(
                                        Resource.Success(
                                            documents.size().toString()
                                        )
                                    )
                                }

                            }
                    }
            } catch (e: Exception) {
                //log it
            }
        }

    }

     fun getTotalNumberOfHives() {

        viewModelScope.launch {
            try {
                hivesListenerRegistration =
                    authUser?.let {
                        db.collection(USERS)
                            .document(it)
                            .collection(HIVES)
                            .addSnapshotListener { documents, error ->
                                documents?.let {
                                    // totalNumberOfHives.text = it.size().toString()
                                    allHives.postValue(Resource.Success(it.size().toString()))

                                }
                            }
                    }
            } catch (e: Exception) {
                //log it
            }
        }
    }

     fun getStings() {
        viewModelScope.launch {
            try {
                stingsListenerRegistration =
                    authUser?.let {
                        db.collection(Constants.USERS)
                            .document(it)
                            .addSnapshotListener { document, error ->
                                error?.let {
                                    //Resource.Error()
                                }
                                document?.let {
                                    if (document.data?.get("stings") == null) {
                                        // totalStings = 0
                                    } else {
                                        //totalStingsHome.text =
                                        stings.postValue(
                                            Resource.Success(
                                                document.data?.get("stings").toString()
                                            )
                                        )
                                    }
                                }
                            }
                    }
            } catch (e: Exception) {
                //log it
            }
        }

    }

     fun getLastInspectionDate() {

        viewModelScope.launch {
            try {
                lastInspectionDateListenerRegistration =
                    authUser?.let {
                        db.collection(USERS)
                            .document(it)
                            .addSnapshotListener { snapshot, error ->
                                error?.let {
                                    //log message
                                }
                                snapshot?.let { lastInspectionDoc ->

                                    try {
                                        val lastInspectionDate =
                                            lastInspectionDoc.data?.get("lastInspection")
                                                ?.let { date ->
                                                    DateFormat.getDateInstance(DateFormat.SHORT)
                                                        .format((date as Timestamp).toDate())
                                                }

                                        lastInspection.postValue(Resource.Success(lastInspectionDate))
                                    } catch (e: Exception) {
                                        //log message
                                    }

                                }
                            }
                    }
            } catch (e: Exception) {
                //log it
            }
        }
    }

     fun getTaskReminder() {

        viewModelScope.launch {
            try {
                reminderNoteListenerRegistration =
                    authUser?.let {
                        db.collection(USERS)
                            .document(it)
                            .addSnapshotListener { document, error ->
                                document?.let {

                                    if (document.data?.get("reminderNote") != null) {
                                        taskReminder.postValue(
                                            Resource.Success(
                                                document.data?.get("reminderNote").toString()
                                            )
                                        )

                                    }
                                }

                            }
                    }
            } catch (e: Exception) {
                //logmessage
            }
        }

    }

    fun resetAllStings() = viewModelScope.launch {
        try {
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .set(
                        mapOf(
                            "stings" to 0
                        ), SetOptions.merge()
                    )
            }
        } catch (e: Exception) {
            //log message
        }
    }

    fun deleteTaskReminder() = viewModelScope.launch {
        try {
            val updates = hashMapOf<String, Any>(
                "reminderNote" to " "
            )
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .set(updates, SetOptions.merge())
                    .addOnCompleteListener { }

            }
        } catch (e: Exception) {
            //log message
        }
    }

    fun getCurrentUserName() {
        currentUser.postValue(Resource.Success(mAuth.currentUser?.displayName!!))
    }

    fun signOut() {
        mAuth.signOut()
    }

    fun cancelListenersRegistration() {
        stingsListenerRegistration?.remove()
        hivesListenerRegistration?.remove()
        inspectedHivesListenerRegistration?.remove()
        strongHivesListenerRegistration?.remove()
        weakHIvesListenerRegistration?.remove()
        nucleusHivesListenerRegistration?.remove()
        lastInspectionDateListenerRegistration?.remove()
        reminderNoteListenerRegistration?.remove()
    }

    private fun getDateSevenDaysAgo(): Date {
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        return calendar.time
    }
}