package xyz.teamgravity.googlecredentialmanagerdemo

data class AuthenticationState(
    val username: String = "",
    val password: String = "",
    val error: String? = null
)