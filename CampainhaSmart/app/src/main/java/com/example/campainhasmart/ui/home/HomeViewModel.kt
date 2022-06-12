package com.example.campainhasmart.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.model.Repository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    enum class State {
        LOADING, READY, ERROR
    }

    private val repository = Repository.getRepository(application)


    private val _state = MutableLiveData(State.LOADING)
    val state: LiveData<State>
        get() = _state

    val occurrences: LiveData<List<Occurrence>>
        get() = Transformations.map(repository.user) {
            it.allOccurrences
        }


    init {
        viewModelScope.launch {
            repository.loadData()
        }
    }


}