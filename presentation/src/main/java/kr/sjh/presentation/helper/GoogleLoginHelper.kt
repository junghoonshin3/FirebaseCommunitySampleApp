package kr.sjh.presentation.helper

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CreateCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kr.sjh.presentation.R
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class GoogleLoginHelper @Inject constructor(
    private val context: Context, private val credentialManager: CredentialManager
) {
    //    private val googleIdOption: GetSignInWithGoogleOption =
//        GetSignInWithGoogleOption.Builder(context.resources.getString(R.string.WEB_CLIENT_ID))
//            .build()

    private val rawNonce = UUID.randomUUID().toString()
    private val bytes = rawNonce.toByteArray()
    private val md = MessageDigest.getInstance("SHA-256")
    private val digest = md.digest(bytes)
    private val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

    private val googleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.resources.getString(R.string.WEB_CLIENT_ID))
        .setNonce(hashedNonce).build()


    suspend fun requestGoogleLogin(
        activityContext: Context
    ): Result<GoogleIdTokenCredential?> {
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        Log.d("sjh", "requestGoogleLogin")
        return runCatching {
            val result = credentialManager.getCredential(
                context = activityContext, request = request
            )
            when (val credential = result.credential) {
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