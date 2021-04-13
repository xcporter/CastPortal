package view.fragments

import model.CastScope
import tornadofx.*

class CastInfoFragment : Fragment() {
    private val info: String by param()
    override val root = vbox {
        style {
            padding = box(10.px)
        }
        text (info) {
            wrappingWidth = 400.0
        }
    }
}