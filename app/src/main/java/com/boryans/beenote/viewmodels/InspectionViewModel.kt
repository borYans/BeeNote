package com.boryans.beenote.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.INSPECTIONS
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.model.QuickInspection
import com.boryans.beenote.util.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.lang.Exception

class InspectionViewModel(): ViewModel() {

    //FirebaseSetup
    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()

    //Listener registrations
     var inspectionDetailsListenerRegistration: ListenerRegistration? = null

    //Live data objects
     var inspectionDetails :MutableLiveData<Resource<QuickInspection>> = MutableLiveData()


     fun getDetailedInspectionDocument(hiveId: String, inspectiondId: String) = viewModelScope.launch {

        try {
            inspectionDetailsListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .document(hiveId)
                        .collection(INSPECTIONS)
                        .document(inspectiondId)
                        .addSnapshotListener { document, error ->

                            document?.let { inspection ->


                                val temper = inspection.data?.get("temperament").toString()
                                val population = inspection.data?.get("population").toString()
                                val honeyStores = inspection.data?.get("honeyStores").toString()
                                val layingPattern = inspection.data?.get("layingPattern").toString()
                                val dateCreated = inspection.data?.get("dateCreated").toString()
                                val notes = inspection.data?.get("notes").toString()
                                val broodFrames = inspection.data?.get("broodFrames").toString()
                                val observed = inspection.data?.get("observed") as List<String>

                                val quickInspection = QuickInspection(hiveId,
                                    null , dateCreated, honeyStores, layingPattern,    // don't need it to display in inspection detail fragment so I pass null to constructor of the model class.
                                population, temper, notes, broodFrames,
                                    observed
                                )
                                inspectionDetails.postValue(Resource.Success(quickInspection))
                            }
                        }
                }

        } catch (e:Exception) {
            //log message
        }

    }

     fun setDetailedInspectionDocument(hiveId: String,  inspection: QuickInspection) = viewModelScope.launch {

         try {
             authUser?.let {
                 db.collection(USERS)
                     .document(it)
                     .collection(HIVES)
                     .document(hiveId)
                     .collection(INSPECTIONS)
                     .add(inspection)
             }
         } catch (e: Exception) {
             //log message
         }


     }

    fun setLastInspectionDate(hiveId: String) = viewModelScope.launch {

        try {
            authUser?.let {
                db.collection("users")
                    .document(it)
                    .set(
                        mapOf(
                            "lastInspection" to FieldValue.serverTimestamp()
                        ), SetOptions.merge()
                    )
                    .addOnSuccessListener {
                        //log message
                    }
                db.collection("users")
                    .document(it)
                    .collection("hives")
                    .document(hiveId)
                    .set(
                        mapOf(
                            "lastInspection" to FieldValue.serverTimestamp()
                        ), SetOptions.merge()
                    )
                    .addOnSuccessListener {
                        //log message
                    }
            }
        } catch (e: Exception) {
            //log message
        }
    }
}