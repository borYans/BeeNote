package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.beenote.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_sting.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment() {


    private val authUser = Firebase.auth.currentUser?.uid
    private var stingsListenerRegistration: ListenerRegistration? = null
    private var hivesListenerRegistration: ListenerRegistration? = null
    private var inspectedHivesListenerRegistration: ListenerRegistration? = null
    private var dateListenerRegistration: ListenerRegistration? = null

    private val db = FirebaseFirestore.getInstance()

    private val calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        addNewHiveLayout.setOnClickListener {
            navigateToAddNewHiveFragment(it)
        }

        addNewStingLayout.setOnClickListener {
            navigateToAddStingFragment(it)
        }

        currentWeatherConditionsLayout.setOnClickListener {
            navigateToWeatherFragment(it)
        }

        quickInspectionBtn.setOnClickListener {
            navigateToQuickInspectionFragment(it)
        }

        viewAllHivesLayout.setOnClickListener {
            navigateToListOfHivesFragment(it)
        }

    }

    private fun getLastInspectionDate() {
        Firebase.auth.currentUser?.uid?.let {
            db.collection("last_inspection")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        lastInspectionDate.text =
                            document.data?.get("lastInspection").toString()
                    }

                }
        }
    }


    private fun getDateSevenDaysAgo(): Date {
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        return calendar.time
    }


    private fun navigateToAddNewHiveFragment(v: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToAddNewHiveFragment()
        Navigation.findNavController(v).navigate(action)
    }

    private fun navigateToWeatherFragment(v: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToWeatherFragment()
        Navigation.findNavController(v).navigate(action)
    }

    private fun navigateToAddStingFragment(v: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToAddStingFragment()
        Navigation.findNavController(v).navigate(action)
    }

    private fun navigateToQuickInspectionFragment(v: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToQuickInspectionFragment()
        Navigation.findNavController(v).navigate(action)
    }

    private fun navigateToListOfHivesFragment(v: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToHivesListFragment()
        Navigation.findNavController(v).navigate(action)
    }

    override fun onStart() {
        super.onStart()
        getLastInspectionDate()
    }

    override fun onResume() {
        super.onResume()

        stingsListenerRegistration =
            authUser.let {
                db.collection("stings")
                    .document(it!!)
                    .addSnapshotListener { document, error ->
                        error?.let {
                            Toast.makeText(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        document?.let {
                            totalStingsHome.text = document.data?.get("stings").toString()
                        }
                    }
            }

        hivesListenerRegistration =
            authUser.let {
                db.collection("new_hive")
                    .addSnapshotListener { documents, error ->
                        error?.let {
                            Toast.makeText(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        documents?.let {
                            totalNumberOfHives.text = it.size().toString()
                        }
                    }
            }

        inspectedHivesListenerRegistration =
            authUser.let {
                db.collection("inspection")
                    .whereGreaterThan("date", getDateSevenDaysAgo())
                    .addSnapshotListener { documents, error ->
                        error?.let {
                            Toast.makeText(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        documents?.let {
                            inspectedHiveTxt.text = documents.size().toString()
                        }

                    }
            }


    }

    override fun onPause() {
        super.onPause()
        stingsListenerRegistration?.remove()
        hivesListenerRegistration?.remove()
        inspectedHivesListenerRegistration?.remove()
    }



}
