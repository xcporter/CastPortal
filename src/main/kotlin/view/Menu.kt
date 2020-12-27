package view

import BaseStyle.Companion.menu
import BaseStyle.Companion.menubuttons
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.nowPlaying
import BaseStyle.Companion.textColor
import controller.Playback
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import model.PrimaryViewModel
import tornadofx.*

class Menu : View() {
    var container : ImageView by singleAssign()

    override val root = vbox(10.0) {
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
            item("Home", FontAwesomeIconView(FontAwesomeIcon.HOME, "2em")) {
                whenSelected { PrimaryViewModel.viewState.value = ViewState.HOME }
            }
            item("Downloads", FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD, "2em")) {
                whenSelected { PrimaryViewModel.viewState.value = ViewState.DOWNLOADS }
            }
            item("Settings", FontAwesomeIconView(FontAwesomeIcon.GEAR, "2em")) {
                whenSelected { PrimaryViewModel.viewState.value = ViewState.SETTINGS }
            }
        }

        region { vgrow = Priority.ALWAYS }

        container = imageview {
            addClass(nowPlaying)
            imageProperty().bind(Playback.image)
            fitHeight = 200.0
            fitWidth = 200.0
        }
        vbox(5.0) {
            text(Playback.titleText) {
                fill = c("#FFFFFF")
                wrappingWidth = 200.0
            }
            text(Playback.authorText) {
                fill = c("#FFFFFF")
                textAlignment = TextAlignment.RIGHT
                wrappingWidth = 200.0
            }
        }
    }
}