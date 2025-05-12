package com.example.smart.ui.report_issue

import android.app.AlertDialog
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentIssueInfoBinding
import com.example.smart.models.FacilityInfoModel
import com.example.smart.utils.Constants
import com.example.smart.utils.LoadingDialogFragment
import com.example.smart.utils.ShowInfoDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class IssueInfoFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseStorage.Instance")
    lateinit var firebaseStorage: StorageReference

    private var loadingDialog: LoadingDialogFragment? = null
    private val viewModel: IssueInfoViewModel by viewModels()
    private lateinit var binding: FragmentIssueInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssueInfoBinding.inflate(inflater, container, false)
        retrieveUniqueRoomNumbersFromDB(binding)

        val imageUriString = arguments?.getString("capturedPhotoUri")
        val imageUri = imageUriString?.let { Uri.parse(it) }
        binding.etTargetLab.setCompoundDrawablesWithIntrinsicBounds(0, 0, com.google.android.material.R.drawable.mtrl_ic_arrow_drop_down, 0)

        imageUri?.let {
            binding.ivPhotoTaken.setImageURI(it)
        }

        binding.apply {
            etTargetLab.setOnClickListener {
                etTargetLab.showDropDown()
            }

            cvSubmitIssue.setOnClickListener {
                //  entry validations
                val etTargetLab = etTargetLab.text.toString()
                val etIssueTitle = etIssueTitle.text.toString()
                val etYourName = etYourName.text.toString()
                val etIssueDesc = etIssueDesc.text.toString()

                if (
                    etTargetLab.isEmpty() ||
                    etIssueTitle.isEmpty() ||
                    etYourName.isEmpty() ||
                    etIssueDesc.isEmpty()
                ) {
                    ShowInfoDialogFragment("Warning", "Please fill all the required fields.")
                        .show(parentFragmentManager, "info_dialog")
                } else {
                    if (imageUri != null) {
                        showConfirmationDialog(
                            etTargetLab,
                            etIssueTitle,
                            etYourName,
                            etIssueDesc,
                            imageUri
                        )
                    }
                }
            }
        }

        return binding.root
    }

    private fun retrieveUniqueRoomNumbersFromDB(binding: FragmentIssueInfoBinding) {
        val listOfRoomNumbers: ArrayList<String> = arrayListOf()

        //  show loading dialog
        loadingDialog = LoadingDialogFragment("Loading", "Please wait while we retrieve all the necessary information.")
        loadingDialog?.show(this.parentFragmentManager, "loading_dialog")

        try {
            fireStore.collection(Constants.COLLECTION_FACILITIES)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        listOfRoomNumbers.add(document.id)
                    }

                    val dropdownAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listOfRoomNumbers)
                    binding.etTargetLab.setAdapter(dropdownAdapter)
                    loadingDialog?.dismiss()
                    Log.d("FirestoreRooms", listOfRoomNumbers.toString())
                }.addOnFailureListener { exception ->
                    loadingDialog?.dismiss()
                    Log.w("tag", "An error occurred: ${exception.message}")
                }
        } catch (err: Exception) {
            Log.w("tag", "An error occurred: ${err.message}")
        }
    }

    private fun showConfirmationDialog(
        etTargetLab: String,
        etIssueTitle: String,
        etYourName: String,
        etIssueDesc: String,
        imageUri: Uri
    ) {
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

            // Show loading dialog
            loadingDialog = LoadingDialogFragment("Loading", "Uploading image and submitting your report...")
            loadingDialog?.show(parentFragmentManager, "loading_dialog")

            val roomNumber = binding.etTargetLab.text.toString().trim()
            val issueTitle = binding.etIssueTitle.text.toString().trim()
            val issueDesc = binding.etIssueDesc.text.toString().trim()
            val submitterId = firebaseAuth.currentUser?.uid ?: "Unknown"

            val currentDate = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MMMM dd, yyyy : hh:mm a", Locale.getDefault())
            val formattedDate = formatter.format(currentDate)

            val fileName = "issue_images/${UUID.randomUUID()}.jpg"
            val storageRef = firebaseStorage.child(fileName)

            // Upload image to Firebase Storage
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val data = FacilityInfoModel(
                            roomNumber = roomNumber,
                            issueName = issueTitle,
                            issueStatus = "Pending",
                            issueDescription = issueDesc,
                            issueSubmitterID = submitterId,
                            issueImageUri = downloadUri.toString(),
                            dateSubmitted = formattedDate
                        )

                        // Save to Firestore
                        fireStore.collection(Constants.COLLECTION_REPORTS)
                            .add(data)
                            .addOnSuccessListener {
                                // update roomNumber's reportCount
                                fireStore.collection(Constants.COLLECTION_FACILITIES)
                                    .document(roomNumber)
                                    .get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        val currentCountString = documentSnapshot.getString("reportCount") ?: "0"
                                        val newCount = (currentCountString.toIntOrNull() ?: 0) + 1

                                        fireStore.collection(Constants.COLLECTION_FACILITIES)
                                            .document(roomNumber)
                                            .update("reportCount", newCount.toString())
                                            .addOnSuccessListener {
                                                loadingDialog?.dismiss()
                                                ShowInfoDialogFragment("Success", "Your issue has been submitted successfully.")
                                                    .show(parentFragmentManager, "info_dialog")
                                                findNavController().navigate(R.id.homeFragment)
                                            }
                                            .addOnFailureListener { exception ->
                                                loadingDialog?.dismiss()
                                                Log.w("mytag", "An error occurred while updating count: ${exception.message}")
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        loadingDialog?.dismiss()
                                        Log.w("mytag", "An error occurred while retrieving count: ${exception.message}")
                                    }
                            }
                            .addOnFailureListener { exception ->
                                loadingDialog?.dismiss()
                                Log.w("FirestoreError", "An error occurred: ${exception.message}")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    loadingDialog?.dismiss()
                    Log.e("UploadError", "Image upload failed: ${exception.message}")
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
}