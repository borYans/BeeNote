package com.boryans.beenote.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.boryans.beenote.listeners.InspectionsClickListener
import com.boryans.beenote.R
import com.boryans.beenote.adapters.InspectionRecyclerAdapter
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.INSPECTIONS
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.util.Resource
import com.boryans.beenote.viewmodels.HiveViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_inspections_list.*


class InspectionsListFragment : Fragment(R.layout.fragment_inspections_list), InspectionsClickListener {
    
    private var hive_id: String? = null
    private val inspectionListAdapter = InspectionRecyclerAdapter(this)
    
    private val hiveViewModel: HiveViewModel by activityViewModels()
    

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
        addNewInspection.setOnClickListener {
            val action = InspectionsListFragmentDirections.actionInspectionsListFragmentToQuickInspectionFragment(hive_id!!)
            Navigation.findNavController(it).navigate(action)
        }

        inspectionsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inspectionListAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        getHiveIdFromArguments()

        hive_id?.let {
            hiveViewModel.getAllInspectionsFromFirebase(it)
        }

        hiveViewModel.allInspections.observe(viewLifecycleOwner, { inspection ->
            when(inspection) {
                is Resource.Success -> {
                    inspection.let { list->
                        list.data?.let { inspectionListAdapter.updateInspectionsList(it) }                    }
                }
            }

        })
    }

    private fun getHiveIdFromArguments() {
        arguments?.let {
            val args = InspectionsListFragmentArgs.fromBundle(it)
            hive_id = args.hiveId

        }
    }

    override fun onStop() {
        super.onStop()
        hiveViewModel.inspectionsListenerRegistration?.remove()
    }

    override fun onInspectionClick(id: String, position: Int, v: View) {
        val action = InspectionsListFragmentDirections.actionInspectionsListFragmentToInspectionDetailFragment(id, hive_id!!)
        Navigation.findNavController(v).navigate(action)
    }

    override fun onInspectionLongCLick(position: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(activity?.getString(R.string.title_confirm_deletion))
            .setMessage(activity?.getString(R.string.message_confirm_deletion_inspection))
            .setCancelable(false)
            .setIcon(R.drawable.ic_info_24)
            .setPositiveButton(activity?.getString(R.string.positive_message)) { dialogInterface, which ->
        
                //delete inspection
                hive_id?.let {
                    hiveViewModel.deleteInspection(it, position)
                }
            }
            .setNegativeButton(activity?.getString(R.string.negative_message)) { dialogInterface, which ->
                dialogInterface.cancel()
            }
            .create()
            .show()
    }

}
