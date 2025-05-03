package com.example.smart.ui.report_issue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentShowInstructionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GuideBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentShowInstructionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowInstructionsBinding.inflate(inflater, container, false)

        binding.apply {
            cvGuideProceed.setOnClickListener {
                this@GuideBottomSheet.dismiss()
                findNavController().navigate(R.id.issueInfoFragment)
            }
        }

        return binding.root
    }
}
