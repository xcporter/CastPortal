package controller

import controller.Encoder.fileNameEncode
import kotlinx.coroutines.*
import model.CastScope
import model.PrimaryViewModel
import model.RSS
import tornadofx.*
import java.io.File

class Syndication : Controller(), CoroutineScope {
    val job = SupervisorJob()
    override val coroutineContext = Dispatchers.IO + job
    val rssLinks = observableListOf<String>("https://feeds.npr.org/510312/podcast.xml", "https://rss.art19.com/1619", "http://joeroganexp.joerogan.libsynpro.com/rss", "https://feeds.megaphone.fm/ADL9840290619", "https://feeds.megaphone.fm/unlocking-us")
    val rssFeeds = observableListOf<RSS>()
    val client: Client by inject()
    val store: Store by inject()

    init {
        rssFeeds.onChange {
            PrimaryViewModel.castScopes.addAll(it.list.map { CastScope(it) } )
        }
    }

    suspend fun refreshRss() = withContext(coroutineContext) {
        PrimaryViewModel.isDownloadRss.value = true
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
            ?.map { store.loadRssText(it) }
            ?.also {
                withContext(Dispatchers.Main){ rssFeeds.clear() }
            }
            ?.let {
                withContext(Dispatchers.Main){ rssFeeds.addAll(it) }
            }
        PrimaryViewModel.isDownloadRss.value = false
    }

    suspend fun loadExisting() = withContext(coroutineContext) {
        store
            .listStoredRss()
            .takeUnless { it.isEmpty() }
            ?.map { store.loadRssText(it) }
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
            .filter { !(File("${Configuration.path.path}/images/${it.channel?.image?.href?.fileNameEncode()}").exists()) }
            .mapNotNull { it.channel?.image?.href }
    }

}