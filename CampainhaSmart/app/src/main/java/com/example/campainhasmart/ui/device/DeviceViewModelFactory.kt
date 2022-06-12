package com.example.campainhasmart.ui.device

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DeviceViewModelFactory(private val app: Application, private val deviceId : String)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DeviceViewModel::class.java)){
            return DeviceViewModel(deviceId, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}