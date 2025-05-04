package com.example.smart.ui.facility

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentFacilityStaffBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FacilityStaffFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentFacilityStaffBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityStaffBinding.inflate(inflater, container, false)

        binding.apply {

        }

        // Inflate the layout for this fragment
        return binding.root
    }
}