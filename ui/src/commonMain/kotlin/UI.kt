import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun SharedApp(
    loader: SharedDataLoader,
) {
    val scope = rememberCoroutineScope()
    val store = remember(loader, scope) {
        SharedStore(
            repository = SharedRepository(loader),
            scope = scope,
        )
    }
    val state by store.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    MaterialTheme {
        LaunchedEffect(store) {
            store.effects.collect { effect ->
                when (effect) {
                    is SharedEffect.ShowError -> {
                        snackbarHostState.showSnackbar(effect.message)
                        store.dispatch(SharedIntent.ErrorShown)
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { contentPadding ->
            SharedScreen(
                state = state,
                onIntent = store::dispatch,
                contentPadding = contentPadding,
            )
        }
    }
}
