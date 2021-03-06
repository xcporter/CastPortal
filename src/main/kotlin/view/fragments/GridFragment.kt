package view.fragments

import BaseStyle.Companion.invisibleButtons
import BaseStyle.Companion.textColor
import controller.Configuration
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import kotlinx.coroutines.*
import model.CastScope
import model.PrimaryViewModel
import model.ShowDescription
import tornadofx.*
import java.io.File
import java.net.URL

class GridFragment : Fragment(), CoroutineScope {
    override val scope = super.scope as CastScope
    override val coroutineContext = Dispatchers.IO
    private val downloadsOnly: Boolean by param(false)

    val imageContainer = hbox {}

    val infoButton = button {
        addClass(invisibleButtons)
        graphic = FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE).apply { fill = textColor; size = "1.5em" }
        action {
            fire(
                ShowDescription(
                    scope.model.description.value,
                    scope.model.title.value,
                    scope.model.author.value
                )
            )
        }
    }

    override val root = vbox(5.0) {
        style {
            padding = box(10.px)
        }

        onLeftClick {
            PrimaryViewModel.setDetail(scope, downloadsOnly)
        }

        add(imageContainer)

        hbox {
            vbox(2.0) {
                text(scope.model.author) {
                    style {
                        fill = textColor
                        fontSize = 1.5.em
                    }
                    wrappingWidth = 150.0
                }
                text(scope.model.title) {
                    style {
                        fill = textColor
                        fontSize = 1.5.em
                    }
                    wrappingWidth = 150.0
                }
            }

            add(infoButton)
        }
    }

    override fun onDock() {
        launch {
            withTimeout(30000) {
                while(!File("${Configuration.path.path}/images/${scope.model.imageUrl.value}").exists() && this.isActive) {
                    delay(10)
                }
                withContext(Dispatchers.Main) {
                    imageContainer.add(
                        imageview(URL("file:///${Configuration.path.path}/images/${scope.model.imageUrl.value}").toExternalForm()) {
                            fitWidth = 170.0
                            fitHeight = 170.0
                        }
                    )
                }
            }
        }
    }
}