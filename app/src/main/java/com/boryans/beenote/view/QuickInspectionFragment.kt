package com.boryans.beenote.view

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants.Companion.HIVES
import com.boryans.beenote.constants.Constants.Companion.INSPECTIONS
import com.boryans.beenote.constants.Constants.Companion.USERS
import com.boryans.beenote.model.QuickInspection
import com.boryans.beenote.viewmodels.InspectionViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_quick_inspection.*
import kotlinx.android.synthetic.main.item_hive.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.Exception


class QuickInspectionFragment : Fragment(R.layout.fragment_quick_inspection) {


    private val inspectionViewModel : InspectionViewModel by activityViewModels()

    private var hive_Id: String? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finishInspectionBtn.setOnClickListener {
            setQuickInspectionModelObject()?.let { inspection ->
                setQuickInspectionToFirebase(inspection)
            }
            setLastInspectionDate()
            navigateBackToHomeFragment(it)
        }

        numberOfFrames.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                numberOfBroodFrames.text = progress.toString()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setQuickInspectionModelObject() = hive_Id?.let {
        QuickInspection(
            it,
        FieldValue.serverTimestamp(),
        getCurrentDate(),
        getTextFromRadioButton(honeyStoresRadioGroup)!!,
        getTextFromRadioButton(layingPatternRadioGroup)!!,
        getTextFromRadioButton(populationRadioGroup)!!,
        getTextFromRadioButton(temperRadioGroup)!!,
        noteTxt.text.toString(),
        numberOfFrames.progress.toString(),
        listOf(
            onCheckBoxClicked(queenCheckBox),
            onCheckBoxClicked(uncappedBroodCheckBox),
            onCheckBoxClicked(cappedBroodCheckBox),
            onCheckBoxClicked(eggsCheckBox)
        )
    )
    }

    private fun setQuickInspectionToFirebase(quickInspection: QuickInspection) {
        inspectionViewModel.setDetailedInspectionDocument(hive_Id!!, quickInspection)
    }

    override fun onResume() {
        super.onResume()
        getHiveIdFromBundle()
    }

    private fun getHiveIdFromBundle() {
        arguments?.let { bundle ->
            val args = QuickInspectionFragmentArgs.fromBundle(bundle)
            hive_Id = args.hiveId
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLastInspectionDate() {
        inspectionViewModel.setLastInspectionDate(hive_Id!!)
    }


    private fun navigateBackToHomeFragment(v: View) {
        val action = v.findNavController()
        action.popBackStack()
        action.navigate(R.id.homeFragment)
    }


    private fun getTextFromRadioButton(rdGroup: RadioGroup): String? {
        val radioBtnID = rdGroup.checkedRadioButtonId
        val radioBtn = view?.findViewById<RadioButton>(radioBtnID)
        return radioBtn?.let { button ->
            val choice = button.text.toString()
            choice
        } ?: "X"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val date = LocalDateTime.now()
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
    }


    private fun onCheckBoxClicked(checkBox: CheckBox) =
        if (checkBox.isChecked) activity?.getString(R.string.yes)!! else activity?.getString(R.string.no)!!

}
