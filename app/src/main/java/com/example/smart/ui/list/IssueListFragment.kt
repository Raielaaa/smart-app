package com.example.smart.ui.list

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentIssueListBinding
import com.example.smart.models.FacilityCommunityModel
import com.example.smart.models.FacilityInfoModel
import com.example.smart.ui.adapters.FacilityInfoAdapter
import com.example.smart.utils.Constants
import com.example.smart.utils.LoadingDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Named

class IssueListFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private var retrievedItemsInfo: ArrayList<FacilityInfoModel> = arrayListOf()
    private var loadingDialog: LoadingDialogFragment? = null
    private val viewModel: IssueListViewModel by viewModels()
    private lateinit var binding: FragmentIssueListBinding
    private var facilityInfo: FacilityInfoAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssueListBinding.inflate(inflater, container, false)
        retrieveDataFromDB()

        binding.apply {
            val adapter = FacilityInfoAdapter() {

            }
            adapter.setItem(retrievedItemsInfo)
            rvListItems.adapter = adapter
        }

        return binding.root
    }

    private fun retrieveDataFromDB() {
        try {
            //  show loading dialog
            loadingDialog = LoadingDialogFragment("Loading", "Please wait while we retrieve all the necessary information.")
            loadingDialog?.show(this.parentFragmentManager, "loading_dialog")

            fireStore.collection(Constants.COLLECTION_REPORTS)
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
                    loadingDialog?.dismiss()
                }
        } catch (exception: Exception) {
            Log.w("tag", "An error occurred: ${exception.message}")
            loadingDialog?.dismiss()
        }
    }
}