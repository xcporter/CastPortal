package controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import kotlinx.coroutines.SupervisorJob
import model.PrimaryViewModel
import model.UpdateProgress
import org.joda.time.format.PeriodFormatterBuilder
import tornadofx.*
import java.io.File
import kotlin.properties.Delegates.observable

object Playback {
    val mediaLoadSupervisor = SupervisorJob()

    val store = find<Store>()

//    Initial value is nonsense file so error can be displayed if anything returns null on first load
    val audio = SimpleObjectProperty<File>(File("file:///${Configuration.path.path}/nowPlaying/*"))
    val media = SimpleObjectProperty<Media>()
    val image = SimpleObjectProperty<Image>()
    val progress = mutableMapOf<String, Double>()
    var player: MediaPlayer? by observable(null) { _, _, new ->
        new?.apply {
            currentTimeProperty().onChange {
                sliderOutProperty.value = it?.toMillis()?.div(media.duration.toMillis()) ?: 0.0
            }

            timeText.bind(currentTimeProperty().stringBinding { durationFormatter.print(
                org.joda.time.Duration(
                    it?.toMillis()?.toLong()
                ).toPeriod()) })
            setOnPlaying {
                isPlaying.value = true
                seekToProgress()
            }
            setOnReady { seekToProgress() }
            setOnPaused {
                isPlaying.value = false
                saveProgress()
            }
            setOnStopped {
                isPlaying.value = false
            }
            setOnEndOfMedia {
                PrimaryViewModel.castScopes
                    .flatMap { it.model.items }
                    .firstOrNull { it.isPlaying.value }
                    ?.let { playing ->
                        progress[playing.guid.value] = -1.0
                        store.saveProgress(progress)
                    }
                PrimaryViewModel.autoplay(progress)
                FX.eventbus.fire(UpdateProgress())
            }
            volumeProperty.bindBidirectional(volumeProperty())
        }
    }

    val isPlaying = SimpleBooleanProperty(false)

    val isMute = SimpleBooleanProperty(false)

    val timeText = SimpleStringProperty()

    val titleText = SimpleStringProperty()

    val authorText = SimpleStringProperty()

    val compositeText = stringBinding(titleText, authorText) {
        if (titleText.value != null && authorText.value != null) "${authorText.value} - ${titleText.value}"
        else ""
    }

    val volumeProperty = SimpleDoubleProperty(1.0)
    var prevVolume = 0.0

    val sliderOutProperty = SimpleDoubleProperty(0.0)

    val sliderInProperty = SimpleDoubleProperty(0.0)

    val isActive = SimpleBooleanProperty(false)

    init {
        store.loadProgress()?.let { progress.putAll(it) }
        audio.onChange {
            isActive.value = true
            it?.let {
                try {
                    media.value = Media(it.toURI().toString())
                    player?.dispose()
                    player = MediaPlayer(media.value)
                    PrimaryViewModel.error.value = null
                } catch (e: Throwable) {
                    println(e)
                    e.printStackTrace()
                }
            }
        }

        sliderInProperty.onChange {
            player?.seek(Duration(it * media.value?.duration?.toMillis()!!))
        }

        isMute.onChange {
            if (it) {
                prevVolume = volumeProperty.value
                volumeProperty.value = 0.0
            } else {
                volumeProperty.value = prevVolume
            }
        }
    }

    fun rewind15() = player?.let {
        it.seek(it.currentTime.subtract(Duration.seconds(15.0)))
    }

    fun fastforward15() = player?.let {
        it.seek(it.currentTime.add(Duration.seconds(15.0)))
    }

    fun saveProgress() {
        PrimaryViewModel.castScopes
            .flatMap { it.model.items }
            .firstOrNull { it.isPlaying.value }
            ?.let { playing ->
                player?.currentTime?.toMillis()?.let { progress[playing.guid.value] = it }
                store.saveProgress(progress)
            }
        FX.eventbus.fire(UpdateProgress())
    }

    private fun seekToProgress() {
        PrimaryViewModel.castScopes
            .flatMap { it.model.items }
            .firstOrNull { it.isPlaying.value }
            ?.let { playing ->
                progress[playing.guid.value]?.let {
                    if (it != -1.0) player?.seek(Duration.millis(it))
                    else  player?.seek(Duration.ZERO)
                }
            }
    }

    private val durationFormatter = PeriodFormatterBuilder().apply {
        printZeroAlways()
        appendHours()
        appendSeparator(":")
        minimumPrintedDigits(2)
        appendMinutes()
        appendSeparator(":")
        appendSeconds()
    }.toFormatter()
}