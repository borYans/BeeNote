package com.example.beenote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beenote.R
import com.example.beenote.adapters.HivesRecyclerAdapter
import com.example.beenote.constants.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_hives_list.*

class HivesListFragment : Fragment() {

    private val authUser = Firebase.auth.currentUser?.uid
    private var hivesListenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    private val hivesListAdapter = HivesRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hives_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        inspectionsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hivesListAdapter
        }

    }


    override fun onResume() {
        super.onResume()
        hivesListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .collection(Constants.HIVES)
                    .addSnapshotListener { documents, error ->
                        error?.let {
                            Toast.makeText(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        documents?.let {
                            val arrayList = ArrayList<QueryDocumentSnapshot>()
                            for (document in documents) {
                                arrayList.add(document)
                            }
                            hivesListAdapter.updateHivesList(arrayList)
                        }
                    }
            }

    }

    override fun onStop() {
        super.onStop()
        hivesListenerRegistration?.remove()
    }
}