package xyz.teamgravity.googlecredentialmanagerdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AuthenticationScreen(
    manager: CredentialManager,
    scope: CoroutineScope = rememberCoroutineScope(),
    state: AuthenticationState,
    onAction: (action: AuthenticationAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                onAction(AuthenticationAction.ReadLogin)
            }
        ) {
            Text(
                text = stringResource(R.string.login)
            )
        }
        TextField(
            value = state.username,
            onValueChange = { value ->
                onAction(AuthenticationAction.UsernameChange(value))
            },
            label = {
                Text(
                    text = stringResource(R.string.username)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = state.password,
            onValueChange = { value ->
                onAction(AuthenticationAction.PasswordChange(value))
            },
            label = {
                Text(
                    text = stringResource(R.string.password)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }
        Button(
            onClick = {
                scope.launch {
                    val result = manager.upsert(
                        username = state.username,
                        password = state.password
                    )
                    onAction(AuthenticationAction.Register(result))
                }
            }
        ) {
            Text(
                text = stringResource(R.string.register)
            )
        }
    }
}