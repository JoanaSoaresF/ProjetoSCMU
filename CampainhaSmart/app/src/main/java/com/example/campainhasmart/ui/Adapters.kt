package com.example.campainhasmart.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campainhasmart.R
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.ui.devices_list.DevicesAdapter
import com.example.campainhasmart.ui.home.OccurrencesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@BindingAdapter("list_occurrences")
fun bindOccurrencesList(recyclerView: RecyclerView, data: List<Occurrence>) {
    Timber.d("List occurrences update size: ${data.size}")
    val adapter = recyclerView.adapter as OccurrencesAdapter
    CoroutineScope(Dispatchers.IO).launch {
        adapter.submitList(data)
    }
}

@BindingAdapter("occurrence_image")
fun bindOccurrencesImage(imageView: ImageView, occurrence: Occurrence) {
    Glide.with(imageView.context /* context */)
        .load(occurrence.storagePhoto)
        .placeholder(R.drawable.house)
        .into(imageView)
}

@BindingAdapter("device_image")
fun bindDeviceImage(imageView: ImageView, device: Device) {
    Timber.d("Binding image device: ${device.id} and ${device.storageReference.toString()}")
    Glide.with(imageView.context /* context */)
        .load(device.storageReference)
        .placeholder(R.drawable.house)
        .into(imageView)
}


@BindingAdapter("list_devices")
fun bindDevicesList(recyclerView: RecyclerView, data: List<Device>) {
    Timber.d("List devices update size: ${data.size}")
    val adapter = recyclerView.adapter as DevicesAdapter
    CoroutineScope(Dispatchers.IO).launch {
        adapter.submitList(data)
    }
}
