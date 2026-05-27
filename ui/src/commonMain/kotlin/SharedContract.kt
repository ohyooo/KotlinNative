internal data class SharedUiState(
    val status: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val statusText: String
        get() = status.ifBlank { "-" }

    val contentText: String
        get() = content.ifBlank { "-" }

    val buttonText: String
        get() = if (isLoading) "Loading" else "Load"
}

internal sealed interface SharedIntent {
    data object LoadClicked : SharedIntent

    data object ErrorShown : SharedIntent
}

internal sealed interface SharedAction {
    data object LoadingStarted : SharedAction

    data class LoadingSucceeded(val data: SharedData) : SharedAction

    data class LoadingFailed(val message: String) : SharedAction

    data object ErrorConsumed : SharedAction
}

internal sealed interface SharedEffect {
    data class ShowError(val message: String) : SharedEffect
}

internal data class SharedData(
    val status: String,
    val content: String,
)
