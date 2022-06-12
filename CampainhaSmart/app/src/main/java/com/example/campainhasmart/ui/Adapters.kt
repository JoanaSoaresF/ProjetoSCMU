package com.example.campainhasmart.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.ui.home.OccurrencesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@BindingAdapter("list_occurrences")
fun bindOccurrencesList(recyclerView: RecyclerView, data: List<Occurrence>) {
    Timber.d("List occurrences update")
    val adapter = recyclerView.adapter as OccurrencesAdapter
    CoroutineScope(Dispatchers.IO).launch {
        adapter.submitList(data)
    }

}

@BindingAdapter("occurrence_image")
fun bindOccurrencesImage(imageView: ImageView, occurrence: Occurrence) {
    Glide.with(imageView.context /* context */)
        .load(occurrence.storagePhoto)
        .into(imageView)
}