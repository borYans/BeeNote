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
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.util.Resource
import com.boryans.beenote.viewmodels.HomeViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.reminder_dialog.*
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by activityViewModels()

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }

    private var clicked: Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.setSupportActionBar(my_toolbar)
        setHasOptionsMenu(true)


        addFab.setOnClickListener {
            onAddButtonClicked()
        }
        addNewHiveFab.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddNewHiveFragment())
        }
        addNewReminderFab.setOnClickListener {
            setTaskReminder()
        }
        addNewStingFab.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddStingFragment())
        }


        taskReminder.setOnLongClickListener(View.OnLongClickListener {

            AlertDialog.Builder(requireContext()).apply {
                setTitle("Delete")
                setMessage("Delete task reminder?")
                setCancelable(false)
                setPositiveButton(activity?.getString(R.string.positive_message)) { dialogInterface, which ->

                    homeViewModel.deleteTaskReminder()

                }
                setNegativeButton(activity?.getString(R.string.negative_message)) { dialogInterface, which ->
                    dialogInterface.cancel()
                }
                create()
                show()
            }



            return@OnLongClickListener true
        })


        totalStingsHome.setOnClickListener {
            Toasty.info(
                requireContext(),
                getString(R.string.reset_number_of_stings_text),
                Toast.LENGTH_SHORT
            ).show()
        }

        totalStingsHome.setOnLongClickListener(View.OnLongClickListener {

            AlertDialog.Builder(requireContext()).apply {
                setTitle(activity?.getString(R.string.reset_stings_title))
                setMessage(activity?.getString(R.string.reset_stings))
                setCancelable(false)
                setPositiveButton(activity?.getString(R.string.positive_message)) { dialogInterface, which ->

                    homeViewModel.resetAllStings()

                }
                setNegativeButton(activity?.getString(R.string.negative_message)) { dialogInterface, which ->
                    dialogInterface.cancel()
                }
                create()
                show()
            }

            return@OnLongClickListener true
        })


        my_toolbar.inflateMenu(R.menu.home_toolbar_menu)
        my_toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.signOutMenu -> {
                    homeViewModel.signOut()
                    val action = requireView().findNavController()
                    action.popBackStack()
                    action.navigate(R.id.signInFragment)
                }
                // R.id.addNewHive ->
                R.id.yourHives -> Navigation.findNavController(requireView())
                    .navigate(HomeFragmentDirections.actionHomeFragmentToHivesListFragment())
                // R.id.addSting ->
                R.id.meteoConditions -> Navigation.findNavController(requireView())
                    .navigate(HomeFragmentDirections.actionHomeFragmentToWeatherFragment())

            }
            true
        }



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

                homeViewModel.updateTaskReminderToFirebase(reminder.text.toString())

            }
            setNegativeButton("Cancel") { dialogInterface, which ->
                dialogInterface.cancel()
            }
            create()
            show()
        }

    }

    private fun refreshHomeFragmentData() {

        homeViewModel.getInspectedHivesInLastSevenDays()
        homeViewModel.inspectedHivesInPastSevenDays.observe(viewLifecycleOwner, { inspectedHives ->
            when(inspectedHives) {
                is Resource.Success -> {
                    inspectedHives.let { hives ->
                        inspectedHiveTxt.text = hives.data  ?: "--"
                    }
                }
            }
        })


        homeViewModel.getStings()
        homeViewModel.stings.observe(viewLifecycleOwner,  { stings ->
            when(stings) {
                is Resource.Success -> {
                    stings.let { ouch ->
                        totalStingsHome.text = ouch.data  ?: "--"
                    }
                }
            }
        })

        homeViewModel.getLastInspectionDate()
        homeViewModel.lastInspection.observe(viewLifecycleOwner,  { date ->
            when(date) {
                is Resource.Success -> {
                    date.let { lastDate ->
                        val inspectionDate = lastDate.data ?: "--"
                        lastInspectionDate.text = inspectionDate
                    }
                }
            }
        })


        homeViewModel.getNumberOfStrongHives(activity?.getString(R.string.hive_status_radio_btn_strong)!!)
        homeViewModel.strongHives.observe(viewLifecycleOwner,  { hives ->
            when(hives) {
                is Resource.Success -> {
                    hives.let { strong ->
                        strongHivesText.text = strong.data  ?: "--"
                    }
                }
            }
        })

        homeViewModel.getNumberOfWeakHives(activity?.getString(R.string.hive_status_radio_btn_weak)!!)
        homeViewModel.weakHives.observe(viewLifecycleOwner, { hives ->
            when(hives) {
                is Resource.Success -> {
                    hives.let { weak ->
                        weakHivesTxt.text = weak.data  ?: "--"
                    }
                }
            }
        })

        homeViewModel.getTotalNumberOfHives()
        homeViewModel.allHives.observe(viewLifecycleOwner,  { hives ->
            when(hives) {
                is Resource.Success -> {
                    hives.let { totalHives ->
                        totalNumberOfHives.text = totalHives.data  ?: "--"
                    }
                }
            }
        })


        homeViewModel.getNumberOfNucelusHives(activity?.getString(R.string.hive_status_radio_btn_nucelus)!!)
        homeViewModel.nucleusHives.observe(viewLifecycleOwner, { nucleuses ->
            when(nucleuses) {
                is Resource.Success -> {
                    nucleuses.let { hives ->
                        nucleusHivesTxt.text = hives.data  ?: "--"
                    }
                }
            }
        })



        homeViewModel.getCurrentUserName()
        homeViewModel.currentUser.observe(
            viewLifecycleOwner, { userName ->
                        when (userName) {
                            is Resource.Success -> {
                                userName.let {  username ->
                                     val greeting = activity?.getString(R.string.greeting)
                                     greetingText.text = "$greeting ${username.data}"
                                }
                            }
                        }
                    })



        homeViewModel.getTaskReminder()
        homeViewModel.taskReminder.observe(viewLifecycleOwner,  { response->
            when(response) {
                is Resource.Success -> {
                    response.let { task ->
                        taskReminder.text = task.data ?: "--"
                    }
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        refreshHomeFragmentData()
    }

    override fun onPause() {
        super.onPause()
        homeViewModel.cancelListenersRegistration()
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }


}

