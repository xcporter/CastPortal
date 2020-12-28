package controller

import controller.Configuration.path
import controller.Encoder.fileNameEncode
import controller.RssParser.parseRss
import kotlinx.coroutines.*
import model.CastScope
import model.PrimaryViewModel
import model.RSS
import tornadofx.*
import java.io.File

class Syndication : Controller(), CoroutineScope {
    val job = SupervisorJob()
    override val coroutineContext = Dispatchers.IO + job

    val client: Client by inject()
    val store: Store by inject()

    val rssLinks = observableListOf<String>(store.getSubscriptions() ?: Configuration.defaultStreams.also { store.saveSubscriptions(it) })
    val rssFeeds = observableListOf<RSS>()

    init {
        rssFeeds.onChange {
            PrimaryViewModel.castScopes.clear()
            PrimaryViewModel.castScopes.addAll(it.list.map { CastScope(it) } )
        }
    }

    suspend fun refreshRss() = withContext(coroutineContext) {
        rssLinks.map { link ->
            launch {
                client.downloadRss(link)?.let { rss ->
                    store.saveRssText(rss, link)
                } ?: println("couldn't refresh RSS:")
            }
        }.joinAll()
        store
            .listStoredRss()
            .takeUnless { it.isEmpty() }
            ?.mapNotNull { store.loadRssText(it) }
            ?.also {
                withContext(Dispatchers.Main){ rssFeeds.clear() }
            }
            ?.let {
                withContext(Dispatchers.Main){ rssFeeds.addAll(it) }
            }
    }

    suspend fun loadExisting() = withContext(coroutineContext) {
        store
            .listStoredRss()
            .takeUnless { it.isEmpty() }
            ?.mapNotNull { store.loadRssText(it) }
            ?.also {
                withContext(Dispatchers.Main){ rssFeeds.clear() }
            }
            ?.let {
                withContext(Dispatchers.Main){ rssFeeds.addAll(it) }
            }
        PrimaryViewModel.isDownloadRss.value = false
    }

    suspend fun downloadImages(urls: List<String>) = withContext(coroutineContext) {
        urls.forEach {
            launch { client.downloadImage(it) }
        }
    }

    suspend fun checkMissingImages() : List<String> = withContext(coroutineContext) {
        return@withContext rssFeeds
            .filter { !(File("${path.path}/images/${it.channel?.image?.href?.fileNameEncode()}").exists()) }
            .mapNotNull { it.channel?.image?.href }
    }

    suspend fun removeRss(url: String) {
        File("${path.path}/rss/${url.fileNameEncode()}").delete()
        rssLinks.indexOf(url).let {
            store.deleteAllDownloads(rssFeeds[it])
            withContext(Dispatchers.Main) {
                rssFeeds.removeAt(it)
                rssLinks.remove(url)
            }
        }
        store.saveSubscriptions(rssLinks)
    }

    suspend fun validateStream (url: String) : Boolean {
        return try {
            client.downloadRss(url)?.parseRss()?.validate() ?: false
        } catch(e: Throwable) {
            println(e)
            false
        }
    }
}