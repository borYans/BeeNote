package com.boryans.beenote.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.boryans.beenote.listeners.HiveClickListener
import com.boryans.beenote.R
import com.boryans.beenote.adapters.HivesRecyclerAdapter
import com.boryans.beenote.model.Interventions
import com.boryans.beenote.util.Resource
import com.boryans.beenote.viewmodels.HiveViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_hives_list.*
import kotlinx.android.synthetic.main.symbol_dialog.*

class HivesListFragment : Fragment(R.layout.fragment_hives_list), HiveClickListener {

    
    private val hivesListAdapter = HivesRecyclerAdapter(this)
    private val hiveViewModel: HiveViewModel by activityViewModels()
    
    private var isSnackBarShowedOnce = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inspectionsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hivesListAdapter
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        hiveViewModel.getAllHives()
        hiveViewModel.allHives.observe(viewLifecycleOwner, { hivesList ->
            when(hivesList) {
                is Resource.Success -> {
                    hivesList.let { list ->

                        if (list.data?.isEmpty() == true && !isSnackBarShowedOnce) {
                            askForAddingNewHive()
                        } else {
                            list.data?.let { hivesListAdapter.updateHivesList(it) }
                        }
                    }
                }
            }
        })
    }

    private fun askForAddingNewHive() {
        if (!isSnackBarShowedOnce) {
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
                hiveViewModel.deleteHive(position)
         
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

        checkAllCheckboxesInDialog(position, treatment, feeding, swarming)


        AlertDialog.Builder(requireContext()).apply {
            setTitle("Mark intervention")
            setView(customDialogLayout)
            setCancelable(false)
            setPositiveButton("Save") { dialogInterface, which ->

                hiveViewModel.updateAllInterventionsToFirebase(position, treatment.isChecked, feeding.isChecked, swarming.isChecked)

            }
            setNegativeButton("Cancel") { dialogInterface, which ->
                dialogInterface.cancel()
            }
            create()
            show()
        }

    }

    private fun checkAllCheckboxesInDialog(position: String, treatment: CheckBox, feeding: CheckBox, swarming: CheckBox) {
        hiveViewModel.getAllInterventions(position)
        hiveViewModel.allInterventions.observe(viewLifecycleOwner, { intervention ->
            when (intervention) {
                is Resource.Success -> {
                    intervention.let {
                        if (it.data?.treatment!!) treatment.isChecked = true
                        if (it.data.feeding) feeding.isChecked = true
                        if (it.data.swarmingSoon) swarming.isChecked = true
                    }
                }
            }
        })
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
        hiveViewModel.hivesListenerRegistration?.remove()
    }

}