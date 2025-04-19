package com.example.smart.ui.account.login

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.apply {
            tvUserOption.setOnClickListener {
                showUserOptionDialog()
            }
            tvNoAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUserOptionDialog()
    }

    private fun showUserOptionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_user_option, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()
    }
}