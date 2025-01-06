package xyz.teamgravity.googlecredentialmanagerdemo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel() {

    var state: AuthenticationState by mutableStateOf(AuthenticationState())
        private set

    private val _event = Channel<AuthenticationEvent>()
    val event: Flow<AuthenticationEvent> = _event.receiveAsFlow()

    private fun handleRegister(result: CredentialManager.CreateResult) {
        when (result) {
            is CredentialManager.CreateResult.Success -> {
                viewModelScope.launch {
                    _event.send(
                        AuthenticationEvent.NavigateHome(
                            username = result.username,
                            password = result.password
                        )
                    )
                }
            }

            CredentialManager.CreateResult.Cancelled -> {
                state = state.copy(error = "Register was cancelled.")
            }

            CredentialManager.CreateResult.Failure -> {
                state = state.copy(error = "Register failed.")
            }
        }
    }

    private fun handleLogin(result: CredentialManager.GetResult) {
        when (result) {
            is CredentialManager.GetResult.Success -> {
                viewModelScope.launch {
                    _event.send(
                        AuthenticationEvent.NavigateHome(
                            username = result.username,
                            password = result.password
                        )
                    )
                }
            }

            CredentialManager.GetResult.Cancelled -> {
                state = state.copy(error = "Login was cancelled.")
            }

            CredentialManager.GetResult.Failure -> {
                state = state.copy(error = "Login failed.")
            }

            CredentialManager.GetResult.NotExists -> {
                state = state.copy(error = "Credentials not exist.")
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun onAction(action: AuthenticationAction) {
        when (action) {
            AuthenticationAction.ReadLogin -> {
                viewModelScope.launch {
                    _event.send(AuthenticationEvent.ReadLogin)
                }
            }

            is AuthenticationAction.Register -> handleRegister(action.result)
            is AuthenticationAction.Login -> handleLogin(action.result)
            is AuthenticationAction.UsernameChange -> {
                state = state.copy(username = action.value)
            }

            is AuthenticationAction.PasswordChange -> {
                state = state.copy(password = action.value)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////

    sealed interface AuthenticationEvent {
        data class NavigateHome(
            val username: String,
            val password: String
        ) : AuthenticationEvent

        data object ReadLogin : AuthenticationEvent
    }
}