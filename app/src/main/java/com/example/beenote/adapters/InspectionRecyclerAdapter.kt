package com.example.beenote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beenote.R
import com.example.beenote.utils.InspectionClickListener
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.item_inspection.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InspectionRecyclerAdapter(
    private val inspectionsList: ArrayList<QueryDocumentSnapshot>,
    private val inspectionClickListener: InspectionClickListener // ova ke treba za otvaranje na poedinecen dokument
) : RecyclerView.Adapter<InspectionRecyclerAdapter.InspectionViewHolder>() {


    fun updateInspectionsList(newInspectionsList: ArrayList<QueryDocumentSnapshot>) {
        inspectionsList.clear()
        inspectionsList.addAll(newInspectionsList)
        notifyDataSetChanged()
    }


    class InspectionViewHolder(view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inspection, parent, false)
        return InspectionViewHolder(view)
    }


    override fun onBindViewHolder(holder: InspectionViewHolder, position: Int) {

        val inspections = inspectionsList[position]
        holder.itemView.dateOfInspection.text = inspections.get("date").toString() // konvertiraj go vo date posto sega ti e timestamp.
    }


    override fun getItemCount() = inspectionsList.size

}

