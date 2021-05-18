package view

import BaseStyle.Companion.midHigh
import BaseStyle.Companion.player
import BaseStyle.Companion.playerButtons
import controller.Playback
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.util.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import model.PrimaryViewModel
import tornadofx.*

class Player : View(), CoroutineScope {
    override val coroutineContext = Dispatchers.Main
    val slider = slider(0.0, 1.0, 0.0) {
        hgrow = Priority.ALWAYS
        valueProperty().bindBidirectional(Playback.sliderOutProperty)
//        Switch bindings to avoid control feedback loop between current time and seek
        valueChangingProperty().onChange {
            if (it) {
                valueProperty().unbindBidirectional(Playback.sliderOutProperty)
                valueProperty().bindBidirectional(Playback.sliderInProperty)
            } else {
                valueProperty().unbindBidirectional(Playback.sliderInProperty)
                valueProperty().bindBidirectional(Playback.sliderOutProperty)
            }
        }

        setOnMousePressed {
            valueProperty().unbindBidirectional(Playback.sliderOutProperty)
            Playback.sliderInProperty.value = value
            valueProperty().bindBidirectional(Playback.sliderOutProperty)
        }
    }

    override val root = vbox {
        alignment = Pos.BOTTOM_CENTER
        addClass(player)
        hbox(5.0) {
            style {
                padding = box(20.px)
            }
            useMaxWidth = true

            hbox (4.0) {
                alignment = Pos.CENTER
                button () {
                    addClass(playerButtons)
                    graphic = FontAwesomeIconView(FontAwesomeIcon.BACKWARD, "2em").apply { fill = c("#3C447F")  }
                    action {
                        if ((Playback.player?.currentTime?.toSeconds() ?: 0.0) <= 1.0) {
                            PrimaryViewModel.getPrevious()?.apply {
                                Playback.saveProgress()
                                startPlayback()
                            }
                        } else {
                            Playback.player?.seek(Duration.ZERO)
                        }
                    }
                }
                button () {
                    addClass(playerButtons)
                    action {
                        Playback.rewind15()
                    }
                    textFill = midHigh
                    text = "15s"
                }
                button () {
                    addClass(playerButtons)
                    visibleWhen { !Playback.isPlaying }
                    managedWhen { !Playback.isPlaying }

                    action {
                        Playback.player?.let { it.play() }
                            ?: PrimaryViewModel.getFirst()?.startPlayback()
                    }
                    graphic = FontAwesomeIconView(FontAwesomeIcon.PLAY, "2em").apply { fill = c("#4B8D1C") }
                }
                button() {
                    addClass(playerButtons)
                    visibleWhen { Playback.isPlaying }
                    managedWhen { Playback.isPlaying }
                    action {
                        Playback.player?.pause()
                    }
                    graphic = FontAwesomeIconView(FontAwesomeIcon.PAUSE, "2em").apply { fill = c("#3C7D7F")}
                }
                button () {
                    addClass(playerButtons)
                    action {
                        Playback.fastforward15()
                    }
                    textFill = midHigh
                    text = "15s"
                }
                button () {
                    addClass(playerButtons)
                    graphic = FontAwesomeIconView(FontAwesomeIcon.FORWARD, "2em").apply { fill = c("#3C447F")  }
                    action {
                        PrimaryViewModel.getNext()?.apply {
                            Playback.saveProgress()
                            startPlayback()
                        }
                    }
                }
            }
            vbox {
                hgrow = Priority.ALWAYS
                hbox {
                    text(Playback.timeText) {
                        fill = c("#FFFFFF")
                        visibleWhen(!PrimaryViewModel.isError)
                        managedWhen(!PrimaryViewModel.isError)
                    }
                    text(PrimaryViewModel.error) {
                        fill = c("#FFFFFF")
                    }
                }
                add(slider)
            }
            add<Volume>()
            progressindicator {
                visibleWhen(PrimaryViewModel.isDownloadMedia)
                managedWhen(PrimaryViewModel.isDownloadMedia)
            }
        }
    }
}