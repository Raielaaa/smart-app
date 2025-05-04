package com.example.smart.ui.facility

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.smart.R
import com.example.smart.databinding.FragmentFacilityInfoBinding
import com.example.smart.models.FacilityCommunityModel
import com.example.smart.models.FacilityInfoModel
import com.example.smart.ui.adapters.FacilityCommunityAdapter
import com.example.smart.ui.adapters.FacilityInfoAdapter
import com.example.smart.utils.Helper

class FacilityInfoFragment : Fragment() {
    private val viewModel: FacilityInfoViewModel by viewModels()
    private lateinit var binding: FragmentFacilityInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityInfoBinding.inflate(inflater, container, false)

        binding.apply {
            val facilityItems = getDummyFacilityInfoList()
            val facilityAdapter = FacilityInfoAdapter {
                if (Helper.userRole.lowercase() != "staff" && Helper.userRole.lowercase() != "officer") {
                    val itemInfoBottomSheet = FacilityInfoBottomSheet()
                    itemInfoBottomSheet.show(parentFragmentManager, "item_info_bottom_sheet")
                } else {
                    FacilityStaffFragment().show(parentFragmentManager, "item_info_staff_bottom_sheet")
                }
            }
            facilityAdapter.setItem(facilityItems)
            rvFacilityInfo.adapter = facilityAdapter

            cvCommunity.setOnClickListener {
                // Set reports text to default black
                tvReports.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))

                // Set reports card background to white
                cvReports.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                // Set community text and card to main color
                tvCommunity.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                cvCommunity.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_color_light))

                val adapter = FacilityCommunityAdapter { }
                adapter.setItem(getDummyCommunityList())
                rvFacilityInfo.adapter = adapter
            }

            cvReports.setOnClickListener {
                // Set reports text and card to main color
                tvReports.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                cvReports.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_color_light))

                // Set community text to black and background to white
                tvCommunity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))
                cvCommunity.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                val adapter = FacilityInfoAdapter {
                    if (Helper.userRole.lowercase() != "staff" && Helper.userRole.lowercase() != "officer") {
                        val itemInfoBottomSheet = FacilityInfoBottomSheet()
                        itemInfoBottomSheet.show(parentFragmentManager, "item_info_bottom_sheet")
                    } else {
                        FacilityStaffFragment().show(parentFragmentManager, "item_info_staff_bottom_sheet")
                    }
                }
                adapter.setItem(getDummyFacilityInfoList())
                rvFacilityInfo.adapter = adapter
            }
        }

        return binding.root
    }

    // Dummy data helpers
    private fun getDummyFacilityInfoList() = arrayListOf(
        FacilityInfoModel("Main Hall", "Large multipurpose hall", "F001", "Leaking Roof", "Water leaking from ceiling near stage", "Pending", "2025-05-01", "U123", ""),
        FacilityInfoModel("Library", "Second floor reading area", "F002", "Broken Lights", "Two lights not working", "In Progress", "2025-04-28", "U456", ""),
        FacilityInfoModel("Gymnasium", "Basketball court facility", "F003", "Cracked Floor", "Floor has a noticeable crack", "Resolved", "2025-04-25", "U789", ""),
        FacilityInfoModel("Computer Lab", "Lab 1 with 30 PCs", "F004", "No Internet", "Network down since morning", "Pending", "2025-04-30", "U321", ""),
        FacilityInfoModel("Cafeteria", "Main canteen area", "F005", "Air Conditioning", "AC not functioning properly", "In Progress", "2025-05-02", "U654", "")
    )

    private fun getDummyCommunityList() = arrayListOf(
        FacilityCommunityModel("F001", "John Doe", "2025-05-01", "Scheduled maintenance will occur tomorrow.", "Facility Manager"),
        FacilityCommunityModel("F002", "Jane Smith", "2025-04-30", "Power outage reported, currently under investigation.", "Resident"),
        FacilityCommunityModel("F003", "Carlos Reyes", "2025-04-29", "New equipment will be installed next week.", "Technician"),
        FacilityCommunityModel("F004", "Maria Garcia", "2025-04-28", "Facility closed for cleaning this weekend.", "Facility Manager"),
        FacilityCommunityModel("F005", "Liam Wong", "2025-04-27", "Please report any issues with the water dispenser.", "Resident")
    )
}