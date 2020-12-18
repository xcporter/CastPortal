package view

import BaseStyle.Companion.menu
import BaseStyle.Companion.menubuttons
import BaseStyle.Companion.nowPlaying
import BaseStyle.Companion.textColor
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.scene.layout.Priority
import tornadofx.*

class Primary() : View ("Cast Portal") {
    override val root = borderpane {
        left = find<Left>().root
        center = text("This is a test") {
            fill = textColor
        }

        bottom = find<Bottom>().root
    }
}