package com.boryans.beenote.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.boryans.beenote.listeners.HiveClickListener
import com.boryans.beenote.R
import com.boryans.beenote.adapters.HivesRecyclerAdapter
import com.boryans.beenote.constants.Constants
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_hives_list.*

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
            Snackbar.make(requireView(), activity?.getText(R.string.empty_list_snackbar)!!, Snackbar.LENGTH_LONG)
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
                    db.collection(Constants.USERS)
                        .document(it)
                        .collection(Constants.HIVES)
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


    override fun onStop() {
        super.onStop()
        hivesListenerRegistration?.remove()
    }

}