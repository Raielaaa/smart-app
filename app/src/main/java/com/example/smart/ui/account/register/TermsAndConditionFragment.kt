package com.example.lab_ass_app.ui.account.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smart.databinding.FragmentRegisterPasswordBinding
import com.example.smart.databinding.FragmentTermsAndConditionBinding
import com.example.smart.ui.account.register.RegisterPasswordFragment
import com.example.smart.ui.account.register.RegisterPasswordViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermsOfServiceDialog(
    private val hostFragment: RegisterPasswordFragment,
    private val registerViewModel: RegisterPasswordViewModel,
    private val registerBinding: FragmentRegisterPasswordBinding
) : BottomSheetDialogFragment() {

    // View Binding
    private lateinit var binding: FragmentTermsAndConditionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTermsAndConditionBinding.inflate(inflater, container, false)

        // Set up click listeners for accept and decline buttons
        setupButtonListeners()

        return binding.root
    }

    // Set up click listeners for accept and decline buttons
    private fun setupButtonListeners() {
        binding.apply {
            // Accept button click listener
            btnAccept.setOnClickListener {
                // Insert user data to Firebase FireStore and navigate to the next screen
//                registerViewModel.insertDataToAuth(
//                    registerBinding.etLRN.text.toString(),
//                    registerBinding.etEmail.text.toString(),
//                    registerBinding.tilPassword.text.toString(),
//                    registerBinding.spUserRegister.selectedItem.toString(),
//                    hostFragment
//                )

                // Dismiss the dialog
                this@TermsOfServiceDialog.dismiss()
            }

            // Decline button click listener
            btnDecline.setOnClickListener {
                // Show a toast message indicating that acceptance is required
                Toast.makeText(
                    hostFragment.requireContext(),
                    "Acceptance of terms and conditions is required to proceed.",
                    Toast.LENGTH_LONG
                ).show()

                // Dismiss the dialog
                this@TermsOfServiceDialog.dismiss()
            }
        }
    }
}