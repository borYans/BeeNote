package com.boryans.beenote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.boryans.beenote.R
import com.boryans.beenote.constants.Constants
import com.boryans.beenote.model.Hive
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_add_new_hive.*


class AddNewHiveFragment : Fragment() {

    private val authUser = Firebase.auth.currentUser?.uid
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
            val hive = Hive(
                hiveNameEditText.text.toString(),
                queenAgeEditText.text.toString(),
                getTextFromRadioButton(statusRadioGroup),
                FieldValue.serverTimestamp()
            )

            if (hiveNameEditText.text.toString().trim().isBlank() || queenAgeEditText.text.toString().trim().isBlank() ) {
                Toasty.info(
                    requireContext(),
                    activity?.getString(R.string.hive_data_cannot_be_empty)!!,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                setNewHives(hive)
                navigateBackToHome(it)
            }
        }
    }

    private fun setNewHives(hive: Hive) {
        authUser?.let {
            db.collection(Constants.USERS)
                .document(it)
                .collection(Constants.HIVES)
                .add(hive)
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