package com.example.smart.ui.account.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
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
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {
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
}