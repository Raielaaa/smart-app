package com.example.smart.ui.account.register

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.smart.R
import com.example.smart.models.RegisterUserDataModel
import com.example.smart.utils.LoadingDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RegisterPasswordViewModel @Inject constructor(
    @Named("FirebaseAuth.Instance")
    val firebaseAuth: FirebaseAuth,
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    private var loadingDialog: LoadingDialogFragment? = null

    fun uploadUserDataToFireStore(
        completeInfo: HashMap<String, String?>,
        hostFragment: Fragment
    ) {
        val email: String = completeInfo["email"].toString()
        val password: String = completeInfo["password"].toString()
        val firstName: String = completeInfo["firstName"].toString()
        val lastName: String = completeInfo["lastName"].toString()
        val role: String = completeInfo["role"].toString()

        //  show loading dialog
        loadingDialog = LoadingDialogFragment("Loading", "Please wait while we process your registration.")
        loadingDialog?.show(hostFragment.parentFragmentManager, "loading_dialog")

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userID = task.result.user?.uid

                        // storing user credentials to RegisterUserDataModel data class
                        val registerUserDataModel = RegisterUserDataModel(
                            userID.toString(),
                            firstName,
                            lastName,
                            email,
                            role
                        )

                        //  inserting user credentials to FireStore
                        firebaseFireStore.collection("smart-app-user-accounts")
                            .document(userID ?: "Error: UserID not found")
                            .set(registerUserDataModel)
                            .addOnSuccessListener {
                                // If success
                                displayToastMessage("Register successful", hostFragment)

                                // Navigate to LoginFragment
                                hostFragment.findNavController().navigate(R.id.action_registerPasswordFragment_to_loginFragment)

                                // Dismiss dialog
                                loadingDialog?.dismiss()
                            }.addOnFailureListener { exception ->
                                // If failed
                                displayToastMessage("Error: ${exception.localizedMessage}", hostFragment)

                                // Dismiss dialog
                                loadingDialog?.dismiss()
                            }
                    }
                }.addOnFailureListener { exception ->
                    displayToastMessage("Error: ${exception.localizedMessage}", hostFragment)
                    loadingDialog?.dismiss()
                }
        } catch (err: Exception) {
            displayToastMessage("Error: $err", hostFragment)
        }
    }

    // Function for displaying toast messages on the screen
    private fun displayToastMessage(message: String, registerFragment: Fragment) {
        Toast.makeText(
            registerFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}