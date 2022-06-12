package com.example.campainhasmart.ui.devices_list


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.Repository
import timber.log.Timber

class DevicesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getRepository(application)

    val devices: List<Device>
            get() = repository.user.value!!.devices


    private val _navigateToDevice: MutableLiveData<Device?> = MutableLiveData(null)
    val navigateToDevice: LiveData<Device?>
        get() = _navigateToDevice

    fun navigateToDevice(device: Device) {
        _navigateToDevice.value = device
    }

    fun navigationDone() {
        _navigateToDevice.value = null
    }

    init {
        Timber.d(
            "Init view model devices from user ${
                repository.user.value?.devices
                    ?.size
            }"
        )
    }
}