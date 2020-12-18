import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

class BaseStyle : Stylesheet() {
    companion object {
        val nowPlaying by cssclass()
        val player by cssclass()
        val menu by cssclass()
        val menubuttons by cssclass()

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

        player {
            prefHeight = 80.px
            backgroundColor += midHigh
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