package com.example.smart.ui.account.register

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
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

        uiConfigurations(binding)

        return binding.root
    }

    private fun uiConfigurations(binding: FragmentRegisterBinding) {
        //  For dropdown config
        val roles = listOf(
            "Student",
            "Teacher",
            "Officers",
            "Staff"
        )
        var typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins)
        val fontSizeSp = resources.getDimension(com.intuit.ssp.R.dimen._8ssp) / resources.displayMetrics.scaledDensity

        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, roles) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).apply {
                    typeface = this@apply.typeface
                    textSize = fontSizeSp  // Applies 8ssp
                }
                return view
            }
        }

        //  click functions
        binding.apply {
            cvNext.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_registerPasswordFragment)
            }
            ivBackButtonRegister.setOnClickListener {
                findNavController().popBackStack()
            }
            dropdownRole.apply {
                setAdapter(adapter)
                setOnClickListener {
                    showDropDown()
                    setOnItemClickListener { parent, _, position, _ ->
                        val selectedRole = parent.getItemAtPosition(position).toString()
                    }
                }
            }
        }

    }
}