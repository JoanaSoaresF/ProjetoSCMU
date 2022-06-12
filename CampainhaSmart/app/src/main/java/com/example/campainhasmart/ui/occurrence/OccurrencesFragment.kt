package com.example.campainhasmart.ui.occurrence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.campainhasmart.databinding.FragmentOccurrencesBinding
import com.example.campainhasmart.ui.home.HomeViewModel

class OccurrencesFragment : Fragment() {

    private var _binding: FragmentOccurrencesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: OccurrenceViewModel by lazy {
        ViewModelProvider(this)[OccurrenceViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentOccurrencesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}