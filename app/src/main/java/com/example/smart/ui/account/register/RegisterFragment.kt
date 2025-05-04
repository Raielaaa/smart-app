package com.example.smart.ui.account.register

import android.app.AlertDialog
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
import com.example.smart.utils.ShowInfoDialogFragment

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
                    textSize = fontSizeSp
                }
                return view
            }
        }

        //  click functions
        binding.apply {
            var selectedRole = ""

            cvNext.setOnClickListener {
                val firstName = etFirstName.text.toString()
                val lastName = etLastName.text.toString()

                if (
                    firstName.isEmpty() ||
                    lastName.isEmpty() ||
                    selectedRole.isEmpty()
                ) {
                    val dialog = ShowInfoDialogFragment("Warning", "Please fill all the required fields.")
                    dialog.show(parentFragmentManager, "warning_dialog")
                } else {
                    if (selectedRole.lowercase() == "officers" || selectedRole.lowercase() == "staff") {
                        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_code_confirmation, null)

                        val dialog = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setCancelable(false)
                            .create()

                        dialog.show()
                    } else {
                        //  store the values in a hashmap then pass it to the next fragment
                        val userData = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "role" to selectedRole.lowercase()
                        )

                        //  serialize the hashmap into a bundle
                        val bundle = Bundle()
                        bundle.putSerializable("userData", userData)

                        findNavController().navigate(
                            R.id.action_registerFragment_to_registerPasswordFragment,
                            bundle
                        )
                    }
                }
            }
            ivBackButtonRegister.setOnClickListener {
                findNavController().popBackStack()
            }
            dropdownRole.apply {
                setAdapter(adapter)
                setOnClickListener {
                    showDropDown()
                    setOnItemClickListener { parent, _, position, _ ->
                        selectedRole = parent.getItemAtPosition(position).toString()
                    }
                }
            }
        }

    }
}