package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.beenote.R
import com.example.beenote.model.QuickInspection
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_quick_inspection.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finishInspectionBtn.setOnClickListener {


            val quickInspection = QuickInspection(hive_Id,
                getDateAndTime(),
                getCurrentDate(),
                getTextFromRadioButton(honeyStoresRadioGroup),
                getTextFromRadioButton(layingPatternRadioGroup),
                getTextFromRadioButton(populationRadioGroup),
                getTextFromRadioButton(temperRadioGroup),
                getTextFromNotes(),
                listOf(
                    onCheckBoxClicked(queenCheckBox),
                    onCheckBoxClicked(uncappedBroodCheckBox),
                    onCheckBoxClicked(cappedBroodCheckBox),
                    onCheckBoxClicked(eggsCheckBox)
                )
            )
            updateInspectionDataToFirebaseFirestore(quickInspection)
            setLastInspectionDate()
            navigateBacktoHomeFragment(it)
        }
    }

    private fun updateInspectionDataToFirebaseFirestore(inspection: QuickInspection) {

            authUser?.let {
                db.collection("users")
                    .document(it)
                    .collection("hives")
                    .document(hive_Id!!)
                    .collection("inspections")
                    .add(inspection)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Successfully added new inspection", Toast.LENGTH_SHORT).show()
                    }
            }
        }


    private fun setLastInspectionDate() {

            Firebase.auth.currentUser?.uid?.let {
                db.collection("users")
                    .document(it)
                    .set(
                        mapOf(
                            "lastInspection" to FieldValue.serverTimestamp()
                        ), SetOptions.merge()
                    )
                    .addOnSuccessListener {
                        Log.d("LAST INSPECTION", "Last inspection date is ${getCurrentDate()}")
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
                        Log.d("LAST INSPECTION", "Last inspection date is ${getCurrentDate()}")
                    }
            }
        }




    private fun navigateBacktoHomeFragment(v: View) {
        val action = QuickInspectionFragmentDirections.actionQuickInspectionFragmentToHomeFragment()
        Navigation.findNavController(v).navigate(action)
    }


    private fun getTextFromRadioButton(rdGroup: RadioGroup): String {
        val radioBtnID = rdGroup.checkedRadioButtonId
        val radioBtn = view?.findViewById<RadioButton>(radioBtnID)
        return radioBtn?.text.toString()
    }

    private fun getCurrentDate(): String {
        val date = LocalDateTime.now()
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
    }

    private fun getDateAndTime(): Date {
        val calendar = Calendar.getInstance()
        calendar.get(Calendar.DAY_OF_YEAR)
        return calendar.time
    }

    private fun getTextFromNotes(): String =
        if (noteTxt.text.equals("")) "Empty notes" else noteTxt.text.toString()


    private fun onCheckBoxClicked(checkBox: CheckBox) =
        if (checkBox.isChecked) checkBox.text.toString() else "Not seen"


    override fun onResume() {
        super.onResume()

        arguments?.let { bundle ->
            val args = QuickInspectionFragmentArgs.fromBundle(bundle)
            hive_Id = args.hiveId
        }
    }
}
