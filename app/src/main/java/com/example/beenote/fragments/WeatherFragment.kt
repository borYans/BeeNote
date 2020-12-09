package com.example.beenote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.beenote.R
import com.example.beenote.model.WeatherApiService
import kotlinx.android.synthetic.main.fragment_weather.*


class WeatherFragment() : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        inspectionRatingInfo.setOnClickListener {
            //navigate to inspectionRatingFragment
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }

    }


}





