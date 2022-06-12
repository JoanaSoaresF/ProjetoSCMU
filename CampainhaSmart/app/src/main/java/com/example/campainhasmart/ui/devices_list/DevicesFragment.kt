package com.example.campainhasmart.ui.devices_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.campainhasmart.databinding.FragmentDevicesListBinding

class DevicesFragment : Fragment() {

    private var _binding: FragmentDevicesListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: DevicesViewModel by lazy {
        ViewModelProvider(this)[DevicesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDevicesListBinding.inflate(inflater, container, false)

        val adapter = DevicesAdapter(DevicesAdapter.OnDeviceClicked {
            viewModel.navigateToDevice(it)
        })
        binding.devicesList.adapter = adapter
        binding.viewModel = viewModel

        viewModel.navigateToDevice.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    DevicesFragmentDirections.navigateToDevice(it.id)
                )
                viewModel.navigationDone()

            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}