package com.example.campainhasmart.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.campainhasmart.databinding.FragmentHomeBinding
import com.example.campainhasmart.model.Device
import com.example.campainhasmart.model.FirebaseOccurrence
import com.example.campainhasmart.model.Occurrence
import com.example.campainhasmart.model.database.DatabaseOccurrence
import com.example.campainhasmart.util.RandomUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        database = Firebase.database.reference
        val occurrences =  database.child("occurrences")



        Log.d("TAG", "inicio")
        for (i in 1..2) {
            // Get a random Restaurant POJO

            val o = RandomUtils.testOccurrences[i]
            Log.d("TAG", "occurrence : $o")

            // Add a new document to the restaurants collection
            occurrences.child(o.id!!).setValue(o).addOnFailureListener {
                Log.d("TAG", "erro")
                it.printStackTrace()
            }.addOnSuccessListener {
                Log.d("TAG", "sucesso")
            }
        }
        Log.d("TAG", "fim")
        val occus = occurrences.get().addOnSuccessListener {
            val typeIndicator = object :
                GenericTypeIndicator<Map<String, FirebaseOccurrence>>() {}
            val t = it.getValue(typeIndicator)
            if (t != null) {
                Log.i("TAG", "Got value ${t.size}")
                Log.i("TAG", "Got value ${t["id3"]}")
            }
        }.addOnFailureListener{
            Log.e("TAG", "Error getting data", it)
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}