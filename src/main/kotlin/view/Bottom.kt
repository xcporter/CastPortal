package view

import BaseStyle.Companion.player
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class Bottom : View() {
    override val root = vbox {
        addClass(player)
        region { vgrow = Priority.ALWAYS }
        hbox {
            style = """
                -fx-border-style: solid none none none;
                -fx-border-color: orange;
                -fx-border-width: 2;
                -fx-padding: 2px;
            """
            region { hgrow = Priority.ALWAYS }
            text("©2020 Podcast Farm Inc. - All Rights Reserved") {
                style {
                    fill = Color.WHITE
                    fontSize = 10.px
                }
            }
        }
    }
}