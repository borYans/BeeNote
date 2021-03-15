package com.boryans.beenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.boryans.beenote.R
import com.boryans.beenote.model.QuickInspection
import com.boryans.beenote.util.Resource
import com.boryans.beenote.viewmodels.InspectionViewModel
import kotlinx.android.synthetic.main.fragment_inspection_detail.*

class InspectionDetailFragment : Fragment(R.layout.fragment_inspection_detail) {
    private val inspectionViewModel: InspectionViewModel by activityViewModels()

    private var inspectionId: String? = null
    private var hive_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getHiveAndInspectionIdFromBundle()
    }


    override fun onResume() {
        super.onResume()

        getDetailedInspectionDocument()
        inspectionViewModel.inspectionDetails.observe(viewLifecycleOwner, { inspectionData->
            when (inspectionData) {
                is Resource.Success -> {
                    populateInspectionDetailScreen(inspectionData)
                }
            }
        })
    }

    private fun populateInspectionDetailScreen(inspection: Resource<QuickInspection>) {

        temperamentTxt.text = inspection.data?.temperament
        populationTxt.text = inspection.data?.population
        honeyStoresTxt.text = inspection.data?.honeyStores
        layingPatternTxt.text = inspection.data?.layingPattern
        framesBrood.text = inspection.data?.broodFrames

        inspection.data?.notes.let { notes ->

            displayNotesTxt.text = notes
            if (displayNotesTxt.text.isBlank()) displayNotesTxt.text = getString(R.string.no_added_notes_text)
        }

        queenSeenTxt.text = inspection.data?.observed?.get(0).toString()
        cappedBroodSeenTxt.text = inspection.data?.observed?.get(1).toString()
        uncappedBroodSeenTxt.text = inspection.data?.observed?.get(2).toString()
        eggsSeenTxt.text = inspection.data?.observed?.get(3).toString()

    }

    override fun onStop() {
        super.onStop()
        inspectionViewModel.inspectionDetailsListenerRegistration?.remove()
    }


    private fun getDetailedInspectionDocument() {
        hive_id?.let {
            inspectionId?.let { it1 ->
            inspectionViewModel.getDetailedInspectionDocument(it, it1)
        } }
    }

    private fun getHiveAndInspectionIdFromBundle() {
        arguments?.let {
            val args = InspectionDetailFragmentArgs.fromBundle(it)
            val args1 = InspectionsListFragmentArgs.fromBundle(it)
            inspectionId = args.inspectionId
            hive_id = args1.hiveId
        }
    }
}