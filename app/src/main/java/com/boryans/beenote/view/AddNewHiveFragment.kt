package com.boryans.beenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.model.Hive
import com.boryans.beenote.viewmodels.HiveViewModel
import com.google.firebase.firestore.FieldValue
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_add_new_hive.*


class AddNewHiveFragment : Fragment(R.layout.fragment_add_new_hive) {

    private val hiveViewModel: HiveViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipBtn.setOnClickListener {
            val hive = Hive(
                hiveNumberEditText.text.toString().toInt(),
                getTextFromRadioButton(queenColorRadioGroup),
                getTextFromRadioButton(statusRadioGroup),
                FieldValue.serverTimestamp()
            )

            if (hiveNumberEditText.text.toString().trim().isBlank()) {
                Toasty.info(
                    requireContext(),
                    activity?.getString(R.string.hive_data_cannot_be_empty)!!,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                hiveViewModel.updateHives(hive)
                navigateBackToHome(it)
            }
        }
    }

    private fun getTextFromRadioButton(rdGroup: RadioGroup): String {
        val radioBtnID = rdGroup.checkedRadioButtonId
        val radioBtn = view?.findViewById<RadioButton>(radioBtnID)
        return radioBtn?.text.toString()
    }

    private fun navigateBackToHome(v: View) {
        val action = v.findNavController()
        action.popBackStack()
        action.navigate(R.id.homeFragment)
    }
}