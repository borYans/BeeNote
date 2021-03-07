package com.boryans.beenote.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.facebook.login.LoginManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.reminder_dialog.*
import java.text.DateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val rotateOpen: Animation by lazy {  AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy {  AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy {  AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy {  AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }

    private var clicked: Boolean = false

    private val authUser = Firebase.auth.currentUser?.uid
    private var stingsListenerRegistration: ListenerRegistration? = null
    private var hivesListenerRegistration: ListenerRegistration? = null
    private var inspectedHivesListenerRegistration: ListenerRegistration? = null
    private var strongHivesListenerRegistration: ListenerRegistration? = null
    private var weakHIvesListenerRegistration: ListenerRegistration? = null
    private var nucleusHivesListenerRegistration: ListenerRegistration? = null
    private var lastInspectionDateListenerRegistration: ListenerRegistration? = null
    private var reminderNoteListenerRegistration: ListenerRegistration? = null

    private val db = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth
    private val calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.setSupportActionBar(my_toolbar)
        setHasOptionsMenu(true)


        addFab.setOnClickListener {
            onAddButtonClicked()
        }
        addNewHiveFab.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToAddNewHiveFragment())
        }
        addNewReminderFab.setOnClickListener {
            setTaskReminder()
        }
        addNewStingFab.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToAddStingFragment())
        }


        taskReminder.setOnLongClickListener(View.OnLongClickListener {

            AlertDialog.Builder(requireContext()).apply {
                setTitle("Delete")
                setMessage("Delete task reminder?")
                setCancelable(false)
                setPositiveButton(activity?.getString(R.string.positive_message)) { dialogInterface, which ->

                    val updates = hashMapOf<String, Any>(
                        "reminderNote" to " "
                    )
                    authUser?.let {
                        db.collection(USERS)
                            .document(it)
                            .set(updates, SetOptions.merge())
                            .addOnCompleteListener { }

                    }
                }
                setNegativeButton(activity?.getString(R.string.negative_message)) { dialogInterface, which ->
                    dialogInterface.cancel()
                }
                create()
                show()
            }



            return@OnLongClickListener true
        })




        totalStingsHome.setOnClickListener{
            Toasty.info(requireContext(), getString(R.string.reset_number_of_stings_text), Toast.LENGTH_SHORT).show()
        }

        totalStingsHome.setOnLongClickListener(View.OnLongClickListener {

           AlertDialog.Builder(requireContext()).apply {
                setTitle(activity?.getString(R.string.reset_stings_title))
                setMessage(activity?.getString(R.string.reset_stings))
                setCancelable(false)
                setPositiveButton(activity?.getString(R.string.positive_message)) { dialogInterface, which ->

                    authUser?.let {
                        db.collection(USERS)
                            .document(it)
                            .set(mapOf(
                                "stings" to 0
                            ))
                    }
                }
                setNegativeButton(activity?.getString(R.string.negative_message)) { dialogInterface, which ->
                    dialogInterface.cancel()
                }
                create()
                show()
            }



            return@OnLongClickListener true
        })


        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        my_toolbar.inflateMenu(R.menu.home_toolbar_menu)
        my_toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.signOutMenu -> {
                    mAuth.signOut()
                    LoginManager.getInstance().logOut()
                    val action = requireView().findNavController()
                    action.popBackStack()
                    action.navigate(R.id.signInFragment)
                }
               // R.id.addNewHive ->
                R.id.yourHives ->   Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToHivesListFragment())
                // R.id.addSting ->
                R.id.meteoConditions -> Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToWeatherFragment())





            }
            true
        }

        val greeting = activity?.getString(R.string.greeting)
        greetingText.text = "$greeting ${currentUser?.displayName}"



        swipeHomeRefresh.setOnRefreshListener {
            refreshHomeFragmentData()
            swipeHomeRefresh.isRefreshing = false
        }

    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            addFab.startAnimation(rotateOpen)
            addNewStingFab.startAnimation(fromBottom)
            addNewHiveFab.startAnimation(fromBottom)
            addNewReminderFab.startAnimation(fromBottom)
        } else {
            addFab.startAnimation(rotateClose)
            addNewStingFab.startAnimation(toBottom)
            addNewHiveFab.startAnimation(toBottom)
            addNewReminderFab.startAnimation(toBottom)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            addNewStingFab.visibility = View.VISIBLE
            addNewReminderFab.visibility = View.VISIBLE
            addNewHiveFab.visibility = View.VISIBLE
        } else {
            addNewStingFab.visibility = View.INVISIBLE
            addNewReminderFab.visibility = View.INVISIBLE
            addNewHiveFab.visibility = View.INVISIBLE
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            addNewStingFab.isClickable = true
            addNewReminderFab.isClickable = true
            addNewHiveFab.isClickable = true
        } else {
            addNewStingFab.isClickable = false
            addNewReminderFab.isClickable = false
            addNewHiveFab.isClickable = false
        }
    }

    private fun setTaskReminder() {

        val customDialogLayout = layoutInflater.inflate(R.layout.reminder_dialog, null)
        val reminder: EditText = customDialogLayout.findViewById(R.id.reminderDialogText)


        AlertDialog.Builder(requireContext()).apply {
            setTitle("Add reminder task")
            setView(customDialogLayout)
            setCancelable(false)
            setPositiveButton("Save") { dialogInterface, which ->

                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .set(mapOf(
                            "reminderNote" to reminder.text.toString()
                        ), SetOptions.merge())
                }

            }
            setNegativeButton("Cancel") { dialogInterface, which ->
                dialogInterface.cancel()
            }
            create()
            show()
        }

    }





    private fun refreshHomeFragmentData() {

        try {
            reminderNoteListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .addSnapshotListener { document, error ->
                            document?.let {
                                if (document.data?.get("reminderNote") != null) {
                                    taskReminder.text = document.data?.get("reminderNote").toString()
                                }
                                if (taskReminder.text == "") { //not clean but it works.
                                    taskReminder.text = "---"
                                }
                            }

                        }
                }
        } catch (e: Exception){
            //logmessage
        }


        try {
            lastInspectionDateListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .addSnapshotListener { snapshot, error ->
                            error?.let {
                                //log message
                            }
                            snapshot?.let { lastInspectionDoc ->

                                try {
                                    lastInspectionDate.text =
                                        lastInspectionDoc.data?.get("lastInspection")?.let { date ->
                                            DateFormat.getDateInstance(DateFormat.SHORT)
                                                .format((date as Timestamp).toDate())
                                        }
                                } catch (e: Exception) {
                                    //log message
                                }

                                if (lastInspectionDate.text == "") { //not clean but it works.
                                    lastInspectionDate.text = "--"
                                }

                            }
                        }
                }
        } catch (e: Exception) {
            //log it
        }

        try {
            stingsListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
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
        } catch (e: Exception) {
            //log it
        }

        try {
            hivesListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .addSnapshotListener { documents, error ->
                            documents?.let {
                                totalNumberOfHives.text = it.size().toString()
                            }
                        }
                }
        } catch (e: Exception) {
            //log it
        }


        try {
            inspectedHivesListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .whereGreaterThan("lastInspection", getDateSevenDaysAgo())
                        .addSnapshotListener { documents, error ->
                            documents?.let {
                                inspectedHiveTxt.text = documents.size().toString()
                            }

                        }
                }
        } catch (e: Exception) {
            //log it
        }


        try {
            strongHivesListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .whereEqualTo(
                            "hiveStatus",
                            activity?.getString(R.string.hive_status_radio_btn_strong)
                        )
                        .addSnapshotListener { snapshots, error ->
                            snapshots?.let {
                                strongHivesText.text = it.size().toString()
                            }
                        }
                }
        } catch (e: Exception) {
            //log it
        }


        try {
            weakHIvesListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .whereEqualTo(
                            "hiveStatus",
                            activity?.getString(R.string.hive_status_radio_btn_weak)
                        )
                        .addSnapshotListener { snapshots, error ->
                            snapshots?.let {
                                weakHivesTxt.text = it.size().toString()
                            }
                        }
                }
        } catch (e: Exception) {
            //log it
        }

        try {
            nucleusHivesListenerRegistration =
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .whereEqualTo(
                            "hiveStatus",
                            activity?.getString(R.string.hive_status_radio_btn_nucelus)
                        )
                        .addSnapshotListener { snapshots, error ->

                            snapshots?.let {
                                nucleusHivesTxt.text = it.size().toString()
                            }
                        }

                }
        } catch (e: Exception) {
            //log it
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


    private fun cancelListenerRegistrations() {
        stingsListenerRegistration?.remove()
        hivesListenerRegistration?.remove()
        inspectedHivesListenerRegistration?.remove()
        strongHivesListenerRegistration?.remove()
        weakHIvesListenerRegistration?.remove()
        nucleusHivesListenerRegistration?.remove()
        lastInspectionDateListenerRegistration?.remove()
        reminderNoteListenerRegistration?.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }


}

