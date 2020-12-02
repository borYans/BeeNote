package com.example.beenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.navigation.Navigation
import com.example.beenote.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_quick_inspection.*
import kotlinx.android.synthetic.main.fragment_quick_inspection.view.*


class QuickInspectionFragment : Fragment() {


    private val db = FirebaseFirestore.getInstance()


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
            updateInspectionDataToFirebaseFirestore()
            navigateBacktoHomeFragment(it)
        }

    }

    private fun updateInspectionDataToFirebaseFirestore() {
        Firebase.auth.currentUser?.uid?.let { it1 ->
            db.collection("inspection")
                .document(it1)
                .set(mapOf(
                    "Notes" to noteTxt.text.toString(),
                    "honey stores" to getTextFromRadioButton(honeyStoresRadioGroup),
                    "laying pattern" to getTextFromRadioButton(layingPatternRadioGroup),
                    "population" to getTextFromRadioButton(populationRadioGroup),
                    "temperament" to getTextFromRadioButton(temperRadioGroup),
                    "observed" to listOf<String>("Queen", "Eggs", "Uncapped brood", "Capped brood")

                ))
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



}