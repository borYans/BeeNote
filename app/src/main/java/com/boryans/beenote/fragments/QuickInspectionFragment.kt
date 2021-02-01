package com.boryans.beenote.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.model.QuickInspection
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_quick_inspection.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class QuickInspectionFragment : Fragment() {

    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    private var hive_Id: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quick_inspection, container, false)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numberOfFrames.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                numberOfBroodFrames.text = progress.toString()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })


        finishInspectionBtn.setOnClickListener {

            val quickInspection = QuickInspection(
                hive_Id,
                FieldValue.serverTimestamp(),
                getCurrentDate(),
                getTextFromRadioButton(honeyStoresRadioGroup),
                getTextFromRadioButton(layingPatternRadioGroup),
                getTextFromRadioButton(populationRadioGroup),
                getTextFromRadioButton(temperRadioGroup),
                noteTxt.text.toString(),
                numberOfFrames.progress.toString(),
                listOf(
                    onCheckBoxClicked(queenCheckBox),
                    onCheckBoxClicked(uncappedBroodCheckBox),
                    onCheckBoxClicked(cappedBroodCheckBox),
                    onCheckBoxClicked(eggsCheckBox)
                )
            )
            updateInspectionDataToFirebaseFirestore(quickInspection)
            setLastInspectionDate()
            navigateBackToHomeFragment(it)
        }
    }

    override fun onResume() {
        super.onResume()

        arguments?.let { bundle ->
            val args = QuickInspectionFragmentArgs.fromBundle(bundle)
            hive_Id = args.hiveId
        }
    }

    private fun updateInspectionDataToFirebaseFirestore(inspection: QuickInspection) {

        authUser?.let {
            db.collection(Constants.USERS)
                .document(it)
                .collection(Constants.HIVES)
                .document(hive_Id!!)
                .collection(Constants.INSPECTIONS)
                .add(inspection)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLastInspectionDate() {

        authUser?.let {
            db.collection("users")
                .document(it)
                .set(
                    mapOf(
                        "lastInspection" to FieldValue.serverTimestamp()
                    ), SetOptions.merge()
                )
                .addOnSuccessListener {
                  //log message
                }
            db.collection("users")
                .document(it)
                .collection("hives")
                .document(hive_Id!!)
                .set(
                    mapOf(
                        "lastInspection" to FieldValue.serverTimestamp()
                    ), SetOptions.merge()
                )
                .addOnSuccessListener {
                    //log message
                }
        }
    }


    private fun navigateBackToHomeFragment(v: View) {
        val action = v.findNavController()
        action.popBackStack()
        action.navigate(R.id.homeFragment)
    }


    private fun getTextFromRadioButton(rdGroup: RadioGroup): String {
        val radioBtnID = rdGroup.checkedRadioButtonId
        val radioBtn = view?.findViewById<RadioButton>(radioBtnID)
        return radioBtn?.text.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDate(): String {
        val date = LocalDateTime.now()
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
    }


    private fun onCheckBoxClicked(checkBox: CheckBox) =
        if (checkBox.isChecked) activity?.getString(R.string.yes)!! else activity?.getString(R.string.no)!!

}
