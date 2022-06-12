package com.example.campainhasmart.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.campainhasmart.databinding.OccurrenceItemBinding
import com.example.campainhasmart.model.Occurrence

class OccurrencesAdapter(onClickListener: OnOccurrenceClicked) : ListAdapter<Occurrence,
        OccurrencesAdapter.ViewHolder>(
    OccurrenceDiffCallback()
) {
    class ViewHolder private constructor(val binding: OccurrenceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Occurrence) {
            binding.occurrence = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = OccurrenceItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * Custom listener to handle clicks ocn [RecyclerView] items. Passes the
     * [Occurrence] associated with the item to tje [onClick] function
     */

    class OnOccurrenceClicked(val clickListener : (occurrence : Occurrence) -> Unit) {
        fun onClick(occurrence: Occurrence) = clickListener(occurrence)
    }

}


/**
 * Callback for calculating the differences between non-null items on the list.
 * Used by the List-Adapter to calculate the minimum number of changes between and old
 * list and a new list that's passed by submitList
 */
class OccurrenceDiffCallback : DiffUtil.ItemCallback<Occurrence>() {
    override fun areItemsTheSame(oldItem: Occurrence, newItem: Occurrence): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Occurrence, newItem: Occurrence): Boolean {
        return oldItem == newItem
    }

}