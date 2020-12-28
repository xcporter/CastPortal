package view

import controller.Configuration
import tornadofx.*
import java.io.File

class Warning : View() {
    override val root = vbox(10.0) {
        style {
            padding = box(10.px)
        }
        label("Warning, this will remove all downloads and saved RSS streams")
        hbox(10.0) {
            button("Ok") {
                action {
                    File(Configuration.path.path).deleteRecursively()
                    close()
                }
            }
            button("Cancel") {
                action {
                    close()
                }
            }
        }
    }
}