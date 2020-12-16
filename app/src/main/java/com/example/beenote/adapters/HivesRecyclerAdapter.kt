package com.example.beenote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beenote.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.item_hive.view.*

class HivesRecyclerAdapter(private val hivesList: ArrayList<QueryDocumentSnapshot>): RecyclerView.Adapter<HivesRecyclerAdapter.HivesViewHolder>() {


    fun updateHivesList(newHivesList: ArrayList<QueryDocumentSnapshot>) {
        hivesList.clear()
        hivesList.addAll(newHivesList)
        notifyDataSetChanged()
    }

    class HivesViewHolder(view:View): RecyclerView.ViewHolder(view)


    //Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HivesViewHolder {

        //Create new view, which defines the UI of the list item.
        val view = LayoutInflater.from(parent.context).
                inflate(R.layout.item_hive, parent, false)

        return HivesViewHolder(view)
    }

    //Replace the contents of a view (invoked by the layout manager.)
    override fun onBindViewHolder(holder: HivesViewHolder, position: Int) {
        //here I got an element from my dataset and replace the contents of the view with that element.
        // viewHolder.textView.text = dataSet[position]



        val docs = hivesList[position]
        holder.itemView.hiveNameTxt.text = docs.get("hive name").toString()
        holder.itemView.hiveStatusTxt.text = "Status: " + docs.get("hive_status").toString()
        holder.itemView.queenBeeAgeTxt.text = "Queen age: " + docs.get("queen_age").toString()
       // holder.itemView.dateCreatedTxt.text = docs.get("hive_created").toString()

    }



    //Return datasize of my dataset.
    override fun getItemCount() = hivesList.size





}




