import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking

val client = HttpClient()

fun getStatus(): Int = runBlocking {
    val response: HttpResponse = client.get("http://google.com/")
    response.status
    client.close()
    response.status.value
}

fun getContent(): String = runBlocking {
    val response: HttpResponse = client.get("http://google.com/")
    response.status
    client.close()
    response.bodyAsText()
}

fun main() {
    println(getStatus())
}