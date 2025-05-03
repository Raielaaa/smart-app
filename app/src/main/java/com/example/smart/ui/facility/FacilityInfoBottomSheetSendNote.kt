package com.example.smart.ui.facility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentFacilityInfoBottomSheetAddNoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FacilityInfoBottomSheetSendNote : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentFacilityInfoBottomSheetAddNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityInfoBottomSheetAddNoteBinding.inflate(inflater, container, false)



        return binding.root
    }
}
