package view

import BaseStyle.Companion.nowPlaying
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

class Empty : Fragment() {
    override val root = vbox (45.0){
        addClass(nowPlaying)
        label {
            graphic = FontAwesomeIconView(FontAwesomeIcon.PODCAST, "5em")
            style {
                padding = box(75.px)
            }
        }
    }
}