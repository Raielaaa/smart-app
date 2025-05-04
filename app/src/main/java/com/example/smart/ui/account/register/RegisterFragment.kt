package com.example.smart.ui.account.register

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentRegisterBinding
import com.example.smart.utils.Constants
import com.example.smart.utils.LoadingDialogFragment
import com.example.smart.utils.ShowInfoDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    private var loadingDialog: LoadingDialogFragment? = null

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
                        val submittedCode = dialog.findViewById<EditText>(R.id.etSubmittedCode)
                        val cvVerify = dialog.findViewById<CardView>(R.id.cvVerify)
                        cvVerify.setOnClickListener {
                            dialog.dismiss()
                            //  retrieve special code
                            verifyCode(
                                submittedCode.text.toString(),
                                firstName,
                                lastName,
                                selectedRole,
                                dialog
                            )
                        }
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

    private fun verifyCode(
        submittedCode: String,
        firstName: String,
        lastName: String,
        selectedRole: String,
        dialog: AlertDialog
    ) {
        var retrievedCode = ""
        //  show loading dialog
        loadingDialog = LoadingDialogFragment("Loading", "Please wait while we process your credentials.")
        loadingDialog?.show(parentFragmentManager, "loading_dialog")

        try {
            fireStore.collection(Constants.COLLECTION_SPECIAL_CODE)
                .document(Constants.DOCUMENT_SPECIAL_CODE)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        retrievedCode = document.getString("code").toString()
                        loadingDialog?.dismiss()

                        if (submittedCode == retrievedCode) {
                            //  store the values in a hashmap then pass it to the next fragment
                            val userData = hashMapOf(
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "role" to selectedRole.lowercase()
                            )

                            //  serialize the hashmap into a bundle
                            val bundle = Bundle()
                            bundle.putSerializable("userData", userData)

                            dialog.dismiss()
                            findNavController().navigate(
                                R.id.action_registerFragment_to_registerPasswordFragment,
                                bundle
                            )
                        } else {
                            dialog.dismiss()
                            ShowInfoDialogFragment("Warning", "Incorrect code, please try again.")
                                .show(parentFragmentManager, "info_dialog")
                        }
                    }
                }.addOnFailureListener {
                    loadingDialog?.dismiss()
                    Log.w("firebase-error", "An error occurred at RegisterFragment.kt: ${it.message}")
                }
        } catch (exception: Exception) {
            Log.w("firebase-error", "An error occurred at RegisterFragment.kt: $exception")
        }
    }
}