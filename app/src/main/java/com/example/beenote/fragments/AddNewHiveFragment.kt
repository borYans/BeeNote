package com.example.beenote.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.beenote.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_new_hive.*
import java.util.*


class AddNewHiveFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_hive, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        addNewHiveBtn.setOnClickListener {
            setNewHives()
            navigateBackToHome(it)
        }
    }


    private fun setNewHives() {
        try {
            Firebase.auth.currentUser?.uid?.let {
                db.collection("new_hive")
                    .add(mapOf(
                        "dateCreated" to getDateAndTime(),
                        "hiveName" to getHiveNameFromEditText(),
                        "hiveStatus" to getTextFromRadioButton(statusRadioGroup),
                        "queenAge" to getQueenAgeFromEditText(),
                    ))
            }
        } catch (e: Exception) {
            Log.d("ERROR", "Error occurred: $e")
        }

    }

    private fun getTextFromRadioButton(rdGroup: RadioGroup): String {
        val radioBtnID = rdGroup.checkedRadioButtonId
        val radioBtn = view?.findViewById<RadioButton>(radioBtnID)
        return radioBtn?.text.toString()
    }

    private fun getDateAndTime(): Date {
        val calendar = Calendar.getInstance()
        calendar.get(Calendar.DAY_OF_YEAR)
        return calendar.time
    }

    private fun getHiveNameFromEditText(): String? {
        var text: String? = null
        if (hiveNameEditText.text.equals("")) {
            Toast.makeText(requireContext(), "You need to enter the hive name.", Toast.LENGTH_SHORT).show()
        } else {
           text = hiveNameEditText.text.toString()
        }
      return text
    }

    private fun getQueenAgeFromEditText(): String? {
        var text: String? = null
        if (queenAgeEditText.text.equals("")) {
            Toast.makeText(requireContext(), "You need to enter the queen age.", Toast.LENGTH_SHORT).show()
        } else {
            text = queenAgeEditText.text.toString()
        }
        return text
    }

    private fun navigateBackToHome(v:View) {
        val action = AddNewHiveFragmentDirections.actionAddNewHiveFragmentToHomeFragment()
        Navigation.findNavController(v).navigate(action)
    }

}