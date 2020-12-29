package com.example.beenote.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.beenote.Listeners.HiveClickListener
import com.example.beenote.R
import com.example.beenote.constants.Constants
import com.example.beenote.fragments.HivesListFragmentDirections
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_hive.view.*


class HivesRecyclerAdapter(
    private val hiveClickListener: HiveClickListener
) :
    RecyclerView.Adapter<HivesRecyclerAdapter.HivesViewHolder>() {

    private val authUser = Firebase.auth.currentUser?.uid
    private val db = FirebaseFirestore.getInstance()


    private var items = mutableListOf<QueryDocumentSnapshot>() //items can be added or removed.

    fun updateHivesList(hivesList: ArrayList<QueryDocumentSnapshot>) {
        val oldList = items
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            HiveItemDiffCallback(
                oldList,
                hivesList
            )
        )
        items = hivesList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class HivesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener {

        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            hiveClickListener.onHiveLongClick(items[adapterPosition].id)
            return true
        }
    }


    //Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HivesViewHolder {

        //Create new view, which defines the UI of the list item.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hive, parent, false)
        return HivesViewHolder(view)
    }

    //Replace the contents of a view (invoked by the layout manager.)
    override fun onBindViewHolder(holder: HivesViewHolder, position: Int) {
        //here I got an element from my dataset and replace the contents of the view with that element.
        // viewHolder.textView.text = dataSet[position]

        val docs = items[position]
        holder.itemView.hiveNameTxt.text = docs.get("hiveName").toString()
        holder.itemView.hiveStatusTxt.text = "Status: " + docs.get("hiveStatus").toString()
        holder.itemView.queenBeeAgeTxt.text = "Queen age: " + docs.get("queenAge").toString()
        // holder.itemView.dateCreatedTxt.text = docs.get("dateCreated").toString()

        holder.itemView.setOnClickListener {
            val action =
                HivesListFragmentDirections.actionHivesListFragmentToInspectionsListFragment(docs.id)
            Navigation.findNavController(it).navigate(action)
        }

        holder.itemView.addNewInspectionBtn.setOnClickListener {
            val action =
                HivesListFragmentDirections.actionHivesListFragmentToQuickInspectionFragment(docs.id)
            Navigation.findNavController(it).navigate(action)
        }

    }

    //Return datasize of my dataset.
    override fun getItemCount() = items.size


}

class HiveItemDiffCallback(
    var oldHivesList: List<QueryDocumentSnapshot>,
    var newHivesList: ArrayList<QueryDocumentSnapshot>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldHivesList.size
    }

    override fun getNewListSize(): Int {
        return newHivesList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldHivesList[oldItemPosition].id == newHivesList[newItemPosition].id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldHivesList[oldItemPosition].equals(newHivesList[newItemPosition])
    }


}




