package xyz.teamgravity.googlecredentialmanagerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import xyz.teamgravity.googlecredentialmanagerdemo.ui.theme.GoogleCredentialManagerDemoTheme

class MainActivity : ComponentActivity() {

    private val manager: CredentialManager by lazy {
        CredentialManager(
            activity = this,
            manager = androidx.credentials.CredentialManager.create(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoogleCredentialManagerDemoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    val controller = rememberNavController()

                    NavHost(
                        navController = controller,
                        startDestination = Route.Authentication,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable<Route.Authentication> {
                            val viewmodel = viewModel<AuthenticationViewModel>()

                            ObserveEvent(
                                flow = viewmodel.event,
                                onEvent = { event ->
                                    when (event) {
                                        is AuthenticationViewModel.AuthenticationEvent.NavigateHome -> {
                                            controller.navigate(
                                                route = Route.Home(
                                                    username = event.username,
                                                    password = event.password
                                                ),
                                                builder = {
                                                    popUpTo(
                                                        route = Route.Authentication,
                                                        popUpToBuilder = {
                                                            inclusive = true
                                                        }
                                                    )
                                                }
                                            )
                                        }

                                        AuthenticationViewModel.AuthenticationEvent.ReadLogin -> {
                                            val result = manager.get()
                                            viewmodel.onAction(AuthenticationAction.Login(result))
                                        }
                                    }
                                }
                            )

                            AuthenticationScreen(
                                manager = manager,
                                state = viewmodel.state,
                                onAction = viewmodel::onAction
                            )
                        }
                        composable<Route.Home> { entry ->
                            val route = entry.toRoute<Route.Home>()

                            HomeScreen(
                                username = route.username,
                                password = route.password
                            )
                        }
                    }
                }
            }
        }
    }
}