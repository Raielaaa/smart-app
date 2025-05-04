package com.example.smart.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.apply {
            cvTermsAndPolicy.setOnClickListener {
                EmptyTermsAndConditionFragment().show(parentFragmentManager, "show_terms_and_condition_bottom_dialog")
            }
        }

        return binding.root
    }
}