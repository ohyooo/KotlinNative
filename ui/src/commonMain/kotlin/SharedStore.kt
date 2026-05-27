import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SharedStore(
    private val repository: SharedRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(SharedUiState())
    val state: StateFlow<SharedUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SharedEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<SharedEffect> = _effects.asSharedFlow()

    private var loadJob: Job? = null

    fun dispatch(intent: SharedIntent) {
        when (intent) {
            SharedIntent.LoadClicked -> load()
            SharedIntent.ErrorShown -> reduce(SharedAction.ErrorConsumed)
        }
    }

    private fun load() {
        if (loadJob?.isActive == true) return

        loadJob = scope.launch {
            reduce(SharedAction.LoadingStarted)

            when (val result = repository.load()) {
                is SharedLoadResult.Success -> reduce(SharedAction.LoadingSucceeded(result.data))
                is SharedLoadResult.Failure -> {
                    reduce(SharedAction.LoadingFailed(result.message))
                    _effects.emit(SharedEffect.ShowError(result.message))
                }
            }
        }
    }

    private fun reduce(action: SharedAction) {
        _state.update { state ->
            SharedReducer.reduce(state, action)
        }
    }
}
