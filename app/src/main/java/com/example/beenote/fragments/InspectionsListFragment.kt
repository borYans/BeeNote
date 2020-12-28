package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beenote.R
import com.example.beenote.adapters.InspectionRecyclerAdapter
import com.example.beenote.constants.Constants
import com.example.beenote.utils.InspectionClickListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_inspections_list.*


class InspectionsListFragment : Fragment(), InspectionClickListener  {


    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    private var inspectionsListenerRegistration: ListenerRegistration? = null

    private var hive_id: String? = null

    private val inspectionListAdapter = InspectionRecyclerAdapter(arrayListOf(), this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inspections_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        .collection("inspections")
                        .addSnapshotListener { inspections, error ->
                            error?.let {
                                Toast.makeText(requireContext(), "Error occurred.", Toast.LENGTH_SHORT).show()
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

    override fun onInspectionClickListener() {
    }

}
