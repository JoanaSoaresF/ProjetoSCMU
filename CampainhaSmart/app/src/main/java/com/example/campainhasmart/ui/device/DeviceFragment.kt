package com.example.campainhasmart.ui.device

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.campainhasmart.R
import com.example.campainhasmart.ui.home.HomeViewModel

class DeviceFragment : Fragment() {


    private lateinit var viewModel: DeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        viewModel =  ViewModelProvider(this)[DeviceViewModel::class.java]
        return inflater.inflate(R.layout.fragment_device, container, false)
    }

}