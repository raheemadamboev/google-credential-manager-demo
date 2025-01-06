package xyz.teamgravity.googlecredentialmanagerdemo

import android.app.Activity
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException

class CredentialManager(
    private val activity: Activity,
    private val manager: CredentialManager
) {

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    suspend fun upsert(
        username: String,
        password: String
    ): CreateResult {
        try {
            manager.createCredential(
                context = activity,
                request = CreatePasswordRequest(
                    id = username,
                    password = password
                )
            )
            return CreateResult.Success(
                username = username,
                password = password
            )
        } catch (e: CreateCredentialCancellationException) {
            e.printStackTrace()
            return CreateResult.Cancelled
        } catch (e: CreateCredentialException) {
            e.printStackTrace()
            return CreateResult.Failure
        }
    }

    suspend fun get(): GetResult {
        try {
            val response = manager.getCredential(
                context = activity,
                request = GetCredentialRequest(
                    credentialOptions = listOf(GetPasswordOption())
                )
            )
            val credentials = response.credential as? PasswordCredential ?: return GetResult.Failure

            return GetResult.Success(
                username = credentials.id,
                password = credentials.password
            )
        } catch (e: GetCredentialCancellationException) {
            e.printStackTrace()
            return GetResult.Cancelled
        } catch (e: NoCredentialException) {
            e.printStackTrace()
            return GetResult.NotExists
        } catch (e: GetCredentialException) {
            e.printStackTrace()
            return GetResult.Failure
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////

    sealed interface CreateResult {
        data class Success(
            val username: String,
            val password: String
        ) : CreateResult

        data object Cancelled : CreateResult
        data object Failure : CreateResult
    }

    sealed interface GetResult {
        data class Success(
            val username: String,
            val password: String
        ) : GetResult

        data object Cancelled : GetResult
        data object Failure : GetResult
        data object NotExists : GetResult
    }
}