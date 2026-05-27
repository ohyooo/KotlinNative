import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SharedRepository(
    private val loader: SharedDataLoader,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend fun load(): SharedLoadResult =
        withContext(dispatcher) {
            try {
                SharedLoadResult.Success(
                    SharedData(
                        status = loader.loadStatus().toString(),
                        content = loader.loadContent(),
                    ),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                SharedLoadResult.Failure(e.message ?: e.toString())
            }
        }
}

internal sealed interface SharedLoadResult {
    data class Success(val data: SharedData) : SharedLoadResult

    data class Failure(val message: String) : SharedLoadResult
}
