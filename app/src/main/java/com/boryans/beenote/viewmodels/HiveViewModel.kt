package com.boryans.beenote.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.INSPECTIONS
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.model.Hive
import com.boryans.beenote.model.Interventions
import com.boryans.beenote.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiveViewModel @Inject constructor(
    private val firebaseDatabase: FirebaseFirestore,
    private val firebaseAuthUser: String?
): ViewModel() {

    //objects
    private lateinit var interventions: Interventions

    //listeners registration
     var hivesListenerRegistration: ListenerRegistration? = null
     var inspectionsListenerRegistration: ListenerRegistration? = null

    //live data objects
    val allHives: MutableLiveData<Resource<ArrayList<QueryDocumentSnapshot>>> = MutableLiveData()
    val allInterventions: MutableLiveData<Resource<Interventions>> = MutableLiveData()
    val allInspections: MutableLiveData<Resource<ArrayList<QueryDocumentSnapshot>>> = MutableLiveData()
    

    fun updateHives(hive: Hive) = viewModelScope.launch {
        try {
            firebaseAuthUser?.let {
                firebaseDatabase.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .add(hive)
            }
        } catch (e: Exception) {
            //log it
        }
    }
    
    
    fun getAllHives() = viewModelScope.launch {

        try {
            hivesListenerRegistration =
                firebaseAuthUser?.let {
                    firebaseDatabase.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .orderBy("hiveNumber", Query.Direction.ASCENDING)
                        .addSnapshotListener { documents, error ->
                            documents?.let {
                                
                               val hivesList = ArrayList<QueryDocumentSnapshot>()
                                for (document in documents) {
                                    hivesList.add(document)
                                }
                                
                                allHives.postValue(Resource.Success(hivesList))
                            }
                            error?.let {
                                allHives.postValue(Resource.Error("Error occurred."))
                            }
                        }
                }
        } catch (e: Exception) {
            //log message
        }
    }
    
    fun getAllInterventions(hiveId: String)  = viewModelScope.launch {

        try {
            firebaseAuthUser?.let {
                firebaseDatabase.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .document(hiveId)
                    .addSnapshotListener { hive, error ->
                        hive?.let {

                            if (hive.data?.get("treatment") != null && hive.data?.get("swarmingSoon") != null && hive.data?.get(
                                    "feeding"
                                ) != null
                            ) {
                                val varoaMites = hive.data?.get("treatment") as Boolean
                                val feedingHive = hive.data?.get("feeding") as Boolean

                                interventions = Interventions(varoaMites, feedingHive)

                                allInterventions.postValue(Resource.Success(interventions))

                            }
                        }
                    }
            }

        } catch (e: Exception) {
            //log message
        }
    }
        

    
    
    fun updateAllInterventionsToFirebase(hiveId: String, treatmentCheckBox: Boolean, swarmingCheckBox: Boolean) =
        
        viewModelScope.launch { 
            
        try {
            firebaseAuthUser?.let {
                firebaseDatabase.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .document(hiveId)
                    .update(
                        mapOf(
                            "treatment" to treatmentCheckBox,
                            "swarmingSoon" to swarmingCheckBox
                        )
                    )
            }
        } catch (e: Exception) {
            //log message
        }
    }
    
    fun deleteHive(hiveId: String)  = viewModelScope.launch { 
        
        try {
            firebaseAuthUser?.let {
                firebaseDatabase.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .document(hiveId)
                    .delete()
            }
        } catch (e: Exception) {
            
        }

    }
    
    fun getAllInspectionsFromFirebase(hiveId: String)  = viewModelScope.launch { 
        
        try {
            inspectionsListenerRegistration =
                firebaseAuthUser?.let {
                    firebaseDatabase.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .document(hiveId)
                        .collection(Constants.INSPECTIONS)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { inspections, error ->
                            inspections?.let {

                                val inspectionsList = ArrayList<QueryDocumentSnapshot>()
                                for (document in inspections) {
                                    inspectionsList.add(document)
                                }
                                allInspections.postValue(Resource.Success(inspectionsList))
                            }
                            error?.let {
                              allInspections.postValue(Resource.Error("Error occurred."))
                            }
                        }
                }
        } catch (e: Exception) {
            
        }
    }

    fun deleteInspection(hiveId: String, position: String)  = viewModelScope.launch { 
        
        try {
            firebaseAuthUser?.let {
                firebaseDatabase.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .document(hiveId)
                    . collection(INSPECTIONS)
                    .document(position)
                    .delete()
            }
        } catch (e: Exception) {
        }
    }


    fun editHives(hiveId: String, hiveName: Int, queenAge: String, hiveStatus: String) = viewModelScope.launch {

        try {
            firebaseAuthUser?.let {
                firebaseDatabase.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .document(hiveId)
                    .update(
                        mapOf(
                            "hiveNumber" to hiveName,
                            "queenColor" to queenAge,
                            "hiveStatus" to hiveStatus
                        )
                    )
                    .addOnSuccessListener {
                    }
            }

        } catch (e: Exception) {
            //log it
        }
    }
}