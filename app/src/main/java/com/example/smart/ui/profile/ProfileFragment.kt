package com.example.smart.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentProfileBinding
import com.example.smart.utils.Constants
import com.example.smart.utils.Helper
import com.example.smart.utils.LoadingDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding
    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        initProfilePage(binding)
        binding.apply {
            textView29.text = Helper.userRole.replaceFirstChar { it.uppercaseChar() }

            cvTermsAndPolicy.setOnClickListener {
                EmptyTermsAndConditionFragment().show(parentFragmentManager, "show_terms_and_condition_bottom_dialog")
            }
            cvShowContact.setOnClickListener {
                ContactFragment().show(parentFragmentManager, "show_contact_dialog")
            }
        }

        return binding.root
    }

    private fun initProfilePage(binding: FragmentProfileBinding) {
        //  show loading dialog
        loadingDialog = LoadingDialogFragment("Loading", "Please wait while we process your credentials.")
        loadingDialog?.show(parentFragmentManager, "loading_dialog")

        fireStore.collection(Constants.COLLECTION_USER_ACCOUNTS)
            .document(firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                val fullName = "${result.getString("firstNameModel")} ${result.getString("lastNameModel")}"
                val emailAddress = result.getString("emailModel")
                val issuesSent = result.getString("issuesSent")

                binding.apply {
                    tvFullNameProfile.text = fullName
                    tvEmailProfile.text = emailAddress
                    tvHeaderEmailProfile.text = emailAddress
                    tvHeaderNameProfile.text = "Hello, ${fullName.split(" ")[0]}"
                    tvIssuesSent.text = issuesSent
                }

                loadingDialog?.dismiss()
            }.addOnFailureListener { exception ->
                loadingDialog?.dismiss()
                Log.w("tag", "An error occurred: ${exception.message}")
            }
    }
}