package com.boryans.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.INSPECTIONS
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_inspection_detail.*
import kotlinx.android.synthetic.main.item_hive.*

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
                db.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .document(hive_id!!)
                    .collection(INSPECTIONS)
                    .document(inspectionId!!)
                    .addSnapshotListener { document, error ->
                        error?.let {
                            //log message
                        }
                        document?.let { inspection ->

                              val temper = inspection.data?.get("temperament").toString()
                             val population = inspection.data?.get("population").toString()
                           val honeyStores = inspection.data?.get("honeyStores").toString()
                           val layingPattern = inspection.data?.get("layingPattern").toString()
                           displayNotesTxt.text  = inspection.data?.get("notes").toString()
                           framesBrood.text  = inspection.data?.get("broodFrames").toString()

                            if (temper != "null") temperamentTxt.text = temper else temperamentTxt.text = "X"
                            if (population != "null") populationTxt.text = population else populationTxt.text= "X"
                            if (honeyStores != "null")  honeyStoresTxt.text = honeyStores else  honeyStoresTxt.text = "X"
                            if (layingPattern != "null")  layingPatternTxt.text = layingPattern else  layingPatternTxt.text = "X"



                            val observed = inspection.data?.get("observed") as List<*>

                            queenSeenTxt.text = observed[0].toString()
                            cappedBroodSeenTxt.text = observed[1].toString()
                            uncappedBroodSeenTxt.text = observed[2].toString()
                            eggsSeenTxt.text = observed[3].toString()


                            if (displayNotesTxt.text.isBlank()) {
                                displayNotesTxt.text = getString(R.string.no_added_notes_text)
                            }

                        }
                    }
            }
    }

    override fun onStop() {
        super.onStop()
        inspectionDetailsListenerRegistration?.remove()
    }


}