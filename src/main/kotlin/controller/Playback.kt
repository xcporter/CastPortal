package controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import model.PrimaryViewModel
import org.joda.time.format.PeriodFormatterBuilder
import tornadofx.*
import java.io.File
import java.net.URL
import kotlin.properties.Delegates.observable

object Playback {
    val mediaLoadSupervisor = SupervisorJob()

//    Initial value is nonsense file so error can be displayed if anything returns null on first load
    val audio = SimpleObjectProperty<File>(File("${Configuration.path.path}/nowPlaying/*"))
    val media = SimpleObjectProperty<Media>()
    val image = SimpleObjectProperty<Image>()
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
            }
            setOnPaused {
                isPlaying.value = false
            }
            setOnStopped {
                isPlaying.value = false
            }
        }
    }

    val isPlaying = SimpleBooleanProperty(false)

    val timeText = SimpleStringProperty()

    val titleText = SimpleStringProperty()

    val authorText = SimpleStringProperty()

    val sliderOutProperty = SimpleDoubleProperty(0.0)

    val sliderInProperty = SimpleDoubleProperty(0.0)

    init {
        audio.onChange {
            it?.let {
                try {
                    media.value = Media(URL("file://${it.path}").toExternalForm())
                    player?.dispose()
                    player = MediaPlayer(media.value)
                    PrimaryViewModel.error.value = ""
                } catch (e: Throwable) {
                    println(e)
                }
            }
        }

        sliderInProperty.onChange {
            player?.seek(Duration(it * media.value?.duration?.toMillis()!!))
        }
    }

    fun rewind15() = player?.let {
        it.seek(it.currentTime.subtract(Duration.seconds(15.0)))
    }

    fun fastforward15() = player?.let {
        it.seek(it.currentTime.add(Duration.seconds(15.0)))
    }

    val durationFormatter = PeriodFormatterBuilder().apply {
        printZeroAlways()
        appendHours()
        appendSeparator(":")
        minimumPrintedDigits(2)
        appendMinutes()
        appendSeparator(":")
        appendSeconds()
    }.toFormatter()
}