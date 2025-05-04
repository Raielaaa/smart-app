package com.example.smart.ui.list

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentIssueListBinding
import com.example.smart.models.FacilityInfoModel
import com.example.smart.ui.adapters.FacilityInfoAdapter

class IssueListFragment : Fragment() {
    private val viewModel: IssueListViewModel by viewModels()
    private lateinit var binding: FragmentIssueListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssueListBinding.inflate(inflater, container, false)

        binding.apply {
            val adapter = FacilityInfoAdapter() {

            }
            adapter.setItem(getDummyFacilityInfoList())
            rvListItems.adapter = adapter
        }

        return binding.root
    }

    private fun getDummyFacilityInfoList() = arrayListOf(
        FacilityInfoModel("Main Hall", "Large multipurpose hall", "F001", "Leaking Roof", "Water leaking from ceiling near stage", "Pending", "2025-05-01", "U123", ""),
        FacilityInfoModel("Library", "Second floor reading area", "F002", "Broken Lights", "Two lights not working", "In Progress", "2025-04-28", "U456", ""),
        FacilityInfoModel("Gymnasium", "Basketball court facility", "F003", "Cracked Floor", "Floor has a noticeable crack", "Resolved", "2025-04-25", "U789", ""),
        FacilityInfoModel("Computer Lab", "Lab 1 with 30 PCs", "F004", "No Internet", "Network down since morning", "Pending", "2025-04-30", "U321", ""),
        FacilityInfoModel("Cafeteria", "Main canteen area", "F005", "Air Conditioning", "AC not functioning properly", "In Progress", "2025-05-02", "U654", "")
    )
}