package com.example.campainhasmart.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.model.Repository
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(application: Application) : AndroidViewModel(application) {


    private val repository = Repository.getRepository(application)


    private val _navigateToOccurrence = MutableLiveData<Occurrence?>()
    val navigateToOccurrence: LiveData<Occurrence?>
        get() = _navigateToOccurrence

    val occurrences: LiveData<List<Occurrence>>
        get() = Transformations.map(repository.user) {
            it.orderedOccurrences
        }

    fun occurrenceClicked(occurrence: Occurrence) {
        _navigateToOccurrence.value = occurrence
    }

    fun navigationDone() {
        _navigateToOccurrence.value = null
    }


}