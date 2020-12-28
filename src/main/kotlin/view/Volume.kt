package view

import BaseStyle.Companion.highlight
import BaseStyle.Companion.playerButtons
import controller.Playback
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Orientation
import javafx.geometry.Pos
import tornadofx.*

class Volume : View() {

    override val root = hbox (6.0){
        style {
            padding = box(10.px)
        }
        alignment = Pos.CENTER
        button {
            visibleWhen(!Playback.isMute)
            managedWhen(!Playback.isMute)
            addClass(playerButtons)
            graphic = FontAwesomeIconView(FontAwesomeIcon.VOLUME_UP).apply {
                size = "2em"
                fill = highlight
            }
            action { Playback.isMute.value = !Playback.isMute.value }
        }
        button {
            visibleWhen(Playback.isMute)
            managedWhen(Playback.isMute)
            addClass(playerButtons)
            graphic = FontAwesomeIconView(FontAwesomeIcon.VOLUME_OFF).apply {
                size = "2em"
                fill = highlight
            }
            action { Playback.isMute.value = !Playback.isMute.value }
        }
        slider(0.0, 1.0, 1.0) {
            prefWidth = 50.0
            valueProperty().bindBidirectional(Playback.volumeProperty)
        }
    }
}