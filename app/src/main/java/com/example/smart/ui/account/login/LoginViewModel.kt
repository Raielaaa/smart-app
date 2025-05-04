package com.example.smart.ui.account.login

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.utils.Helper
import com.example.smart.utils.LoadingDialogFragment
import com.example.smart.utils.ShowInfoDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.rpc.Help
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel responsible for handling Login-related logic.
 * Uses dependency injection to access the configured GoogleSignInClient instance.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    @Named("GoogleSignInClient.Instance")
    private val googleSignInClient: GoogleSignInClient,
    @Named("FirebaseAuth.Instance")
    val firebaseAuth: FirebaseAuth,
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    private var loadingDialog: LoadingDialogFragment? = null

    /**
     * Returns the Intent used to initiate the Google Sign-In flow
     */
    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Sign out a google signed in user
     */
    fun signOut(onComplete: () -> Unit) {
        googleSignInClient.signOut()
            .addOnCompleteListener {
                onComplete()
            }
    }

    fun loginUser(
        email: String,
        password: String,
        hostFragment: Fragment
    ) {
        //  show loading dialog
        loadingDialog = LoadingDialogFragment("Loading", "Please wait while we verify your credentials.")
        loadingDialog?.show(hostFragment.parentFragmentManager, "loading_dialog")

        try {
            firebaseFireStore.collection("smart-app-user-accounts")
                .whereEqualTo("emailModel", email)
                .whereEqualTo("roleModel", Helper.userRole)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result
                        if (!documents.isEmpty && documents != null) {
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Navigate to the appropriate screen based on the user type
                                        // Dismiss dialog
                                        loadingDialog?.dismiss()

                                        hostFragment.findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                        displayToastMessage("Login successful", hostFragment)
                                    }
                                }.addOnFailureListener { exception ->
                                    // show authentication failure
                                    displayToastMessage(exception.toString(), hostFragment)
                                    // Dismiss dialog
                                    loadingDialog?.dismiss()
                                }
                        } else {
                            // No documents found, account not found
//                            displayToastMessage("Account not found, please try again", hostFragment)
                            ShowInfoDialogFragment("Warning", "Account not found, please try again")
                                .show(hostFragment.parentFragmentManager, "info_dialog")
                            // Dismiss dialog
                            loadingDialog?.dismiss()
                        }
                    } else {
                        // Task not successful
                        displayToastMessage(task.exception.toString(), hostFragment)
                        // Dismiss dialog
                        loadingDialog?.dismiss()
                    }
                }
        } catch (err: Exception) {
            // general exception
            displayToastMessage(err.toString(), hostFragment)
            // Dismiss dialog
            loadingDialog?.dismiss()
        }
    }

    // Function to display toast messages
    private fun displayToastMessage(message: String, hostFragment: Fragment) {
        Toast.makeText(
            hostFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}