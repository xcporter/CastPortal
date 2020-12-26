package model

import controller.*
import controller.Encoder.fileNameEncode
import controller.Encoder.formatDate
import controller.Encoder.formatTime
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornadofx.*
import java.net.URL
import kotlin.coroutines.CoroutineContext

class EpisodeModel(init: RssItem, override val scope: CastScope) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    val store: Store by inject()
    val client: Client by inject()

    val title = SimpleStringProperty(init.title)
    val author = SimpleStringProperty(init.author)
    val description = SimpleStringProperty(init.description)
    val duration = SimpleStringProperty(init.duration.formatTime())
    val pubDate = SimpleStringProperty(init.pubDate.formatDate())
    val audioUrl = SimpleStringProperty(init.enclosure?.url)
    val isPlaying = SimpleBooleanProperty(false)

    val detail = SimpleBooleanProperty(false)

    fun startPlayback () = launch(coroutineContext) {
        val nowPlaying = store.loadNowPlaying(audioUrl.value ?: "")
//        Load audio if already downloaded
        Playback.player?.stop()
        if (nowPlaying?.name == audioUrl.value.fileNameEncode()) {
//            load media
            if (Playback.audio.value?.name != audioUrl.value?.fileNameEncode()) {
                Playback.audio.value = nowPlaying
                isPlaying.value = true
                Playback.image.value = Image(URL("file://${Configuration.path.path}/images/${scope.model.imageUrl.value}").toExternalForm())
                Playback.titleText.value = title.value
                Playback.authorText.value = author.value
            }
//            play / pause on click
            if (Playback.isPlaying.value) {
                Playback.player?.pause()
            } else {
                Playback.player?.play()
            }
        } else {
//            Load new episode if now playing doesn't match
            CastView.clearDetail()
            detail.value = true
            CastView.clearIsPlaying()
            isPlaying.value = true
            store.clearNowPlaying()
            audioUrl.value?.let {
                Playback.audio.value = client.downloadNowPlaying(it)
            }
            Playback.player?.play()
            Playback.image.value = Image(URL("file://${Configuration.path.path}/images/${scope.model.imageUrl.value}").toExternalForm())
            Playback.titleText.value = title.value
            Playback.authorText.value = author.value
        }
    }
}