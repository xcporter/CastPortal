import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

class BaseStyle : Stylesheet() {
    companion object {
        val nowPlaying by cssclass()
        val player by cssclass()
        val menu by cssclass()
        val menubuttons by cssclass()
        val episodeButtons by cssclass()
        val episode by cssclass()
        val playButton by cssclass()
        val moreEpisodes by cssclass()

        val textColor = c("#FFFFFF")
        val primary = c("#3C3C3F")
        val mid = c("#59595C")
        val midHigh = c("#828285")
        val highlight = c("#CFCFD2")
        val shadow = c("#2B2B2E")
    }
    init {
        importStylesheet("/style/custom.css")

        root {
            prefWidth = 1000.px
            prefHeight = 700.px
            backgroundColor += primary
        }

        nowPlaying {
            prefWidth = 200.px
            prefHeight = 200.px
            backgroundColor += mid
//            maybe for podcast cards
//            effect = DropShadow(8.0, 6.0, 6.0, c("#00000044"))
        }

        episodeButtons {
            borderRadius += box(0.px)
            backgroundColor += highlight
            textFill = highlight
            and(hover) {
                backgroundColor += midHigh
            }
        }

        playButton {
            borderRadius += box(0.px)
            backgroundColor += highlight
            textFill = highlight
            and(hover) {
                backgroundColor += Color.GREEN
            }
        }

        episode {
            backgroundColor += primary
            padding = box(8.px)
            and(hover) {
                backgroundColor += c("#081F1D")
            }
        }

        moreEpisodes {
            fontSize = 2.em
            backgroundColor += Color.TRANSPARENT
            borderRadius += box(4.px)
            backgroundRadius += box(4.px)
            backgroundInsets += box(2.px)
            borderWidth += box(2.px)
            borderColor += box(primary)
            textFill = shadow

            and (hover) {
                backgroundColor += highlight
            }

            and (pressed) {
                backgroundColor += Color.WHITE
            }
        }

        player {
            prefHeight = 80.px
            backgroundColor += shadow
        }

        menu {
            backgroundColor += shadow
        }

        menubuttons {
            fontSize = 1.5.em
        }

        scrollBar {
            backgroundColor += primary // transparent
            backgroundInsets += box(0.px)
            thumb {
                backgroundColor += highlight
                backgroundRadius += box(8.px, 8.px)
                backgroundInsets += box(0.px, 4.px)
            }
            incrementArrow {
                shape = " "
            }
            decrementArrow {
                shape = " "
            }
        }
    }
}