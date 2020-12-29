package com.example.beenote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.beenote.R
import com.example.beenote.fragments.InspectionsListFragmentDirections
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.item_inspection.view.*
import java.util.*
import kotlin.collections.ArrayList

class InspectionRecyclerAdapter(
) : RecyclerView.Adapter<InspectionRecyclerAdapter.InspectionViewHolder>() {

    private var items = ArrayList<QueryDocumentSnapshot>()


    fun updateInspectionsList(newInspectionsList: ArrayList<QueryDocumentSnapshot>) {
        val oldList = items
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            InspectionItemDiffCallback(
                oldList,
                newInspectionsList
            )
        )
        items = newInspectionsList
        diffResult.dispatchUpdatesTo(this)
    }


    class InspectionViewHolder(view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inspection, parent, false)
        return InspectionViewHolder(view)
    }


    override fun onBindViewHolder(holder: InspectionViewHolder, position: Int) {

        val inspections = items[position]
        holder.itemView.dateOfInspection.text ="Hive inspection: ${inspections.get("dateCreated").toString()}"


        holder.itemView.setOnClickListener {
            val action = InspectionsListFragmentDirections.actionInspectionsListFragmentToInspectionDetailFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }


    override fun getItemCount() = items.size

}

class InspectionItemDiffCallback(
    var oldInspectionList: List<QueryDocumentSnapshot>,
    var newInspectionList: List<QueryDocumentSnapshot>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldInspectionList.size
    }

    override fun getNewListSize(): Int {
        return newInspectionList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldInspectionList[oldItemPosition].id == newInspectionList[newItemPosition].id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldInspectionList[oldItemPosition].equals(newInspectionList[newItemPosition])
    }

}

