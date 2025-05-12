package com.example.smart.ui.facility

import android.net.Uri
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
import com.example.smart.models.FacilityHomeModel
import com.example.smart.models.FacilityInfoModel
import com.example.smart.ui.adapters.FacilityCommunityAdapter
import com.example.smart.ui.adapters.FacilityInfoAdapter
import com.example.smart.utils.Constants
import com.example.smart.utils.Helper
import com.example.smart.utils.LoadingDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class FacilityInfoFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private var loadingDialog: LoadingDialogFragment? = null
    private val viewModel: FacilityInfoViewModel by viewModels()
    private lateinit var binding: FragmentFacilityInfoBinding
    private var roomNumber = ""
    private var facilityInfo: FacilityInfoAdapter? = null
    private var facilityCommunity: FacilityCommunityAdapter? = null
    private var retrievedItemsInfo: ArrayList<FacilityInfoModel> = arrayListOf()
    private var retrievedItemsCommunity: ArrayList<FacilityCommunityModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityInfoBinding.inflate(inflater, container, false)

        roomNumber = arguments?.getString("roomNumber").toString()
        facilityCommunity = FacilityCommunityAdapter { }
        retrieveDataFromDB()

        binding.apply {
            textView35.text = "Room number: $roomNumber"
            facilityInfo = FacilityInfoAdapter {
                loadingDialog = LoadingDialogFragment("Loading", "Please wait while we retrieve all the necessary information.")
                loadingDialog?.show(parentFragmentManager, "loading_dialog")

                fireStore.collection(Constants.COLLECTION_REPORTS)
                    .whereEqualTo("issueName", it.issueName)
                    .whereEqualTo("dateSubmitted", it.dateSubmitted)
                    .whereEqualTo("issueSubmitterID", it.issueSubmitterID)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        var retrievedData: FacilityInfoModel? = null
                        for (document in querySnapshot) {
                            retrievedData = FacilityInfoModel(
                                roomNumber = document.getString("roomNumber") ?: "",
                                issueName = document.getString("issueName") ?: "",
                                issueStatus = document.getString("issueStatus") ?: "",
                                issueDescription = document.getString("issueDescription") ?: "",
                                issueSubmitterID = document.getString("issueSubmitterID") ?: "",
                                issueImageUri = document.getString("issueImageUri") ?: "",
                                dateSubmitted = document.getString("dateSubmitted") ?: ""
                            )
                        }

                        if (Helper.userRole.lowercase() != "staff" && Helper.userRole.lowercase() != "officer") {
                            val itemInfoBottomSheet = FacilityInfoBottomSheet(retrievedData!!)
                            itemInfoBottomSheet.show(parentFragmentManager, "item_info_bottom_sheet")
                            loadingDialog?.dismiss()
                        } else {
                            FacilityStaffFragment(retrievedData!!).show(parentFragmentManager, "item_info_staff_bottom_sheet")
                            loadingDialog?.dismiss()
                        }
                    }
            }
            facilityInfo!!.setItem(retrievedItemsInfo)
            rvFacilityInfo.adapter = facilityInfo

            cvCommunity.setOnClickListener {
                // Set reports text to default black
                tvReports.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))

                // Set reports card background to white
                cvReports.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                // Set community text and card to main color
                tvCommunity.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                cvCommunity.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_color_light))

                facilityCommunity!!.setItem(retrievedItemsCommunity)
                rvFacilityInfo.adapter = facilityCommunity
            }

            cvReports.setOnClickListener {
                // Set reports text and card to main color
                tvReports.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                cvReports.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_color_light))

                // Set community text to black and background to white
                tvCommunity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))
                cvCommunity.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                facilityInfo = FacilityInfoAdapter {
                    loadingDialog = LoadingDialogFragment("Loading", "Please wait while we retrieve all the necessary information.")
                    loadingDialog?.show(parentFragmentManager, "loading_dialog")

                    fireStore.collection(Constants.COLLECTION_REPORTS)
                        .whereEqualTo("issueName", it.issueName)
                        .whereEqualTo("dateSubmitted", it.dateSubmitted)
                        .whereEqualTo("issueSubmitterID", it.issueSubmitterID)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            var retrievedData: FacilityInfoModel? = null
                            for (document in querySnapshot) {
                                retrievedData = FacilityInfoModel(
                                    roomNumber = document.getString("roomNumber") ?: "",
                                    issueName = document.getString("issueName") ?: "",
                                    issueStatus = document.getString("issueStatus") ?: "",
                                    issueDescription = document.getString("issueDescription") ?: "",
                                    issueSubmitterID = document.getString("issueSubmitterID") ?: "",
                                    issueImageUri = document.getString("issueImageUri") ?: "",
                                    dateSubmitted = document.getString("dateSubmitted") ?: ""
                                )
                            }

                            if (Helper.userRole.lowercase() != "staff" && Helper.userRole.lowercase() != "officer") {
                                val itemInfoBottomSheet = FacilityInfoBottomSheet(retrievedData!!)
                                itemInfoBottomSheet.show(parentFragmentManager, "item_info_bottom_sheet")
                                loadingDialog?.dismiss()
                            } else {
                                FacilityStaffFragment(retrievedData!!).show(parentFragmentManager, "item_info_staff_bottom_sheet")
                                loadingDialog?.dismiss()
                            }
                        }
                }
                facilityInfo!!.setItem(retrievedItemsInfo)
                rvFacilityInfo.adapter = facilityInfo
            }
        }

        return binding.root
    }

    private fun retrieveDataFromDB() {
        try {
            //  show loading dialog
            loadingDialog = LoadingDialogFragment("Loading", "Please wait while we retrieve all the necessary information.")
            loadingDialog?.show(this.parentFragmentManager, "loading_dialog")

            fireStore.collection(Constants.COLLECTION_REPORTS)
                .whereEqualTo("roomNumber", roomNumber)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        retrievedItemsInfo.add(
                            FacilityInfoModel(
                                roomNumber = document.get("roomNumber").toString(),
                                issueName = document.get("issueName").toString(),
                                issueStatus = document.get("issueStatus").toString(),
                                issueDescription = document.get("issueDescription").toString(),
                                issueSubmitterID = document.get("issueSubmitterID").toString(),
                                issueImageUri = document.getString("issueImageUri") ?: "",
                                dateSubmitted = document.get("dateSubmitted").toString()
                            )
                        )
                    }
                    facilityInfo!!.setItem(retrievedItemsInfo)

                    fireStore.collection(Constants.COLLECTION_COMMUNITY)
                        .whereEqualTo("roomNumber", roomNumber)
                        .get()
                        .addOnSuccessListener { querySnapshotCommunity ->
                            for (documentCommunity in querySnapshotCommunity) {
                                retrievedItemsCommunity.add(
                                    FacilityCommunityModel(
                                        issueTitle = documentCommunity.get("issueTitle").toString(),
                                        roomNumber = documentCommunity.get("roomNumber").toString(),
                                        communitySender = documentCommunity.get("communitySender").toString(),
                                        communityDate = documentCommunity.get("communityDate").toString(),
                                        communityContent = documentCommunity.get("communityContent").toString(),
                                        communitySenderRole = documentCommunity.get("communitySenderRole").toString()
                                    )
                                )
                            }
                            facilityCommunity!!.setItem(retrievedItemsCommunity)
                            loadingDialog?.dismiss()
                        }.addOnFailureListener { exception ->
                            Log.w("tag", "An error occurred - community: ${exception.message}")
                        }
                }
        } catch (exception: Exception) {
            Log.w("tag", "An error occurred: ${exception.message}")
            loadingDialog?.dismiss()
        }
    }
}