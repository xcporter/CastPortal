package view

import BaseStyle.Companion.primary
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import tornadofx.*

class Primary() : View ("Cast Portal") {
    override val root = borderpane {
        left<Menu>()
        center = vbox {

            scrollpane {
                isFitToWidth = true
                isFitToHeight = true
                flowpane() {
                    style {
                        padding = box (30.px)
                        backgroundColor += primary
                    }
                    hgap = 20.0
                    vgap = 20.0
                    alignment = Pos.CENTER
                    for (i in 1..43) { add(Empty::class) }
                }
            }

        }


        bottom<Player>()
    }
}