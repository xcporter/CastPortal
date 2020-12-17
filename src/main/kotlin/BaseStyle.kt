import tornadofx.*
import tornadofx.Stylesheet.Companion.root

class BaseStyle : Stylesheet() {
    companion object {

    }
    init {
        root {
           prefWidth = 1000.px
           prefHeight = 700.px
        }
    }
}