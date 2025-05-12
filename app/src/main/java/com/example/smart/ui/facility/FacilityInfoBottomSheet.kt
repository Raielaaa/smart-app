package com.example.smart.ui.facility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.smart.R
import com.example.smart.databinding.FragmentFacilityInfoBottomSheetBinding
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
class FacilityInfoBottomSheet(
    private val facilityInfoModel: FacilityInfoModel
) : BottomSheetDialogFragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentFacilityInfoBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityInfoBottomSheetBinding.inflate(inflater, container, false)

        binding.apply {
            Glide.with(requireContext())
                .load(facilityInfoModel.issueImageUri)
                .into(imageView19)

            tvIssueTitle.text = facilityInfoModel.issueName
            tvDateSubmittedBottom.text = facilityInfoModel.dateSubmitted
            tvDescBottom.text = facilityInfoModel.issueDescription

            fireStore.collection(Constants.COLLECTION_USER_ACCOUNTS)
                .document(firebaseAuth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val senderName = documentSnapshot.getString("firstNameModel") + " ${documentSnapshot.getString("lastNameModel")}"
                    tvIssueSender.text = senderName
                }

            tvStatusBottom.text = facilityInfoModel.issueStatus
            tvRoleBottom.text = Helper.userRole.replaceFirstChar { it.uppercaseChar() }
            when (facilityInfoModel.issueStatus) {
                "Pending" -> {
                    cvStatusBottom.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pending_bg))
                    tvStatusBottom.setTextColor(ContextCompat.getColor(requireContext(), R.color.pending_main))
                }
                "Completed" -> {
                    cvStatusBottom.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_bg))
                    tvStatusBottom.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_main))
                }
                "Ongoing" -> {
                    cvStatusBottom.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.student_bg))
                    tvStatusBottom.setTextColor(ContextCompat.getColor(requireContext(), R.color.student_main))
                }
                else -> {
                    cvStatusBottom.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.student_bg))
                    tvStatusBottom.setTextColor(ContextCompat.getColor(requireContext(), R.color.student_main))
                }
            }

            cvRoleBottom.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.student_bg))
            tvRoleBottom.setTextColor(ContextCompat.getColor(requireContext(), R.color.student_main))

            cvAddNote.setOnClickListener {
                val itemInfoBottomSheet = FacilityInfoBottomSheetSendNote(facilityInfoModel)
                this@FacilityInfoBottomSheet.dismiss()

                itemInfoBottomSheet.show(parentFragmentManager, "item_info_bottom_sheet_send_note")
            }
        }

        return binding.root
    }
}
