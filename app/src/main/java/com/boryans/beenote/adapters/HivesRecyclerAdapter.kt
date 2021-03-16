package com.boryans.beenote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.boryans.beenote.listeners.HiveClickListener
import com.boryans.beenote.R
import com.boryans.beenote.view.HivesListFragmentDirections
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.item_hive.view.*


class HivesRecyclerAdapter(
    private val hiveClickListener: HiveClickListener
) :
    RecyclerView.Adapter<HivesRecyclerAdapter.HivesViewHolder>() {

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
            hiveClickListener.onHiveLongClicked(items[adapterPosition].id)
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HivesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hive, parent, false)
        return HivesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HivesViewHolder, position: Int) {

        val docs = items[position]
        holder.itemView.apply {
            hiveNameTxt.text = docs.get("hiveNumber").toString()
            hiveStatusTxt.text = "${holder.itemView.context.getString(R.string.status_recycler_item)} ${docs.get("hiveStatus").toString()}"


            queenBeeAgeTxt.apply {

                when(docs.get("queenColor").toString()) {
                    "green" -> setTextColor(resources.getColor(android.R.color.holo_green_light))
                    "blue" -> setTextColor(resources.getColor(android.R.color.holo_blue_light))
                    "yellow" -> setTextColor(resources.getColor(R.color.yellowText))
                    "red" -> setTextColor(resources.getColor(android.R.color.holo_red_light))
                    "white" -> setTextColor(resources.getColor(android.R.color.white))
                    else -> setTextColor(resources.getColor(android.R.color.white))
                }

                text = ("${holder.itemView.context.getString(R.string.queen_recycler_item)} ${docs.get("queenColor").toString()} ") //queenColor
            }

            if (docs.get("swarmingSoon") == true) swarmWarning.visibility = View.VISIBLE else swarmWarning.visibility = View.GONE
            if (docs.get("treatment") == true) varoaMites.visibility = View.VISIBLE else varoaMites.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val action =
                HivesListFragmentDirections.actionHivesListFragmentToInspectionsListFragment(docs.id)
            Navigation.findNavController(it).navigate(action)
        }


        holder.itemView.editHIve.setOnClickListener {
         Navigation.findNavController(it).navigate(HivesListFragmentDirections.actionHivesListFragmentToEditHiveFragment(docs.id))
        }

        holder.itemView.addSymbol.setOnClickListener { feeding ->
            hiveClickListener.onAddSignClicked(docs.id)
        }

    }

    //Return datasize of my dataset.
    override fun getItemCount() = items.size

}

class HiveItemDiffCallback(
    var oldHivesList: List<QueryDocumentSnapshot>,
    var newHivesList: ArrayList<QueryDocumentSnapshot>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldHivesList.size

    override fun getNewListSize() = newHivesList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldHivesList[oldItemPosition].id == newHivesList[newItemPosition].id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldHivesList[oldItemPosition] === newHivesList[newItemPosition]
    }
}




