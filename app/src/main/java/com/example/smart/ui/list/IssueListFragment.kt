package com.example.smart.ui.list

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
import com.example.smart.databinding.FragmentHomeBinding
import com.example.smart.databinding.FragmentIssueListBinding
import com.example.smart.models.FacilityCommunityModel
import com.example.smart.models.FacilityInfoModel
import com.example.smart.ui.adapters.FacilityInfoAdapter
import com.example.smart.ui.facility.FacilityInfoBottomSheet
import com.example.smart.ui.facility.FacilityStaffFragment
import com.example.smart.utils.Constants
import com.example.smart.utils.Helper
import com.example.smart.utils.LoadingDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class IssueListFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    private var retrievedItemsInfo: ArrayList<FacilityInfoModel> = arrayListOf()
    private var loadingDialog: LoadingDialogFragment? = null
    private val viewModel: IssueListViewModel by viewModels()
    private lateinit var binding: FragmentIssueListBinding
    private var facilityInfo: FacilityInfoAdapter? = null
    private var selectedStatus: String = "pending"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssueListBinding.inflate(inflater, container, false)
        retrieveDataFromDB()
        binding.textView30.text = "${Helper.issueCount} facility issues detected. Please review them in the Issues tab."
        binding.textView29.text = Helper.userRole.replaceFirstChar { it.uppercaseChar() }
        initHeader(binding)

        facilityInfo = FacilityInfoAdapter { item ->
            loadingDialog = LoadingDialogFragment("Loading", "Please wait while we retrieve all the necessary information.")
            loadingDialog?.show(parentFragmentManager, "loading_dialog")

            fireStore.collection(Constants.COLLECTION_REPORTS)
                .whereEqualTo("issueName", item.issueName)
                .whereEqualTo("dateSubmitted", item.dateSubmitted)
                .whereEqualTo("issueSubmitterID", item.issueSubmitterID)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val document = querySnapshot.documents.firstOrNull()
                    val retrievedData = document?.let {
                        FacilityInfoModel(
                            roomNumber = it.getString("roomNumber") ?: "",
                            issueName = it.getString("issueName") ?: "",
                            issueStatus = it.getString("issueStatus") ?: "",
                            issueDescription = it.getString("issueDescription") ?: "",
                            issueSubmitterID = it.getString("issueSubmitterID") ?: "",
                            issueImageUri = it.getString("issueImageUri") ?: "",
                            dateSubmitted = it.getString("dateSubmitted") ?: ""
                        )
                    }

                    retrievedData?.let { data ->
                        if (Helper.userRole.lowercase() != "staff" && Helper.userRole.lowercase() != "officer") {
                            FacilityInfoBottomSheet(data).show(parentFragmentManager, "item_info_bottom_sheet")
                        } else {
                            FacilityStaffFragment(data).show(parentFragmentManager, "item_info_staff_bottom_sheet")
                        }
                    }

                    loadingDialog?.dismiss()
                }
        }

        binding.rvListItems.adapter = facilityInfo
        setupClickListeners()

        return binding.root
    }

    private fun initHeader(binding: FragmentIssueListBinding) {
        fireStore.collection(Constants.COLLECTION_USER_ACCOUNTS)
            .document(firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                val fullName = "${result.getString("firstNameModel")} ${result.getString("lastNameModel")}"
                val emailAddress = result.getString("emailModel")
                val issuesSent = result.getString("issuesSent")

                binding.apply {
                    tvHeaderNameList.text = "Hello, ${fullName.split(" ")[0]}"
                    tvHeaderEmailList.text = emailAddress
                }
            }.addOnFailureListener { exception ->
                Log.w("tag", "An error occurred: ${exception.message}")
            }
    }

    private fun retrieveDataFromDB() {
        try {
            loadingDialog = LoadingDialogFragment("Loading", "Please wait...")
            loadingDialog?.show(parentFragmentManager, "loading_dialog")

            fireStore.collection(Constants.COLLECTION_REPORTS)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    retrievedItemsInfo.clear()
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
                    filterListByStatus()
                    loadingDialog?.dismiss()
                }
        } catch (e: Exception) {
            Log.e("tag", e.message ?: "Unknown error")
            loadingDialog?.dismiss()
        }
    }

    private fun filterListByStatus() {
        val filteredList = retrievedItemsInfo.filter { it.issueStatus.equals(selectedStatus, ignoreCase = true) }
        facilityInfo?.setItem(ArrayList(filteredList))
    }

    private fun setupClickListeners() {
        binding.apply {
            cvPending.setOnClickListener {
                selectedStatus = "pending"
                updateCardStyles("pending")
                filterListByStatus()
            }

            cvOngoing.setOnClickListener {
                selectedStatus = "ongoing"
                updateCardStyles("ongoing")
                filterListByStatus()
            }

            cvCompleted.setOnClickListener {
                selectedStatus = "completed"
                updateCardStyles("completed")
                filterListByStatus()
            }
        }
    }

    private fun updateCardStyles(status: String) {
        binding.apply {
            val highlight = ContextCompat.getColor(requireContext(), R.color.main_color)
            val highlightBg = ContextCompat.getColor(requireContext(), R.color.main_color_light)
            val normal = ContextCompat.getColor(requireContext(), R.color.black_light)
            val normalBg = ContextCompat.getColor(requireContext(), R.color.white)

            tvPending.setTextColor(if (status == "pending") highlight else normal)
            cvPending.setCardBackgroundColor(if (status == "pending") highlightBg else normalBg)

            tvOngoing.setTextColor(if (status == "ongoing") highlight else normal)
            cvOngoing.setCardBackgroundColor(if (status == "ongoing") highlightBg else normalBg)

            tvCompleted.setTextColor(if (status == "completed") highlight else normal)
            cvCompleted.setCardBackgroundColor(if (status == "completed") highlightBg else normalBg)
        }
    }
}
