package com.example.beenote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.beenote.listeners.InspectionsClickListener
import com.example.beenote.R
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.item_inspection.view.*
import kotlin.collections.ArrayList

class InspectionRecyclerAdapter(
    private val inspectionsClickListener: InspectionsClickListener
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


    inner class InspectionViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener {

        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            inspectionsClickListener.onInspectionLongCLick(items[adapterPosition].id)
            return true
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inspection, parent, false)
        return InspectionViewHolder(view)
    }


    override fun onBindViewHolder(holder: InspectionViewHolder, position: Int) {

        val inspections = items[position]
        holder.itemView.dateOfInspection.text ="${holder.itemView.context.getString(R.string.hive_inspection_recycler_item)} ${inspections.get("dateCreated").toString()}"


        holder.itemView.setOnClickListener {
            inspectionsClickListener.onInspectionClick(inspections.id, position, it)
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

