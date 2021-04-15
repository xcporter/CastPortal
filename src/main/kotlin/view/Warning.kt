package view

import controller.Configuration
import model.PrimaryViewModel
import tornadofx.*
import java.io.File

class Warning : View() {
    override val root = vbox(10.0) {
        style {
            padding = box(10.px)
        }
        label(PrimaryViewModel.warning)
        hbox(10.0) {
            button("Ok") {
                action {
                    PrimaryViewModel.warnAction?.invoke()
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

    override fun onUndock() {
        PrimaryViewModel.warnAction = null
    }
}