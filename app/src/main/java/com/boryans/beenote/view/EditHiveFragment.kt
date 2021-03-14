package com.boryans.beenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.viewmodels.HiveViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_update_hive.*


class EditHiveFragment : Fragment(R.layout.fragment_update_hive) {
    private val hiveViewModel: HiveViewModel by activityViewModels()

    private var hiveId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getHiveIdFromBundle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateHiveBtn_update.setOnClickListener {

            if (isHiveNameBlank || isQueenAgeBlank) {
                warnUserToAddHiveNameAndQueenAge()
            } else {
                hiveId?.let { id ->
                  editHive(id)
                }
            }
            navigateBackToHome(it)
        }
    }

    private fun getTextFromRadioButton(rdGroup: RadioGroup): String {
        val radioBtnID = rdGroup.checkedRadioButtonId
        val radioBtn = view?.findViewById<RadioButton>(radioBtnID)
        return radioBtn?.text.toString()
    }

    private fun navigateBackToHome(v: View) {
        val action = EditHiveFragmentDirections.actionUpdateHiveFragmentToHomeFragment()
        Navigation.findNavController(v).navigate(action)
    }

    private fun warnUserToAddHiveNameAndQueenAge() {
        Toasty.info(
            requireContext(),
            activity?.getString(R.string.hive_data_cannot_be_empty)!!,
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun editHive(hive_id: String) {
        hiveViewModel.editHives(
            hive_id,
            hiveNameEditText_update.text.toString(),
            queenAgeEditText_update.text.toString(),
            getTextFromRadioButton(statusRadioGroup_update)
        )
    }

    private fun getHiveIdFromBundle() {
        arguments?.let {
            val args = EditHiveFragmentArgs.fromBundle(it)
            hiveId = args.hiveId
        }
    }

    private val isHiveNameBlank = hiveNameEditText_update.text.toString().trim().isBlank()
    private val isQueenAgeBlank = queenAgeEditText_update.text.toString().trim().isBlank()
}

