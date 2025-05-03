package com.example.smart.ui.facility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentFacilityInfoBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FacilityInfoBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentFacilityInfoBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityInfoBottomSheetBinding.inflate(inflater, container, false)

        binding.apply {
            cvAddNote.setOnClickListener {
                val itemInfoBottomSheet = FacilityInfoBottomSheetSendNote()
                this@FacilityInfoBottomSheet.dismiss()

                itemInfoBottomSheet.show(parentFragmentManager, "item_info_bottom_sheet_send_note")
            }
        }

        return binding.root
    }
}
