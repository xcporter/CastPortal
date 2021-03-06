package view

import BaseStyle.Companion.menu
import BaseStyle.Companion.menubuttons
import BaseStyle.Companion.nowPlaying
import ViewState
import controller.Playback
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import model.PrimaryViewModel
import tornadofx.*

class Menu : View() {
    var container : ImageView by singleAssign()

    override val root = vbox {
        addClass(menu)
        hbox {
            alignment = Pos.CENTER
            style {
                padding = box(10.px)
            }
            imageview("/icons/Logo_Red.png") {
                isPreserveRatio = true
                fitWidth = 150.0
            }
            onLeftClick {
                this@Menu.app.hostServices.showDocument("https://podcastfarm.co/")
            }
        }

        listmenu {
            addClass(menubuttons)
            useMaxWidth = true
            PrimaryViewModel.viewState.onChange {
                if (it == ViewState.DETAIL || it == ViewState.DETAIL_DOWNLOAD) activeItem = null
            }
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
    }
}