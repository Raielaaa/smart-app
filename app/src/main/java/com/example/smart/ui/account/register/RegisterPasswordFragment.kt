package com.example.smart.ui.account.register

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.ui.account.register.TermsOfServiceDialog
import com.example.smart.R
import com.example.smart.databinding.FragmentRegisterPasswordBinding
import com.example.smart.utils.ShowInfoDialogFragment

class RegisterPasswordFragment : Fragment() {
    private val viewModel: RegisterPasswordViewModel by viewModels()
    private lateinit var binding: FragmentRegisterPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterPasswordBinding.inflate(inflater, container, false)

        binding.apply {
            cvNext.setOnClickListener {
                val email = etEmailRegister.text.toString()
                val password = etPasswordRegister.text.toString()
                val confirmPassword = etConfirmPasswordRegister.text.toString()

                if (
                    email.isEmpty() ||
                    password.isEmpty() ||
                    confirmPassword.isEmpty()
                ) {
                    val warningDialog = ShowInfoDialogFragment("Warning", "Please fill all the required fields.")
                    warningDialog.show(parentFragmentManager, "warning_dialog")
                } else if (password != confirmPassword) {
                    val warningDialog = ShowInfoDialogFragment("Warning", "Password and confirm password doesn't match.")
                    warningDialog.show(parentFragmentManager, "warning_dialog")

                    etConfirmPasswordRegister.setText("")
                    etPasswordRegister.setText("")
                } else {
                    TermsOfServiceDialog(
                        this@RegisterPasswordFragment,
                        viewModel,
                        binding
                    ).show(this@RegisterPasswordFragment.parentFragmentManager, "Register_BottomDialog")
                }
            }
            ivBackButtonRegisterPass.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        return binding.root
    }
}