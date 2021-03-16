package com.boryans.beenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.boryans.beenote.R
import com.boryans.beenote.viewmodels.HiveViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_edit_hive.*
import kotlinx.android.synthetic.main.fragment_edit_hive.hiveNumberEditText
import kotlinx.android.synthetic.main.fragment_edit_hive.queenColorRadioGroup
import kotlinx.android.synthetic.main.fragment_edit_hive.statusRadioGroup


class EditHiveFragment : Fragment(R.layout.fragment_edit_hive) {

    private val hiveViewModel: HiveViewModel by activityViewModels()

    private var hiveId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getHiveIdFromBundle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        editHive.setOnClickListener {

            if (hiveNumberEditText.text.toString().trim().isBlank()) {
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
        val action = EditHiveFragmentDirections.actionEditHiveFragmentToHomeFragment()
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
            hiveNumberEditText.text.toString().toInt(),
            getTextFromRadioButton(queenColorRadioGroup),
            getTextFromRadioButton(statusRadioGroup),
        )
    }

    private fun getHiveIdFromBundle() {
        arguments?.let {
            val args = EditHiveFragmentArgs.fromBundle(it)
            hiveId = args.hiveId
        }
    }


}


