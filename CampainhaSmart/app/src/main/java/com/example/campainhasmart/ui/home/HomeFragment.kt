package com.example.campainhasmart.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.campainhasmart.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding: FragmentHomeBinding
        get() = _binding!!

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

//        Repository.getRepository(requireContext()).populateWithMockupData()

        val adapter = OccurrencesAdapter(OccurrencesAdapter.OnOccurrenceClicked {
            viewModel.occurrenceClicked(it)
        })

        binding.occurrencesList.adapter = adapter

        viewModel.navigateToOccurrence.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    HomeFragmentDirections
                        .actionNavigationHomeToOccurrencesFragment(it.id)
                )
                viewModel.navigationDone()
            }
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}