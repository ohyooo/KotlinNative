import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking

private const val TEST_URL = "https://baidu.com/"

object SharedBridge {
    fun nativeGetStatus(): Int = runBlocking {
        val client = HttpClient {
            followRedirects = false
        }
        try {
            // TLS sessions are not supported on Native platform: https://youtrack.jetbrains.com/issue/KTOR-7262
            val response: HttpResponse = client.get(TEST_URL)
            response.status.value
        } catch (e: Throwable) {
            e.printStackTrace()
            0
        } finally {
            client.close()
        }
//    1
    }

    fun nativeGetContent(): String = runBlocking {
        val client = HttpClient {
            followRedirects = false
        }
        try {
            // TLS sessions are not supported on Native platform: https://youtrack.jetbrains.com/issue/KTOR-7262
            val response: HttpResponse = client.get(TEST_URL)
            response.bodyAsText()
        } catch (e: Throwable) {
            e.printStackTrace()
            e.toString()
        } finally {
            client.close()
        }
//    "123"
    }
}

fun main() {
    println("status: ${SharedBridge.nativeGetStatus()}")
}
