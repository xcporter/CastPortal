package view.fragments

import BaseStyle.Companion.episode
import BaseStyle.Companion.episodeButtons
import BaseStyle.Companion.highlight
import BaseStyle.Companion.primary
import controller.Playback
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import model.CastScope
import model.EpisodeModel
import tornadofx.*

class EpisodeFragment : Fragment() {
    override val scope = super.scope as CastScope
    val model: EpisodeModel by param()


    val playSymbol = FontAwesomeIconView(FontAwesomeIcon.PLAY_CIRCLE, "4em").apply { fill = c("#447F57") }
    val pauseSymbol = FontAwesomeIconView(FontAwesomeIcon.PAUSE_CIRCLE, "4em").apply { fill = c("#4B8D1C") }

    val infoButton = button {
        addClass(episodeButtons)
        graphic = FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE).apply { fill = primary }
        action {
            model.detail.value = !model.detail.value
        }
    }

    val downloadButton = button {
        addClass(episodeButtons)
        graphic = FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD).apply { fill = primary }
        action {
            if (model.isDownload.value) {
                model.removeDownload()
            } else { model.download() }
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

        model.detail.onChange {
            if (it) {
                infoButton.style {
                    backgroundColor += c("#B4955F")
                }
            } else {
                infoButton.style {
                    backgroundColor += highlight
                }
            }
        }

        if (model.isDownload.value) {
            downloadButton.style {
                backgroundColor += c("#5C7FB4")
            }
        } else {
            downloadButton.style {
                backgroundColor += highlight
            }
        }

        model.isDownload.onChange {
            if (it) {
                downloadButton.style {
                    backgroundColor += c("#5C7FB4")
                }
            } else {
                downloadButton.style {
                    backgroundColor += highlight
                }
            }
        }
    }

    override val root = vbox {
        hbox(8.0) {
            addClass(episode)
            hoverProperty().or(model.isPlaying).onChange {
                if (it) style { backgroundColor +=  c("#081F1D") }
                else style { backgroundColor += primary }
            }
            onLeftClick { model.startPlayback() }
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
                wrappingWidth = 300.0
                fill = highlight
            }
            region { hgrow = Priority.ALWAYS }
            vbox (5.0) {
                text(model.duration) {
                    fill = highlight
                }
                hbox(5.0) {
                    add(downloadButton)
                    add(infoButton)
                }
            }
        }

        hbox {
            visibleWhen(model.detail)
            managedWhen(model.detail)
            alignment = Pos.CENTER
            region {
                hgrow = Priority.ALWAYS
            }
            vbox {
                style {
                    backgroundColor += highlight
                    padding = box(20.px)
                }
                text(model.description) {
                    wrappingWidth = 500.0
                    fill = c("#000000")
                }
            }
        }
    }
}
