package com.example.campainhasmart.ui.device

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.Repository
import timber.log.Timber


class DeviceViewModel(val deviceId: String, application: Application) : AndroidViewModel
    (application) {

    private val _warning: MutableLiveData<Boolean> = MutableLiveData(false)
    val warning: LiveData<Boolean>
        get() = _warning


    private val repository = Repository.getRepository(application)
    val device: Device
        get() = repository.user.value!!.devices.find { it.id == deviceId }!!


    fun ledButtonPressed(b: Boolean): Boolean {
        val success = repository.setLedValue(device, b)
        _warning.value = !success
        return success
    }

    fun openDoorButtonPressed(): Boolean {
        val success = repository.setOpenDoor(device)
        _warning.value = !success
        return success
    }

    fun sendMessageButton(message: String): Boolean {
        val success = repository.setMessage(device, message)
        _warning.value = !success
        return success


    }

//    init {
//        Timber.d("Device view model with id $device")
//        Timber.d("Device View model user state ${repository.user.value}")
//    }


}