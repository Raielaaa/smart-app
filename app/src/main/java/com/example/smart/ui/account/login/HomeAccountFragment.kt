package com.example.smart.ui.account.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentHomeAccountBinding

class HomeAccountFragment : Fragment() {
    private lateinit var binding: FragmentHomeAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeAccountBinding.inflate(inflater, container, false)

        binding.apply {
            cvLogin.setOnClickListener {
                findNavController().navigate(R.id.action_homeAccountFragment_to_loginFragment)
            }
            cvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_homeAccountFragment_to_registerFragment)
            }
        }

        return binding.root
    }
}