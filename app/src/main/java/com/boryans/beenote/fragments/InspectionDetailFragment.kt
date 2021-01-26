package com.boryans.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_inspection_detail.*

class InspectionDetailFragment : Fragment() {


    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    private var inspectionId: String? = null
    private var hive_id: String? = null

    private var inspectionDetailsListenerRegistration: ListenerRegistration? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = InspectionDetailFragmentArgs.fromBundle(it)
            val args1 = InspectionsListFragmentArgs.fromBundle(it)
            inspectionId = args.inspectionId
            hive_id = args1.hiveId
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inspection_detail, container, false)

    }

    override fun onResume() {
        super.onResume()
        inspectionDetailsListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .collection(Constants.HIVES)
                    .document(hive_id!!)
                    .collection(Constants.INSPECTIONS)
                    .document(inspectionId!!)
                    .addSnapshotListener { document, error ->
                        error?.let {
                            Log.d("ERROR", "Error occurred!")
                        }
                        document?.let {
                            temperamentTxt.text = it.data?.get("temperament").toString()
                            populationTxt.text = it.data?.get("population").toString()
                            honeyStoresTxt.text = it.data?.get("honeyStores").toString()
                            layingPatternTxt.text = it.data?.get("layingPattern").toString()
                            displayNotesTxt.text = it.data?.get("notes").toString()
                            val observed = it.data?.get("observed") as List<*>

                            queenSeenTxt.text = observed[0].toString()
                            cappedBroodSeenTxt.text = observed[1].toString()
                            uncappedBroodSeenTxt.text = observed[2].toString()
                            eggsSeenTxt.text = observed[3].toString()

                        }
                    }
            }
    }

    override fun onStop() {
        super.onStop()
        inspectionDetailsListenerRegistration?.remove()
    }


}