internal object SharedReducer {
    fun reduce(state: SharedUiState, action: SharedAction): SharedUiState =
        when (action) {
            SharedAction.LoadingStarted -> state.copy(
                status = "",
                content = "",
                isLoading = true,
                errorMessage = null,
            )

            is SharedAction.LoadingSucceeded -> state.copy(
                status = action.data.status,
                content = action.data.content,
                isLoading = false,
                errorMessage = null,
            )

            is SharedAction.LoadingFailed -> state.copy(
                isLoading = false,
                errorMessage = action.message,
            )

            SharedAction.ErrorConsumed -> state.copy(errorMessage = null)
        }
}
