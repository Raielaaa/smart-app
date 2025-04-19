package com.example.smart.ui.account.carousel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentFifthPageBinding

class FifthPageFragment : Fragment() {
    private lateinit var binding: FragmentFifthPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFifthPageBinding.inflate(inflater, container, false)

        binding.apply {
            cvNext.setOnClickListener {
                findNavController().navigate(R.id.action_fifthPageFragment_to_sixthPageFragment)
            }
        }

        return binding.root
    }
}