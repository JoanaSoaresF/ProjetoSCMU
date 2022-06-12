package com.example.campainhasmart.ui.occurrence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.campainhasmart.databinding.FragmentOccurrenceBinding
import com.example.campainhasmart.model.Repository

class OccurrenceFragment : Fragment() {

    private var _binding: FragmentOccurrenceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentOccurrenceBinding.inflate(inflater, container, false)

        val occurrenceToShow by navArgs<OccurrenceFragmentArgs>()

        val repository = Repository.getRepository(requireContext())
        val occurrence =
            repository.user.value!!.allOccurrences[occurrenceToShow.occurence]
        binding.occurrence = occurrence


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}