package com.example.smart.ui.facility

import android.app.AlertDialog
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.smart.R
import com.example.smart.databinding.FragmentFacilityStaffBinding
import com.example.smart.models.FacilityInfoModel
import com.example.smart.utils.Constants
import com.example.smart.utils.Helper
import com.example.smart.utils.LoadingDialogFragment
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
    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityStaffBinding.inflate(inflater, container, false)
        var selectedStatus: String = facilityInfoModel.issueStatus!!.lowercase()

        binding.apply {
            cvPending.setOnClickListener {
                selectedStatus = "pending"
                tvPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                cvPending.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_color_light))

                tvOngoing.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))
                cvOngoing.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                tvCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))
                cvCompleted.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            cvOngoing.setOnClickListener {
                selectedStatus = "ongoing"
                tvOngoing.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                cvOngoing.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_color_light))

                tvPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))
                cvPending.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                tvCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))
                cvCompleted.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            cvCompleted.setOnClickListener {
                selectedStatus = "completed"
                tvCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                cvCompleted.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_color_light))

                tvPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))
                cvPending.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                tvOngoing.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_light))
                cvOngoing.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            when (facilityInfoModel.issueStatus.lowercase()) {
                "pending" -> binding.cvPending.performClick()
                "ongoing" -> binding.cvOngoing.performClick()
                "completed" -> binding.cvCompleted.performClick()
            }


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

            cvAddNote.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.custom_dialog_confirmation_dialog, null)

                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setCancelable(false)
                    .create()
                dialog.show()

                val exitButton = dialog.findViewById<ImageView>(R.id.ivExit)
                exitButton.setOnClickListener {
                    dialog.dismiss()
                }

                val cancelButton = dialog.findViewById<CardView>(R.id.cvWarningCancel)
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }

                val confirmButton = dialog.findViewById<CardView>(R.id.cvConfirm)
                confirmButton.setOnClickListener {
                    dialog.dismiss()

                    //  show loading dialog
                    loadingDialog = LoadingDialogFragment("Loading", "Please wait while we update the status of the selected issue.")
                    loadingDialog?.show(parentFragmentManager, "loading_dialog")

                    val newStatus = when (selectedStatus.lowercase()) {
                        "pending" -> "Pending"
                        "ongoing" -> "Ongoing"
                        "completed" -> "Completed"
                        else -> return@setOnClickListener
                    }

                    fireStore.collection(Constants.COLLECTION_REPORTS)
                        .whereEqualTo("dateSubmitted", facilityInfoModel.dateSubmitted)
                        .whereEqualTo("issueDescription", facilityInfoModel.issueDescription)
                        .whereEqualTo("issueName", facilityInfoModel.issueName)
                        .whereEqualTo("issueSubmitterID", facilityInfoModel.issueSubmitterID)
                        .whereEqualTo("roomNumber", facilityInfoModel.roomNumber)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val document = querySnapshot.documents[0]
                                fireStore.collection(Constants.COLLECTION_REPORTS)
                                    .document(document.id)
                                    .update("issueStatus", newStatus)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                                        loadingDialog?.dismiss()
                                        this@FacilityStaffFragment.dismiss()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
                                        loadingDialog?.dismiss()
                                        this@FacilityStaffFragment.dismiss()
                                    }
                            } else {
                                Toast.makeText(requireContext(), "No matching document found", Toast.LENGTH_SHORT).show()
                                loadingDialog?.dismiss()
                                this@FacilityStaffFragment.dismiss()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to query document", Toast.LENGTH_SHORT).show()
                            loadingDialog?.dismiss()
                            this@FacilityStaffFragment.dismiss()
                        }
                }

                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                val metrics = resources.displayMetrics
                val marginInPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    metrics
                ).toInt()
                val screenWidth = metrics.widthPixels
                val dialogWidth = screenWidth - (marginInPx * 2)

                dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            // Automatically trigger initial selection
            when (facilityInfoModel.issueStatus?.lowercase()) {
                "pending" -> cvPending.performClick()
                "ongoing" -> cvOngoing.performClick()
                "completed" -> cvCompleted.performClick()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}