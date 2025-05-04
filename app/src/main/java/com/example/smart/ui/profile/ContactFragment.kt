package com.example.smart.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentContactBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ContactFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(inflater, container, false)

        binding.apply {
            cvEmail.setOnClickListener {
                openEmail()
            }
            cvMessage.setOnClickListener {
                openMessage()
            }
            cvCall.setOnClickListener {
                openCall()
            }

            tvEmail.setOnClickListener {
                openEmail()
            }
            tvMessage.setOnClickListener {
                openMessage()
            }
            tvCall.setOnClickListener {
                openCall()
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun openEmail() {
        val email = "ralphhonra@gmail.com"
        val subject = Uri.encode("Inquiry")
        val body = Uri.encode("For any inquiries, please enter your message below before sending.")
        val uri = Uri.parse("mailto:$email?subject=$subject&body=$body")

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = uri
        }

        startActivity(Intent.createChooser(intent, "Send email via..."))
    }

    private fun openMessage() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:09701900391")
            putExtra("sms_body", "For any inquiries, please enter your message below before sending.")
        }
        startActivity(intent)
    }

    private fun openCall() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:09701900391")
        }
        startActivity(intent)
    }
}