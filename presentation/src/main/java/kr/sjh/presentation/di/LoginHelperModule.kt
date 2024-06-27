package kr.sjh.presentation.di

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.sjh.presentation.helper.GoogleLoginHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginHelperModule {

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }

    @Provides
    @Singleton
    fun provideGoogleLoginHelper(
        @ApplicationContext context: Context,
        credentialManager: CredentialManager
    ): GoogleLoginHelper {
        return GoogleLoginHelper(context, credentialManager)
    }

}