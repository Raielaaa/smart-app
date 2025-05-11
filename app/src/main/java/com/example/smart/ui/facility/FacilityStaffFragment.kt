package com.example.smart.ui.facility

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.smart.R
import com.example.smart.databinding.FragmentFacilityStaffBinding
import com.example.smart.models.FacilityInfoModel
import com.example.smart.utils.Constants
import com.example.smart.utils.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class FacilityStaffFragment(
    private val facilityInfoModel: FacilityInfoModel
) : BottomSheetDialogFragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentFacilityStaffBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityStaffBinding.inflate(inflater, container, false)

        binding.apply {
            Glide.with(requireContext())
                .load(facilityInfoModel.issueImageUri)
                .into(imageView19)

            tvIssueTitleStaff.text = facilityInfoModel.issueName
            tvDateSubmittedStaff.text = facilityInfoModel.dateSubmitted
            tvDescStaff.text = facilityInfoModel.issueDescription

            fireStore.collection(Constants.COLLECTION_USER_ACCOUNTS)
                .document(firebaseAuth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val senderName = documentSnapshot.getString("firstNameModel") + " ${documentSnapshot.getString("lastNameModel")}"
                    tvIssueSenderStaff.text = senderName
                }

            tvStatusStaff.text = facilityInfoModel.issueStatus
            tvRoleStaff.text = Helper.userRole.replaceFirstChar { it.uppercaseChar() }
            when (facilityInfoModel.issueStatus) {
                "Pending" -> {
                    cvStatusStaff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pending_bg))
                    tvStatusStaff.setTextColor(ContextCompat.getColor(requireContext(), R.color.pending_main))
                }
                "Completed" -> {
                    cvStatusStaff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_bg))
                    tvStatusStaff.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_main))
                }
                "Ongoing" -> {
                    cvStatusStaff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.student_bg))
                    tvStatusStaff.setTextColor(ContextCompat.getColor(requireContext(), R.color.student_main))
                }
                else -> {
                    cvStatusStaff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.student_bg))
                    tvStatusStaff.setTextColor(ContextCompat.getColor(requireContext(), R.color.student_main))
                }
            }

            cvRoleStaff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.student_bg))
            tvRoleStaff.setTextColor(ContextCompat.getColor(requireContext(), R.color.student_main))
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}