import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking

fun getStatus(): Int = runBlocking {
    val client = HttpClient()
    val response: HttpResponse = client.get("http://ktor.io/")
    response.status
    client.close()
    response.status.value
}

fun getContent(): String = runBlocking {
    val client = HttpClient()
    val response: HttpResponse = client.get("http://ktor.io/")
    response.status
    client.close()
    response.bodyAsText()
}