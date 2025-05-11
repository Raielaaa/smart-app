package com.example.smart.ui.main

import androidx.fragment.app.viewModels
import android.os.Bundle
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
import com.example.smart.utils.LoadingDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private lateinit var binding: FragmentHomeBinding
    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initBottomRv(binding)

        return binding.root
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

                        val facility = FacilityHomeModel(roomNumber)
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