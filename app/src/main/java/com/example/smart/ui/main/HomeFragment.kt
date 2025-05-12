package com.example.smart.ui.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.smart.R
import com.example.smart.databinding.FragmentHomeBinding
import com.example.smart.models.FacilityHomeModel
import com.example.smart.ui.adapters.FacilityHomeAdapter
import com.example.smart.utils.Constants
import com.example.smart.utils.Helper
import com.example.smart.utils.LoadingDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    private var loadingDialog: LoadingDialogFragment? = null
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initIssueCount(binding)
        initBottomRv(binding)
        initHeader(binding)
        binding.apply {
            textView29.text = Helper.userRole.replaceFirstChar { it.uppercaseChar() }
        }

        return binding.root
    }

    private fun initHeader(binding: FragmentHomeBinding) {
        fireStore.collection(Constants.COLLECTION_USER_ACCOUNTS)
            .document(firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                val fullName = "${result.getString("firstNameModel")} ${result.getString("lastNameModel")}"
                val emailAddress = result.getString("emailModel")
                val issuesSent = result.getString("issuesSent")

                binding.apply {
                    tvHeaderNameHome.text = "Hello, ${fullName.split(" ")[0]}"
                    tvHeaderEmailHome.text = emailAddress
                }
            }.addOnFailureListener { exception ->
                Log.w("tag", "An error occurred: ${exception.message}")
            }
    }

    private fun initIssueCount(binding: FragmentHomeBinding) {
        fireStore.collection(Constants.COLLECTION_REPORTS)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var issueCount: Int = 0
                for (document in querySnapshot) {
                    if (document.getString("issueStatus") != "Completed") issueCount++
                }

                Helper.issueCount = issueCount.toString()
                binding.textView30.text = "$issueCount facility issues detected. Please review them in the Issues tab."
            }
    }

    private fun initBottomRv(binding: FragmentHomeBinding) {
        var selectedRoomNumber: String = ""

        //  show loading dialog
        loadingDialog = LoadingDialogFragment("Loading", "Please wait while we retrieve all the necessary information.")
        loadingDialog?.show(this.parentFragmentManager, "loading_dialog")

        val roomList = arrayListOf<FacilityHomeModel>()
        val adapter = FacilityHomeAdapter { selectedFacility ->
            val bundle = Bundle().apply {
                putString("roomNumber", selectedFacility.roomNumber)
            }
            findNavController().navigate(R.id.facilityInfoFragment, bundle)
        }

        // Set up RecyclerView first (empty for now)
        binding.rvFacilities.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFacilities.adapter = adapter

        try {
            fireStore.collection(Constants.COLLECTION_FACILITIES)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val roomNumber = document.getString("roomNumber") ?: ""
                        val reportCount = document.getString("reportCount") ?: ""

                        val facility = FacilityHomeModel(roomNumber, reportCount)
                        roomList.add(facility)
                    }

                    // Set items to adapter
                    adapter.setItem(roomList)

                    // Dismiss loading dialog
                    loadingDialog?.dismiss()
                }
        } catch (err: Exception) {
            Toast.makeText(requireContext(), "Unexpected error: ${err.message}", Toast.LENGTH_LONG).show()
            loadingDialog?.dismiss()
        }
    }
}