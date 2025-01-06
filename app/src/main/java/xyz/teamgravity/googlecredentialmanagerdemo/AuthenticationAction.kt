package xyz.teamgravity.googlecredentialmanagerdemo

sealed interface AuthenticationAction {
    data object ReadLogin : AuthenticationAction
    data class Register(val result: CredentialManager.CreateResult) : AuthenticationAction
    data class Login(val result: CredentialManager.GetResult) : AuthenticationAction
    data class UsernameChange(val value: String) : AuthenticationAction
    data class PasswordChange(val value: String) : AuthenticationAction
}