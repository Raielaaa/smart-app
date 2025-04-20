package com.example.smart.di;

import android.content.Context
import com.example.smart.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides a singleton instance of GoogleSignInClient configured with:
     * - ID Token request for authentication
     * - Email access
     * @param context Application context used to create the client
     * @return Configured GoogleSignInClient
     */
    @Singleton
    @Provides
    @Named("GoogleSignInClient.Instance")
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context
    ): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.CLIENT_ID_TOKEN)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Provides a singleton instance of Firebase Authentication
     * @param
     * @return Firebase Authentication Instance
     */
    @Singleton
    @Provides
    @Named("FirebaseAuth.Instance")
    fun provideFirebaseAuthInstance() = FirebaseAuth.getInstance()

    /**
     * Provides a singleton instance of Firebase FireStore
     * @param
     * @return Firebase FireStore Instance
     */
    @Singleton
    @Provides
    @Named("FirebaseFireStore.Instance")
    fun provideFirebaseFirebaseFireStoreInstance() = FirebaseFirestore.getInstance()

    /**
     * Provides a singleton instance of Firebase Storage
     * @param
     * @return Firebase Storage Instance
     */
    @Singleton
    @Provides
    @Named("FirebaseStorage.Instance")
    fun provideFirebaseStorageInstance() = FirebaseStorage.getInstance().reference
}
