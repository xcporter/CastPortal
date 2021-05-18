package view.fragments

import BaseStyle.Companion.highlight
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import tornadofx.*

class Progress() : Fragment() {
    val progress: Double by param()

    override val root = stackpane {
        group {
            circle {
                centerX = 20.0
                centerY = 20.0
                radius = 10.0
                fill = Color.TRANSPARENT
                stroke = highlight
                strokeWidth = 2.0
            }
            arc {
                centerX = 20.0
                centerY = 20.0
                radiusX = 10.0
                radiusY = 10.0
                startAngle = 90.0
                length = if (progress == -1.0) { 360.0 } else -(360.0 * progress)
                fill = highlight
                type = ArcType.ROUND
            }
        }
    }

}