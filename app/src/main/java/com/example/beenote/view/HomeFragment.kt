package com.example.beenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.beenote.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        relativeLayout.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddNewHiveFragment()
            Navigation.findNavController(it).navigate(action)
        }

        relativeLayout3.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddStingFragment()
            Navigation.findNavController(it).navigate(action)
        }

        relativeLayout2.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToWeatherFragment()
            Navigation.findNavController(it).navigate(action)
        }


        quickInspectionBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToQuickInspectionFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }



}
