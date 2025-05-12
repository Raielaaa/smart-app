package com.example.smart.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.smart.databinding.FragmentProfileBinding
import com.example.smart.utils.Constants
import com.example.smart.utils.Helper
import com.example.smart.utils.LoadingDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseStorage.Instance")
    lateinit var firebaseStorageRef: StorageReference

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding
    private var loadingDialog: LoadingDialogFragment? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    uploadImageToFirebase(uri)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        initProfilePage(binding)

        binding.apply {
            textView29.text = Helper.userRole.replaceFirstChar { it.uppercaseChar() }

            cvTermsAndPolicy.setOnClickListener {
                EmptyTermsAndConditionFragment().show(
                    parentFragmentManager,
                    "show_terms_and_condition_bottom_dialog"
                )
            }

            cvShowContact.setOnClickListener {
                ContactFragment().show(parentFragmentManager, "show_contact_dialog")
            }

            ivProfileImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                imagePickerLauncher.launch(intent)
            }
        }

        return binding.root
    }

    private fun initProfilePage(binding: FragmentProfileBinding) {
        loadingDialog = LoadingDialogFragment("Loading", "Please wait while we process your credentials.")
        loadingDialog?.show(parentFragmentManager, "loading_dialog")

        fireStore.collection(Constants.COLLECTION_USER_ACCOUNTS)
            .document(firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                val fullName = "${result.getString("firstNameModel")} ${result.getString("lastNameModel")}"
                val emailAddress = result.getString("emailModel")
                val issuesSent = result.getString("issuesSent")
                val profileImageLink = result.getString("profileImageLink")

                binding.apply {
                    tvFullNameProfile.text = fullName
                    tvEmailProfile.text = emailAddress
                    tvHeaderEmailProfile.text = emailAddress
                    tvHeaderNameProfile.text = "Hello, ${fullName.split(" ")[0]}"
                    tvIssuesSent.text = issuesSent

                    // Load profile image if available
                    if (!profileImageLink.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(profileImageLink)
                            .circleCrop()
                            .into(ivProfileImage)
                    }
                }

                loadingDialog?.dismiss()
            }.addOnFailureListener { exception ->
                loadingDialog?.dismiss()
                Log.w("ProfileFragment", "Error loading profile: ${exception.message}")
            }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val ref = firebaseStorageRef.child("profile_images/$userId.jpg")

        loadingDialog = LoadingDialogFragment("Uploading", "Uploading your photo...")
        loadingDialog?.show(parentFragmentManager, "upload_dialog")

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    updateUserProfileImage(downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                loadingDialog?.dismiss()
                Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("UploadError", "Upload failed: ${e.message}")
            }
    }

    private fun updateUserProfileImage(downloadUrl: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        fireStore.collection(Constants.COLLECTION_USER_ACCOUNTS)
            .document(userId)
            .update("profileImageLink", downloadUrl)
            .addOnSuccessListener {
                Glide.with(requireContext())
                    .load(downloadUrl)
                    .circleCrop()
                    .into(binding.ivProfileImage)

                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                loadingDialog?.dismiss()
            }
            .addOnFailureListener { e ->
                loadingDialog?.dismiss()
                Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreUpdate", "Update failed: ${e.message}")
            }
    }
}
