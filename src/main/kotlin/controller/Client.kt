package controller

import controller.Configuration.path
import controller.Encoder.fileNameEncode
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.*
import tornadofx.*
import java.io.File
import java.net.URL

class Client : Controller(), CoroutineScope  {
    val job = SupervisorJob()
    override val coroutineContext = Dispatchers.IO + job

    val client = HttpClient(Apache) {
        expectSuccess = false
    }

    suspend fun downloadRss(url: String) : String? = withContext(coroutineContext) { client.get(url) }

    suspend fun downloadImage(url: String) : File? = withContext(coroutineContext) {
        val file = File("${path.path}/images/${url.fileNameEncode()}")
        file.parentFile.mkdirs()
        val res = client.request<HttpResponse> {
            url(URL(url))
            method = HttpMethod.Get
        }
        return@withContext if (res.status.isSuccess()) {
            println("get success")
            res.content.copyAndClose(file.writeChannel())
            file
        } else null.also { println("${res.status} ${res.content}") }
    }
}