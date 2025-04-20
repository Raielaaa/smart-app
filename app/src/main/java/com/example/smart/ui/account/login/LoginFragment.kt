package com.example.smart.ui.account.login

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.databinding.FragmentLoginBinding
import com.example.smart.utils.ShowInfoDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    /**
     *  ActivityResultLauncher for handling Google Sign-In result
     *  This replaces deprecated onActivityResult() approach.
     */
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                //  Attempt to retrieve the signed-in account
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                val email = account?.email

                //  Using ID token to authenticate with Firebase

            } catch(e: ApiException) {
                Log.e("GoogleLogin", "Google sign-in failed", e)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val dialog = ShowInfoDialogFragment("Notice", "Please fill in all the required fields.")
        dialog.show(parentFragmentManager, "infoDialog")

        //  set up click listeners and navigation
        binding.apply {
            tvUserOption.setOnClickListener {
                showUserOptionDialog()
            }
            tvNoAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            cvGoogleLogin.setOnClickListener {
                //  force sign out before showing the account picker
                viewModel.signOut {
                    //  start google sign in flow
                    val signInIntent = viewModel.getGoogleSignInIntent()
                    googleSignInLauncher.launch(signInIntent)
                }
            }
            ivBackButtonLogin.setOnClickListener {
                findNavController().popBackStack()
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

        // Set transparent background to apply custom corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set custom width (25dp margins on both sides)
        val metrics = resources.displayMetrics
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            25f,
            metrics
        ).toInt()
        val screenWidth = metrics.widthPixels
        val dialogWidth = screenWidth - (marginInPx * 2)

        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}