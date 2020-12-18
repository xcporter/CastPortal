import tornadofx.*
import tornadofx.Stylesheet.Companion.root

class BaseStyle : Stylesheet() {
    companion object {
        val nowPlaying by cssclass()
        val player by cssclass()
        val menu by cssclass()
        val textColor = c("#FFFFFF")
        val menubuttons by cssclass()
    }
    init {
        root {
            prefWidth = 1000.px
            prefHeight = 700.px
            backgroundColor += c("#3C3C3F")
        }

        nowPlaying {
            prefWidth = 200.px
            prefHeight = 200.px
            backgroundColor += c("#59595C")
        }

        player {
            prefHeight = 80.px
            backgroundColor += c("#828285")
        }

        menu {
            backgroundColor += c("#2B2B2E")
        }

        menubuttons {
            importStylesheet("/style/custommenu.css")
            fontSize = 1.5.em
        }
    }
}