package view

import BaseStyle.Companion.menu
import BaseStyle.Companion.menubuttons
import BaseStyle.Companion.nowPlaying
import BaseStyle.Companion.textColor
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.*

class Menu : View() {
    override val root = vbox {
        addClass(menu)
        hbox {
            alignment = Pos.CENTER
            text("[YOUR LOGO HERE]") {
                style {
                    fontSize = 18.px
                    fill = c("#59595C")
                }
            }
        }

        listmenu {
            addClass(menubuttons)
            useMaxWidth = true
            item("Home", FontAwesomeIconView(FontAwesomeIcon.HOME, "2em"))
            item("Downloads", FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD, "2em"))
            item("Settings", FontAwesomeIconView(FontAwesomeIcon.GEAR, "2em"))
        }
        region { vgrow = Priority.ALWAYS }
        add(Empty::class)
    }
}