package xyz.teamgravity.googlecredentialmanagerdemo

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Authentication : Route

    @Serializable
    data class Home(
        val username: String,
        val password: String
    ) : Route
}