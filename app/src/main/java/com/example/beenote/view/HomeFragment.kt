package com.example.beenote.view

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
import kotlinx.android.synthetic.main.fragment_add_sting.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class HomeFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var numberOfInspections: Int = 0 // counter for inspected hives documents in past 7 days


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        relativeLayout.setOnClickListener {
            navigateToAddNewHiveFragment(it)
        }

        relativeLayout3.setOnClickListener {
            navigateToAddStingFragment(it)
        }

        relativeLayout2.setOnClickListener {
            navigateToWeatherFragment(it)
        }

        quickInspectionBtn.setOnClickListener {
            navigateToQuickInspectionFragment(it)
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
        Firebase.auth.currentUser?.uid.let {
            db.collection("inspection")
                .whereLessThan("timestamp", getCurrentDate())
                .whereGreaterThan("timestamp", getDateSevenDaysAgo())
                .get()
                .addOnSuccessListener { documents ->
                    documents.let {
                        for (document in documents) {

                            Log.d("DOCUMENT", "Document id: ${document.data}")

                            document.let {
                                numberOfInspections++ // get the number of docs from the query
                            }
                            inspectedHiveTxt.text = numberOfInspections.toString()
                        }

                    }

                }
                .addOnFailureListener {
                    Log.d("ERROR", "Exeption: $it")
                }
        }
        numberOfInspections = 0 //reset counter
    }

    private fun getCurrentDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.get(Calendar.DAY_OF_YEAR)
        return calendar.time
    }

    private fun getDateSevenDaysAgo(): Date {
        val calendar = Calendar.getInstance()
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

    private fun displayAllDataForHomeScreen() {
        getInspectedHivesThisWeek()
        getTotalStingsFromFirebaseFirestore()
    }

    override fun onStart() {
        super.onStart()
        displayAllDataForHomeScreen()
    }


}
