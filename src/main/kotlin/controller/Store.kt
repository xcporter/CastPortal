package controller

import controller.Configuration.path
import controller.Encoder.fileNameEncode
import controller.RssParser.parseRss
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import model.RSS
import tornadofx.*
import java.io.File

class Store : Controller(),  CoroutineScope {
    val job = SupervisorJob()
    override val coroutineContext = Dispatchers.IO + job

    init { if (!path.exists()) path.mkdirs() }

    fun saveRssText(str: String, name: String) = File("${path.path}/rss/${name.fileNameEncode()}")
        .apply {
            parentFile.mkdirs()
            writeText(str, Charsets.UTF_8)
        }

    fun saveSubscriptions(streams: List<String>) = File("${path.path}/subscriptions")
        .apply {
            if (exists()) delete()
            parentFile.mkdirs()
            streams.forEach { appendText("$it\n") }
        }

    fun saveSubscription(sub: String) = File("${path.path}/subscriptions")
        .apply {
            if (exists()) appendText("$sub\n")
        }

    fun getSubscriptions (): List<String>? = File("${path.path}/subscriptions").takeIf { it.exists() }?.readLines()

    fun loadRssText(name: String) : RSS? = File("${path.path}/rss/$name").takeIf { it.exists() }?.parseRss()

    fun listStoredRss() : List<String> = File("${path.path}/rss/")
        .listFiles()
        ?.let { it.map { it.name }.filter { it.length == 64 } } ?: listOf()

    fun removeDownload(url: String) = File("${path.path}/downloads/${url.fileNameEncode()}").delete()

    fun getFromDownloads(url: String): File? = File("${path.path}/downloads/${url.fileNameEncode()}").takeIf { it.exists() }

    fun clearNowPlaying() = File("${path.path}/nowPlaying/").listFiles()?.forEach { it.delete() }

    fun loadNowPlaying(url: String) = File("${path.path}/nowPlaying/${url.fileNameEncode()}").takeIf { it.exists() }

    fun deleteAllDownloads(obj: RSS) {
        println(obj.channel?.title)
        obj.channel?.image?.href?.let {
            File("${path.path}/images/${it.fileNameEncode()}").delete()
        }
        obj.channel?.items?.forEach {
            File("${path.path}/downloads/${it.enclosure?.url?.fileNameEncode()}").delete()
        }
    }
}