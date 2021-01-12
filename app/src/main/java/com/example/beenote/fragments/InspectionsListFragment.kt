package com.example.beenote.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beenote.listeners.InspectionsClickListener
import com.example.beenote.R
import com.example.beenote.adapters.InspectionRecyclerAdapter
import com.example.beenote.constants.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_inspections_list.*


class InspectionsListFragment : Fragment(), InspectionsClickListener {


    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    private var inspectionsListenerRegistration: ListenerRegistration? = null

    private var hive_id: String? = null

    private val inspectionListAdapter = InspectionRecyclerAdapter(this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inspections_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        addNewInspection.setOnClickListener {
            val action =
                InspectionsListFragmentDirections.actionInspectionsListFragmentToQuickInspectionFragment(
                    hive_id!!
                )
            Navigation.findNavController(it).navigate(action)
        }

        inspectionsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inspectionListAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        arguments?.let {
            val args = InspectionsListFragmentArgs.fromBundle(it)
            hive_id = args.hiveId

        }

        Log.d("HIVE", "onResume Called!")
        Log.d("HIVE", "Hive id is: $hive_id")
        inspectionsListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .collection(Constants.HIVES)
                    .document(hive_id!!)
                    .collection(Constants.INSPECTIONS)
                    .addSnapshotListener { inspections, error ->
                        error?.let {
                            Toast.makeText(requireContext(), "Error occurred.", Toast.LENGTH_SHORT)
                                .show()
                        }

                        inspections?.let {

                            val inspectionArrayList = ArrayList<QueryDocumentSnapshot>()
                            for (document in inspections) {
                                inspectionArrayList.add(document)
                            }
                            Log.d("HIVE", "$inspectionArrayList")
                            inspectionListAdapter.updateInspectionsList(inspectionArrayList)
                        }
                    }
            }
    }

    override fun onStop() {
        super.onStop()
        inspectionsListenerRegistration?.remove()
    }

    override fun onInspectionClick(id: String, position: Int, v: View) {
        val action =
            InspectionsListFragmentDirections.actionInspectionsListFragmentToInspectionDetailFragment(
                id,
                hive_id!!
            )
        Navigation.findNavController(v).navigate(action)
    }

    override fun onInspectionLongCLick(inspection_id: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Are you sure?")
            .setMessage("Are you sure you want to delete this inspection?")
            .setCancelable(false)
            .setIcon(R.drawable.ic_info_24)
            .setPositiveButton("Yes") { dialogInterface, which ->
                authUser?.let {
                    db.collection(Constants.USERS)
                        .document(it)
                        .collection(Constants.HIVES)
                        .document(hive_id!!)
                        .collection(Constants.INSPECTIONS)
                        .document(inspection_id)
                        .delete()
                }
            }
            .setNegativeButton("No") { dialogInterface, which ->
                dialogInterface.cancel()
            }
            .create()
            .show()
    }


}
