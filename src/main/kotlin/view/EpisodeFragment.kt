package view

import BaseStyle.Companion.episode
import BaseStyle.Companion.episodeButtons
import BaseStyle.Companion.highlight
import BaseStyle.Companion.playButton
import BaseStyle.Companion.primary
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import model.EpisodeModel
import tornadofx.*

class EpisodeFragment : Fragment() {
    val model: EpisodeModel by param()

    val playSymbol = FontAwesomeIconView(FontAwesomeIcon.PLAY_CIRCLE, "4em").apply { fill = c("#447F57") }

    override val root = hbox(8.0) {
        addClass(episode)
        label(graphic = playSymbol) {
            visibleWhen { parent.hoverProperty() }
        }
        text(model.pubDate) {
            fill = highlight
        }
        text(model.title) {
            wrappingWidth = 300.0
            fill = highlight
        }
        region { hgrow = Priority.ALWAYS }
        vbox (5.0) {
            text(model.duration) {
                fill = highlight
            }
            hbox(5.0) {
                button {
                    addClass(episodeButtons)
                    graphic = FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD).apply { fill = primary }
                }
                button {
                    addClass(episodeButtons)
                    graphic = FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE).apply { fill = primary }
                }
            }
        }
    }
}