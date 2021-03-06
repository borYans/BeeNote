package com.boryans.beenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.model.Sting
import com.boryans.beenote.util.Resource
import com.boryans.beenote.viewmodels.StingsViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_add_sting.*


class AddStingFragment : Fragment(R.layout.fragment_add_sting) {

    private val stingsViewModel: StingsViewModel by activityViewModels()

    private var stingCounter = 0
    private var currentNumberOfStings: Long = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stingNumberTxt.setOnClickListener {
            stingCounter++
            stingNumberTxt.text = stingCounter.toString()
        }

        addSting.setOnClickListener {
            if (stingCounter != 0) {

                val totalStingsToAdd = stingCounter + currentNumberOfStings
                val sting = Sting(totalStingsToAdd)

                stingsViewModel.updateStingsFromFirebase(sting)
                navigateBackToHome(it)

                Toasty.success(
                    requireContext(),
                    "$stingCounter ${activity?.getString(R.string.sting_added)}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toasty.info(
                    requireContext(),
                    activity?.getString(R.string.add_sting_text)!!,
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun navigateBackToHome(v: View) {
        val action = v.findNavController()
        action.popBackStack()
        action.navigate(R.id.homeFragment)
    }

    override fun onResume() {
        super.onResume()

        stingsViewModel.stingsData.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let { stings ->
                        totalNumberOfStings.text = stings.stings.toInt().toString()
                        currentNumberOfStings = stings.stings
                    }
                }

                is Resource.Error -> {
                    response.message?.let { message ->
                        Toasty.info(requireContext(), "An error occurred: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        stingsViewModel.stingListenerRegistration?.remove()
    }

}


