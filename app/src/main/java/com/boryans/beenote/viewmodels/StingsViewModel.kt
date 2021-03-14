package com.boryans.beenote.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.model.Sting
import com.boryans.beenote.util.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class StingsViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val authUser: String?
) : ViewModel() {

    var stingListenerRegistration: ListenerRegistration? = null

    val stingsData: MutableLiveData<Resource<Sting>> = MutableLiveData()

    init {
        getStingsFromFirebase()
    }

    fun updateStingsFromFirebase(sting: Sting) = viewModelScope.launch {

        authUser?.let {
            db.collection(USERS)
                .document(it)
                .set(sting, SetOptions.merge())
                .addOnSuccessListener {
                    //log message
                }
        }
   }


   fun getStingsFromFirebase() {
        CoroutineScope(Dispatchers.IO).launch {
            stingListenerRegistration = authUser?.let {
                db.collection(USERS)
                    .document(it)
                    .addSnapshotListener { document, error ->
                        document?.let {
                            try {

                                if (document.data?.get("stings") != null) {

                                   val stings = Sting((document.data?.get("stings") as Long?)!!)
                                    stingsData.postValue(Resource.Success(stings))

                                }
                            } catch (e: Exception) {
                                 //log message
                            }
                        }
                        error?.let {
                            stingsData.postValue(Resource.Error("Error occurred."))
                        }
                    }
            }
        }
    }
}


