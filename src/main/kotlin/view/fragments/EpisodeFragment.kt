package view.fragments

import BaseStyle.Companion.episode
import BaseStyle.Companion.episodeButtons
import BaseStyle.Companion.highlight
import BaseStyle.Companion.primary
import BaseStyle.Companion.textColor
import controller.Playback
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import model.CastScope
import model.EpisodeModel
import model.PrimaryViewModel
import model.RenderDownloads
import tornadofx.*

class EpisodeFragment : Fragment() {
    override val scope = super.scope as CastScope
    val model: EpisodeModel by param()

    val playSymbol = FontAwesomeIconView(FontAwesomeIcon.PLAY_CIRCLE, "4em").apply { fill = c("#447F57") }
    val pauseSymbol = FontAwesomeIconView(FontAwesomeIcon.PAUSE_CIRCLE, "4em").apply { fill = c("#4B8D1C") }

    val container = vbox {
        add(find<Progress>(mapOf("progress" to model.progress.value)))
    }

    val downloadButton = button {
        addClass(episodeButtons)
        graphic = FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD).apply { fill = primary }
        action {
            if (model.isDownload.value) {
                model.removeDownload()
            } else { model.download() }
            fire(RenderDownloads())
        }
    }

    init {
        model.isPlaying.onChange {
            if (it) {
                playSymbol.fill = c("#4B8D1C")
            } else {
                playSymbol.fill = c("#447F57")
            }
        }

        if (model.isDownload.value) {
            downloadButton.style {
                backgroundColor += c("#5C7FB4")
            }
        } else {
            downloadButton.style {
                backgroundColor += textColor
            }
        }

        model.isDownload.onChange {
            if (it) {
                downloadButton.style {
                    backgroundColor += c("#5C7FB4")
                }
            } else {
                downloadButton.style {
                    backgroundColor += textColor
                }
            }
        }

        model.progress.onChange {
            container.replaceChildren {
               add(find<Progress>(mapOf("progress" to model.progress.value)))
            }
        }
    }

    override val root = hbox(8.0) {
        addClass(episode)
        hgrow = Priority.ALWAYS
        hoverProperty().or(model.isPlaying).onChange {
            if (it) style { backgroundColor +=  c("#081F1D") }
            else style { backgroundColor += primary }
        }
        onLeftClick { model.startPlayback() }
        add(container)
        label(graphic = playSymbol) {
            visibleWhen { (model.isPlaying.or(parent.hoverProperty())).and(!Playback.isPlaying) }
            managedWhen { !Playback.isPlaying }
        }
        label(graphic = pauseSymbol) {
            visibleWhen { model.isPlaying.and(Playback.isPlaying) }
            managedWhen { Playback.isPlaying }
        }
        text(model.pubDate) {
            fill = highlight
        }
        text(model.title) {
            fill = highlight
            wrappingWidthProperty().bind(PrimaryViewModel.contentWidth.multiply(0.3))
        }
        region { hgrow = Priority.ALWAYS }
        vbox (5.0) {
            text(model.duration) {
                fill = highlight
            }
            hbox(5.0) {
                alignment = Pos.CENTER_RIGHT
                add(downloadButton)
            }
        }
    }

}
