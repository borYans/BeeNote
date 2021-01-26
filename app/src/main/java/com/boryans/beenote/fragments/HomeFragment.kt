package com.boryans.beenote.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DateFormat
import java.util.*


class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    private val authUser = Firebase.auth.currentUser?.uid
    private var stingsListenerRegistration: ListenerRegistration? = null
    private var hivesListenerRegistration: ListenerRegistration? = null
    private var inspectedHivesListenerRegistration: ListenerRegistration? = null
    private var strongHivesListenerRegistration: ListenerRegistration? = null
    private var weakHIvesListenerRegistration: ListenerRegistration? = null
    private var nucleusHivesListenerRegistration: ListenerRegistration? = null
    private var lastInspectionDateListenerRegistration: ListenerRegistration? = null

    private val db = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth
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
        (activity as AppCompatActivity?)?.setSupportActionBar(my_toolbar)
        setHasOptionsMenu(true)

        my_toolbar.inflateMenu(R.menu.home_toolbar_menu)

        my_toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.signOutMenu -> {
                    Firebase.auth.signOut()
                    Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToSignInFragment())
                }
            }
            true
        }





        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val greeting = activity?.getString(R.string.greeting)

        greetingText.text = "$greeting ${currentUser?.displayName}"


        swipeHomeRefresh.setOnRefreshListener {
            refreshHomeFragmentData()
            swipeHomeRefresh.isRefreshing = false
        }

        googleSignInBtn.setOnClickListener {
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

                            lastInspectionDate.text =
                                lastInspectionDoc.data?.get("lastInspection")?.let { date ->
                                    DateFormat.getDateInstance(DateFormat.SHORT)
                                        .format((date as Timestamp).toDate())
                                }
                            if (lastInspectionDate.text == "") { //not clean but it works.
                                lastInspectionDate.text = "--"
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
                            if (document.data?.get("stings") == null) {
                                totalStingsHome.text = "0"
                            } else {
                                totalStingsHome.text = document.data?.get("stings").toString()
                            }
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
                    .whereEqualTo(
                        "hiveStatus",
                        activity?.getString(R.string.hive_status_radio_btn_strong)
                    )
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
                    .whereEqualTo(
                        "hiveStatus",
                        activity?.getString(R.string.hive_status_radio_btn_weak)
                    )
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
                    .whereEqualTo(
                        "hiveStatus",
                        activity?.getString(R.string.hive_status_radio_btn_nucelus)
                    )
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      inflater.inflate(R.menu.home_toolbar_menu, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
           R.id.signOutMenu -> Firebase.auth.signOut()
       }
        return true
    }
}
