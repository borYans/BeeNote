package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.beenote.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var numberOfInspections: Int = 0 // counter for inspected hives documents in past 7 days
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

    private fun getTotalStingsFromFirebaseFirestore() {
        Firebase.auth.currentUser?.uid?.let {
            db.collection("stings")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        totalStingsHome.text = document.data?.get("count").toString()
                    }
                }
        }

    }

    private fun getInspectedHivesThisWeek() {
        Log.d("INSPECTED HIVES", "getInspectedHivesThisWeek() is calling")

        Firebase.auth.currentUser?.uid.let {
            db.collection("inspection")
                .whereLessThan("timestamp", getCurrentDate())
                .whereGreaterThan("timestamp", getDateSevenDaysAgo())
                .get()
                .addOnSuccessListener { documents ->
                    documents.let {
                        for (document in documents) {
                            Log.d("DOCUMENT", "Document id: ${document.data}")

                            numberOfInspections++ // get the number of docs from the query
                            Log.d("INSPECTED HIVES", "Number of inspected hives is: $numberOfInspections")
                            inspectedHiveTxt.text = numberOfInspections.toString()

                        }

                    }

                }
                .addOnFailureListener {
                    Log.d("ERROR", "Exception: $it")
                }
        }
        // numberOfInspections = 0 //reset counter
    }


    private fun getLastInspectionDate() {
        Firebase.auth.currentUser?.uid?.let {
            db.collection("last inspection")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        lastInspectionDate.text =
                            document.data?.get("last inspection timestamp").toString()
                    }

                }
        }
    }

    private fun getCurrentDate(): Date {
        calendar.get(Calendar.DAY_OF_YEAR)
        return calendar.time
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

    private fun displayAllDataForHomeScreen() {
        getInspectedHivesThisWeek()
        getTotalStingsFromFirebaseFirestore()
        getLastInspectionDate()
    }

    override fun onResume() {
        super.onResume()
        displayAllDataForHomeScreen()
    }


}
