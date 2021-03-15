package view

import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import model.PrimaryViewModel
import tornadofx.*
import view.fragments.DownloadCastFragment

class Downloads : View() {
    override val root = scrollpane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        style {
            backgroundColor += primary
        }
        isFitToHeight = true
        isFitToWidth = true
        vbox {
            style {
                padding = box(18.px)
            }
            vgrow = Priority.ALWAYS
            alignment = Pos.TOP_LEFT
            label ("Nothing yet downloaded") {
                visibleWhen(!PrimaryViewModel.hasDownloads)
                managedWhen(!PrimaryViewModel.hasDownloads)
                textFill = midHigh
                style {
                    fontSize = 3.em
                }
            }
            label ("Downloads") {
                visibleWhen(PrimaryViewModel.hasDownloads)
                managedWhen(PrimaryViewModel.hasDownloads)
                textFill = midHigh
                style {
                    fontSize = 3.em
                }
            }
            vbox(15.0) {
                hgrow = Priority.ALWAYS
                useMaxSize = true
                alignment = Pos.CENTER
                style {
                    padding = box(30.px, 0.px)
                }
                bindChildren(PrimaryViewModel.castScopes) {
                    find<DownloadCastFragment>(it).root
                }
            }
        }

    }
}