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
import com.example.beenote.constants.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DateFormat
import java.util.*

class HomeFragment : Fragment() {


    private val authUser = Firebase.auth.currentUser?.uid
    private var stingsListenerRegistration: ListenerRegistration? = null
    private var hivesListenerRegistration: ListenerRegistration? = null
    private var inspectedHivesListenerRegistration: ListenerRegistration? = null
    private var strongHivesListenerRegistration: ListenerRegistration? = null
    private var weakHIvesListenerRegistration: ListenerRegistration? = null
    private var nucleusHivesListenerRegistration: ListenerRegistration? = null
    private var lastInspectionDateListenerRegistration: ListenerRegistration? = null

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


        swipeHomeRefresh.setOnRefreshListener {
            refreshHomeFragmentData()
            swipeHomeRefresh.isRefreshing = false
        }

        addNewHiveLayout.setOnClickListener {
            navigateToAddNewHiveFragment(it)
        }

        addNewStingLayout.setOnClickListener {
            navigateToAddStingFragment(it)
        }

        currentWeatherConditionsLayout.setOnClickListener {
            navigateToWeatherFragment(it)
        }

        viewAllHivesLayout.setOnClickListener {
            navigateToListOfHivesFragment(it)
        }

    }

    private fun refreshHomeFragmentData() {
        lastInspectionDateListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .addSnapshotListener { snapshot, error ->
                        error?.let {
                            Log.d("@@@", "Error occured: $error")
                        }
                        snapshot?.let { lastInspectionDoc ->
                            lastInspectionDate.text = lastInspectionDoc.data?.get("lastInspection")?.let { date ->
                                DateFormat.getDateInstance(DateFormat.SHORT)
                                    .format((date as Timestamp).toDate())
                            }

                        }
                    }
            }
        stingsListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
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
                            totalNumberOfHives.text = it.size().toString()
                        }
                    }
            }

        inspectedHivesListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .collection(Constants.HIVES)
                    .whereGreaterThan("lastInspection", getDateSevenDaysAgo())
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

        strongHivesListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .collection(Constants.HIVES)
                    .whereEqualTo("hiveStatus", "strong")
                    .addSnapshotListener { snapshots, error ->
                        error?.let {
                            Toast.makeText(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        snapshots?.let {
                            strongHivesText.text = it.size().toString()
                        }
                    }
            }



        weakHIvesListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .collection(Constants.HIVES)
                    .whereEqualTo("hiveStatus", "weak")
                    .addSnapshotListener { snapshots, error ->
                        error?.let {
                            Toast.makeText(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        snapshots?.let {
                            weakHivesTxt.text = it.size().toString()
                        }
                    }
            }


        nucleusHivesListenerRegistration =
            authUser?.let {
                db.collection(Constants.USERS)
                    .document(it)
                    .collection(Constants.HIVES)
                    .whereEqualTo("hiveStatus", "nucleus")
                    .addSnapshotListener { snapshots, error ->
                        error?.let {
                            Toast.makeText(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        snapshots?.let {
                            nucleusHivesTxt.text = it.size().toString()
                        }
                    }

            }

    }

    override fun onResume() {
        super.onResume()
        refreshHomeFragmentData()
    }

    override fun onPause() {
        super.onPause()
        cancelListenerRegistrations()
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


    private fun navigateToListOfHivesFragment(v: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToHivesListFragment()
        Navigation.findNavController(v).navigate(action)
    }

    private fun cancelListenerRegistrations() {
        stingsListenerRegistration?.remove()
        hivesListenerRegistration?.remove()
        inspectedHivesListenerRegistration?.remove()
        strongHivesListenerRegistration?.remove()
        weakHIvesListenerRegistration?.remove()
        nucleusHivesListenerRegistration?.remove()
        lastInspectionDateListenerRegistration?.remove()
    }
}
