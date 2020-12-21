package view

import BaseStyle.Companion.nowPlaying
import BaseStyle.Companion.shadow
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import tornadofx.*

class Empty : Fragment() {
    override val root = vbox (45.0){
        addClass(nowPlaying)
        label {
            graphic = FontAwesomeIconView(FontAwesomeIcon.PODCAST, "5em").apply { fill = shadow }
            style {
                padding = box(75.px)
            }
        }
    }
}