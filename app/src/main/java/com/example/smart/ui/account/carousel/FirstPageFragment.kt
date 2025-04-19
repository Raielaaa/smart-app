package com.example.smart.ui.account.carousel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentFirstPageBinding

class FirstPageFragment : Fragment() {
    private lateinit var binding: FragmentFirstPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstPageBinding.inflate(inflater, container, false)

        binding.apply {
            cvNext.setOnClickListener {
                findNavController().navigate(R.id.action_firstPageFragment_to_secondPageFragment)
            }
            tvSkip.setOnClickListener {
                findNavController().navigate(R.id.action_firstPageFragment_to_homeAccountFragment)
            }
        }

        return binding.root
    }
}