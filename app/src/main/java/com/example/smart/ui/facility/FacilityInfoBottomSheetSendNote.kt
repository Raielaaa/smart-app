package com.example.smart.ui.facility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentFacilityInfoBottomSheetAddNoteBinding
import com.example.smart.models.FacilityCommunityModel
import com.example.smart.models.FacilityHomeModel
import com.example.smart.models.FacilityInfoModel
import com.example.smart.utils.Constants
import com.example.smart.utils.Helper
import com.example.smart.utils.LoadingDialogFragment
import com.example.smart.utils.ShowInfoDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.rpc.Help
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class FacilityInfoBottomSheetSendNote(
    private val items: FacilityInfoModel
) : BottomSheetDialogFragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentFacilityInfoBottomSheetAddNoteBinding
    private var loadingDialog: LoadingDialogFragment? = null

    val currentDate = Calendar.getInstance().time
    val formatter = SimpleDateFormat("MMMM dd, yyyy : hh:mm a", Locale.getDefault())
    val formattedDate = formatter.format(currentDate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilityInfoBottomSheetAddNoteBinding.inflate(inflater, container, false)

        binding.apply {
            cvSendNote.setOnClickListener {
                val inputtedNote = etAddNote.text.toString()

                if (inputtedNote.isNotEmpty()) {
                    //  show loading dialog
                    loadingDialog = LoadingDialogFragment("Loading", "Please wait while we retrieve all the necessary information.")
                    loadingDialog?.show(parentFragmentManager, "loading_dialog")

                    fireStore.collection(Constants.COLLECTION_USER_ACCOUNTS)
                        .document(items.issueSubmitterID!!)
                        .get()
                        .addOnSuccessListener { result ->
                            val firstName = result.getString("firstNameModel")
                            val lastName = result.getString("lastNameModel")
                            val fullName = "$firstName $lastName"

                            val data = FacilityCommunityModel(
                                issueTitle = items.issueName,
                                roomNumber = items.roomNumber,
                                communitySender = fullName,
                                communityDate = formattedDate,
                                communityContent = inputtedNote,
                                communitySenderRole = Helper.userRole
                            )
                            fireStore.collection(Constants.COLLECTION_COMMUNITY)
                                .add(data)
                                .addOnSuccessListener {
                                    loadingDialog?.dismiss()
                                    this@FacilityInfoBottomSheetSendNote.dismiss()
                                    ShowInfoDialogFragment("Success", "Your message has been delivered successfully.")
                                        .show(parentFragmentManager, "info_dialog")
                                }.addOnFailureListener { exception ->
                                    loadingDialog?.dismiss()
                                    this@FacilityInfoBottomSheetSendNote.dismiss()
                                    ShowInfoDialogFragment("Warning", "An error occurred, please try again later. Err: ${exception.message}")
                                        .show(parentFragmentManager, "info_dialog")
                                }
                        }.addOnFailureListener { exception ->
                            loadingDialog?.dismiss()
                            this@FacilityInfoBottomSheetSendNote.dismiss()
                            ShowInfoDialogFragment("Warning", "An error occurred, please try again later. Err: ${exception.message}")
                                .show(parentFragmentManager, "info_dialog")
                        }
                } else {
                    loadingDialog?.dismiss()
                    ShowInfoDialogFragment("Warning", "Please fill all the required fields.")
                        .show(parentFragmentManager, "info_dialog")
                }
            }
        }

        return binding.root
    }
}
