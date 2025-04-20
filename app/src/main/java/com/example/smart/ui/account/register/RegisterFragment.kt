package com.example.smart.ui.account.register

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.apply {
            cvLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_registerPasswordFragment)
            }
            ivBackButtonRegister.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        return binding.root
    }
}