package com.example.smart.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentEmptyTermsAndConditionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmptyTermsAndConditionFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentEmptyTermsAndConditionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmptyTermsAndConditionBinding.inflate(inflater, container, false)

        binding.apply {

        }

        return binding.root
    }
}