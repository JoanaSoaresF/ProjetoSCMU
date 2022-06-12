package com.example.campainhasmart.ui.devices_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.campainhasmart.databinding.ItemDeviceBinding

import com.example.campainhasmart.model.Device

class DevicesAdapter(val onClickListener: OnDeviceClicked) :
    ListAdapter<Device,
            DevicesAdapter.ViewHolder>(DeviceDiffCallback()) {
    class ViewHolder private constructor(val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Device, onDeviceClicked: OnDeviceClicked) {
            binding.device = item
            binding.clickListener = onDeviceClicked

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDeviceBinding.inflate(layoutInflater, parent, false)
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
        holder.bind(item, onClickListener)
    }

    /**
     * Custom listener to handle clicks ocn [RecyclerView] items. Passes the
     * [Device] associated with the item to tje [onClick] function
     */

    class OnDeviceClicked(val clickListener: (device: Device) -> Unit) {
        fun onClick(device: Device) = clickListener(device)
    }

}


/**
 * Callback for calculating the differences between non-null items on the list.
 * Used by the List-Adapter to calculate the minimum number of changes between and old
 * list and a new list that's passed by submitList
 */
class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }

}