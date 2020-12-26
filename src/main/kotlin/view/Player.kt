package view

import BaseStyle.Companion.highlight
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.player
import BaseStyle.Companion.playerButtons
import controller.CastView
import controller.Playback
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.InvalidationListener
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.util.Duration
import kotlinx.coroutines.*
import tornadofx.*
import kotlin.coroutines.CoroutineContext

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
                            CastView.getPrevious()?.startPlayback()
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
                            ?: CastView.getFirst()?.startPlayback()
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
                        CastView.getNext()?.startPlayback()
                    }
                }
            }
            vbox {
                hgrow = Priority.ALWAYS
                text(Playback.timeText) {
                    fill = c("#FFFFFF")
                }
                add(slider)
            }
        }
    }
}