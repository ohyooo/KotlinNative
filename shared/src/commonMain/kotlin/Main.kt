import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking


fun getStatus(): Int = runBlocking {
    val client = HttpClient {
        followRedirects = false
    }
    try {
        val response: HttpResponse = client.get("http://google.com/")
        response.status.value
    } catch (e: Throwable) {
        println("aaaaaaaaaa =======1======== aaaaaaaaaa")
        e.printStackTrace()
        println("aaaaaaaaaa =======2======== aaaaaaaaaa")
        0
    } finally {
        client.close()
    }
}

fun getContent(): String = runBlocking {
    val client = HttpClient {
        followRedirects = false
    }
    try {
        val response: HttpResponse = client.get("http://google.com/")
        response.bodyAsText()
    } catch (e: Throwable) {
        println("aaaaaaaaaa ---------1-------- aaaaaaaaaa")
        e.printStackTrace()
        println("aaaaaaaaaa ---------2-------- aaaaaaaaaa")
        e.toString()
    } finally {
        client.close()
    }
}

fun main() {
    println(getStatus())
}
