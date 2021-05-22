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
import kotlinx.coroutines.CancellationException
import model.PrimaryViewModel
import tornadofx.*
import java.io.File
import java.net.URL

class Client : Controller(){
    val client = HttpClient(Apache) {
        expectSuccess = false
    }

    suspend fun downloadRss(url: String) : String? = client.get(url)

    suspend fun downloadImage(url: String) : File? {
        val file = File("${path.path}/images/${url.fileNameEncode()}")
        file.parentFile.mkdirs()
        val res = client.request<HttpResponse> {
            url(URL(url))
            method = HttpMethod.Get
        }
        return if (res.status.isSuccess()) {
            println("download image")
            res.content.copyAndClose(file.writeChannel())
            file
        } else null.also { println("${res.status} ${res.content}") }
    }

    suspend fun downloadAudio(url: String) : File? {
        try {
            PrimaryViewModel.isDownloadMedia.value = true
            val file = File("${path.path}/downloads/${url.fileNameEncode()}")
            file.parentFile.mkdirs()
            val res = client.request<HttpResponse> {
                url(URL(url))
                method = HttpMethod.Get
            }
            return if (res.status.isSuccess()) {
                PrimaryViewModel.isDownloadMedia.value = false
                println("download audio")
                res.content.copyAndClose(file.writeChannel())
                file
            } else null
                .also { println("${res.status} ${res.content}")
                    PrimaryViewModel.isDownloadMedia.value = false
                }
        } catch(e: Throwable) {
            if (e !is CancellationException){
                PrimaryViewModel.setError("unable to download media")
                PrimaryViewModel.isDownloadMedia.value = false
            }
            return null.also { println(e) }
        }
    }

    suspend fun downloadNowPlaying(url: String) : File? {
        try {
            val file = File("${path.path}/nowPlaying/${url.fileNameEncode()}")
            file.parentFile.mkdirs()
            val res = client.request<HttpResponse> {
                url(URL(url))
                method = HttpMethod.Get
            }
            return if (res.status.isSuccess()) {
                println("download now playing")
                res.content.copyAndClose(file.writeChannel())
                file
            } else null.also { println("${res.status} ${res.content}") }
        } catch(e: Throwable) {
            if (e !is CancellationException){
                Playback.player?.dispose()
                Playback.player = null
                PrimaryViewModel.setError("unable to get audio stream")
            }
            return null.also { println(e) }
        }

    }
}