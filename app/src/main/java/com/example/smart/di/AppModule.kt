package com.example.smart.di;

import android.content.Context
import com.example.smart.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
}
