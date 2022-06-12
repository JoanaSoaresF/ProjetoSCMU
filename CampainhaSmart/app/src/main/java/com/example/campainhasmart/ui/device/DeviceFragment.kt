package com.example.campainhasmart.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.campainhasmart.databinding.FragmentDeviceBinding

class DeviceFragment : Fragment() {


    private lateinit var viewModel: DeviceViewModel

    private lateinit var binding: FragmentDeviceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val device by navArgs<DeviceFragmentArgs>()

        val viewModelFactory = DeviceViewModelFactory(
            requireActivity().application,
            device.device
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[DeviceViewModel::class.java]

        binding.device = viewModel.device

        val ledButton = binding.ledButton
        ledButton.setOnCheckedChangeListener { compoundButton, b ->
            val s = viewModel.ledButtonPressed(b)
            if (!s) {
                ledButton.isChecked = !b

            }


        }
        binding.openDoorButton.setOnClickListener {
            viewModel.openDoorButtonPressed()

        }
        val messageText = binding.messageText
        binding.sendMessageButton.setOnClickListener {
            val message = messageText.text.toString()
            val s = viewModel.sendMessageButton(message)
            if (!s) {
                messageText.setText(viewModel.device.messageOnDisplay)
            }

        }

        viewModel.warning.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireActivity().application,
                    "Não há conexão à internet, tente mais tarde. ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return binding.root


    }

}