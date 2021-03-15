package view

import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import model.PrimaryViewModel
import tornadofx.*
import view.fragments.CastFragment

class Home : View() {
    override val root = stackpane {
        fitToParentSize()
        scrollpane {
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            style {
                backgroundColor += primary
            }

            isFitToHeight = true
            isFitToWidth = true

            vbox(15.0) {
                hgrow = Priority.ALWAYS
                alignment = Pos.CENTER
                style {
                    padding = box(30.px, 0.px)
                }
                bindChildren(PrimaryViewModel.castScopes) {
                    find<CastFragment>(it).root
                }
            }
        }
        vbox {
            useMaxSize = true
            alignment = Pos.CENTER
            label("Checking for new episodes...") {
                style {
                    fontSize = 3.em
                    textFill = midHigh
                }
            }
            progressindicator()
            visibleWhen(PrimaryViewModel.isDownloadRss)
            managedWhen(PrimaryViewModel.isDownloadRss)
        }
    }
}
