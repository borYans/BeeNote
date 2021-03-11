package com.boryans.beenote.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.boryans.beenote.listeners.HiveClickListener
import com.boryans.beenote.R
import com.boryans.beenote.adapters.HivesRecyclerAdapter
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_hives_list.*
import kotlinx.android.synthetic.main.item_hive.*
import kotlinx.android.synthetic.main.symbol_dialog.*

class HivesListFragment : Fragment(), HiveClickListener {

    private val authUser = Firebase.auth.currentUser?.uid
    private var hivesListenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    private val hivesListAdapter = HivesRecyclerAdapter(this)
    private var isSnackBarShowedOnce = false

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
                db.collection(USERS)
                    .document(it)
                    .collection(HIVES)
                    .orderBy("dateCreated", Query.Direction.DESCENDING)
                    .addSnapshotListener { documents, error ->
                        error?.let {
                            Toast.makeText(
                                requireContext(),
                                "Error occured: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        documents?.let {
                            val hivesList = ArrayList<QueryDocumentSnapshot>()
                            for (document in documents) {
                                hivesList.add(document)
                            }
                            if (hivesList.isEmpty() && !isSnackBarShowedOnce) {
                                askForAddingNewHive(isSnackBarShowedOnce)
                            } else {
                                hivesListAdapter.updateHivesList(hivesList)
                            }
                        }
                    }
            }
    }

    private fun askForAddingNewHive(shown: Boolean) {
        if (!shown) {
            Snackbar.make(
                requireView(),
                activity?.getText(R.string.empty_list_snackbar)!!,
                Snackbar.LENGTH_LONG
            )
                .setBackgroundTint(resources.getColor(R.color.darkBackgroundColor))
                .setActionTextColor(resources.getColor(R.color.yellowText))
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAction(activity?.getString(R.string.empty_list_snackbar_add)) {
                    val action =
                        HivesListFragmentDirections.actionHivesListFragmentToAddNewHiveFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                }
                .show()
        }
    }

    private fun informUserForActions(position: String) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog
            .setTitle(activity?.getString(R.string.title_confirm_deletion))
            .setMessage(activity?.getString(R.string.message_confirm_deletion))
            .setCancelable(false)
            .setPositiveButton(activity?.getString(R.string.positive_message)) { dialogInterface, which ->

                isSnackBarShowedOnce = true
                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .document(position)
                        .delete()
                }
            }
            .setNegativeButton(activity?.getString(R.string.negative_message)) { dialogInterface, which ->
                dialogInterface.cancel()
            }
            .create()
            .show()
    }

    override fun onHiveLongClicked(position: String) {
        informUserForActions(position)
    }

    override fun onHiveClicked(position: String) {
        informUserForActions(position)
    }

    override fun onAddSignClicked(position: String) {

        val customDialogLayout = layoutInflater.inflate(R.layout.symbol_dialog, null)
        val swarming: CheckBox = customDialogLayout.findViewById(R.id.swarmingSoon)
        val feeding: CheckBox = customDialogLayout.findViewById(R.id.addFeeding)
        val treatment: CheckBox = customDialogLayout.findViewById(R.id.treatedHive)


        authUser?.let {
            db.collection(USERS)
                .document(it)
                .collection(HIVES)
                .document(position)
                .addSnapshotListener { hive, error ->
                    hive?.let {

                        if (hive.data?.get("treatment") != null && hive.data?.get("swarmingSoon") != null && hive.data?.get(
                                "feeding"
                            ) != null
                        ) {
                            val varoaMites = hive.data?.get("treatment") as Boolean
                            val swarmingHive = hive.data?.get("swarmingSoon") as Boolean
                            val feedingHive = hive.data?.get("feeding") as Boolean

                            if (varoaMites) treatment.isChecked = true
                            if (swarmingHive) swarming.isChecked = true
                            if (feedingHive) feeding.isChecked = true
                        }


                    }
                }
        }


        AlertDialog.Builder(requireContext()).apply {
            setTitle("Mark intervention")
            setView(customDialogLayout)
            setCancelable(false)
            setPositiveButton("Save") { dialogInterface, which ->


                authUser?.let {
                    db.collection(USERS)
                        .document(it)
                        .collection(HIVES)
                        .document(position)
                        .update(
                            mapOf(
                                "treatment" to treatment.isChecked,
                                "feeding" to feeding.isChecked,
                                "swarmingSoon" to swarming.isChecked
                            )
                        )
                }
            }
            setNegativeButton("Cancel") { dialogInterface, which ->
                dialogInterface.cancel()
            }
            create()
            show()
        }

    }


    fun varoaAndSwarminginfo(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).apply {
            setBackgroundTint(resources.getColor(R.color.darkBackgroundColor))
            setActionTextColor(resources.getColor(R.color.yellowText))
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
            show()
        }
    }


    override fun onStop() {
        super.onStop()
        hivesListenerRegistration?.remove()
    }

}