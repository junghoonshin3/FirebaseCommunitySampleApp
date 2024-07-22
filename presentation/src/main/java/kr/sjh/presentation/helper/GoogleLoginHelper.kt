package kr.sjh.presentation.helper

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kr.sjh.presentation.R
import javax.inject.Inject

class GoogleLoginHelper @Inject constructor(
    context: Context,
    private val credentialManager: CredentialManager
) {

//    private val googleIdOption = GetGoogleIdOption.Builder()
//        .setFilterByAuthorizedAccounts(true)
//        .setServerClientId(context.resources.getString(R.string.WEB_CLIENT_ID))
//        .setNonce(null)
//        .build()

    private val googleIdOption: GetSignInWithGoogleOption =
        GetSignInWithGoogleOption.Builder(context.resources.getString(R.string.WEB_CLIENT_ID))
            .build()

    suspend fun requestGoogleLogin(
        activityContext: Context
    ): Result<GoogleIdTokenCredential?> {
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        Log.d("sjh", "requestGoogleLogin")
        return runCatching {
            val credential = credentialManager.getCredential(
                context = activityContext, request = request
            ).credential

            when (credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        GoogleIdTokenCredential.createFrom(credential.data)
                    } else {
                        null
                    }
                }

                else -> {
                    null
                }
            }
        }
    }
}