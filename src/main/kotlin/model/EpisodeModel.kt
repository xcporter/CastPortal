package model

import controller.*
import controller.Encoder.fileNameEncode
import controller.Encoder.formatDate
import controller.Encoder.formatTime
import controller.Encoder.stripHtml
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import tornadofx.*
import java.net.URL
import kotlin.coroutines.CoroutineContext

class EpisodeModel(init: RssItem, override val scope: CastScope) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Playback.mediaLoadSupervisor
    val store: Store by inject()
    val client: Client by inject()

    val title = SimpleStringProperty(init.title)
    val author = SimpleStringProperty(init.author)
    val description = SimpleStringProperty(init.description.stripHtml())
    val duration = SimpleStringProperty(init.duration.formatTime())
    val pubDate = SimpleStringProperty(init.pubDate.formatDate())
    val guid = SimpleStringProperty(init.guid)
    val audioUrl = SimpleStringProperty(init.enclosure?.url)

    val isPlaying = SimpleBooleanProperty(false)
    val isDownload = SimpleBooleanProperty(false)

    val progress = SimpleDoubleProperty(0.0)

    init {
        updateProgress()
        isDownload.value = PrimaryViewModel.downloads.contains(init.enclosure?.url?.fileNameEncode())
        isDownload.onChange {
            if (it) scope.model.downloaded.add(this)
            else scope.model.downloaded.remove(this)
        }

        isPlaying.onChange {
            if (it) {
                scope.currentDescription.value = description.value
                scope.currentTitle.value = title.value
                PrimaryViewModel.clearDetailsOnNonPlayingCasts()
            }
        }

        subscribe<UpdateProgress> {
            updateProgress()
        }
    }

    fun download() {
        launch(coroutineContext + PrimaryViewModel.backgroundJobs) {
            client.downloadAudio(audioUrl.value)
                ?.takeIf { it.exists() }
                ?.run {
                    isDownload.value = true
                    PrimaryViewModel.downloads.add(this.name)
                }
        }
    }

    fun removeDownload() {
        launch(coroutineContext + PrimaryViewModel.backgroundJobs) {
            store.removeDownload(audioUrl.value)
        }
        isDownload.value = false
    }

    fun updateProgress() {
        progress.value = Playback.progress[guid.value]?.let {
            if (it == -1.0) -1.0 else it / Time.parse(duration.value)
        } ?: 0.0
    }

    /**
     * This function starts playback in a variety of different ways depending on the context
     *
     * Todo it does too much and should be broken down into something simpler
     *
     */

    fun startPlayback () {
        Playback.mediaLoadSupervisor.cancelChildren()
        launch(coroutineContext) {
            PrimaryViewModel.isDownloadMedia.value = true
            val nowPlaying = store.loadNowPlaying(audioUrl.value ?: "")
//        Load audio if downloaded as now playing
            if (nowPlaying?.name == audioUrl.value.fileNameEncode()) {
//            load media if needed
                if (Playback.audio.value?.name != audioUrl.value?.fileNameEncode()) {
                    Playback.audio.value = nowPlaying
                    isPlaying.value = true
                    Playback.image.value =
                        Image(URL("file:///${Configuration.path.path}/images/${scope.model.imageUrl.value}").toExternalForm())
                    Playback.titleText.value = title.value
                    Playback.authorText.value = author.value
                }
//            play / pause on click
                if (Playback.isPlaying.value) {
                    Playback.player?.pause()
                } else {
                    Playback.player?.play()
                }
            } else if (isDownload.value) {
//                if exists in downloads
//                  delete nowPlaying to prevent future confusion
                store.clearNowPlaying()
                if (Playback.audio.value?.name != audioUrl.value?.fileNameEncode()) {
//                    load media from downloads
                    Playback.audio.value = store.getFromDownloads(audioUrl.value)
                    PrimaryViewModel.clearIsPlaying()
                    isPlaying.value = true
                    Playback.image.value =
                        Image(URL("file:///${Configuration.path.path}/images/${scope.model.imageUrl.value}").toExternalForm())
                    Playback.titleText.value = title.value
                    Playback.authorText.value = author.value
                    Playback.player?.play()
                } else {
//                     play / pause on click
                    if (Playback.isPlaying.value) {
                        Playback.player?.pause()
                    } else {
                        Playback.player?.play()
                    }
                }
            } else {
//            Load new episode if now playing doesn't match
                Playback.player?.stop()
                PrimaryViewModel.clearIsPlaying()
                isPlaying.value = true
                store.clearNowPlaying()
                audioUrl.value?.let {
                    Playback.audio.value = client.downloadNowPlaying(it)
                }
                Playback.player?.play()
                Playback.image.value =
                    Image(URL("file:///${Configuration.path.path}/images/${scope.model.imageUrl.value}").toExternalForm())
                Playback.titleText.value = title.value
                Playback.authorText.value = author.value
            }
            PrimaryViewModel.isDownloadMedia.value = false
        }
    }
}