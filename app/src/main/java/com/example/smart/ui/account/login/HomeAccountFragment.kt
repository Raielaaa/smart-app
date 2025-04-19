package com.example.smart.ui.account.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentHomeAccountBinding

class HomeAccountFragment : Fragment() {
    private lateinit var binding: FragmentHomeAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeAccountBinding.inflate(inflater, container, false)



        return binding.root
    }
}