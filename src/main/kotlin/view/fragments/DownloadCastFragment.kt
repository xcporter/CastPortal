package view.fragments

import BaseStyle.Companion.highlight
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import controller.Configuration
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.layout.HBox
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.*
import model.CastScope
import tornadofx.*
import java.io.File

class DownloadCastFragment: Fragment(), CoroutineScope {
    override val scope = super.scope as CastScope
    override val coroutineContext = Dispatchers.IO

    var imageContainer: HBox by singleAssign()

    override val root = vbox(10.0) {

        visibleWhen(scope.model.hasDownloads)
        managedWhen(scope.model.hasDownloads)

        maxWidth = 500.0
        style {
            padding = box(30.px)
            backgroundColor += midHigh
            effect = DropShadow(10.0, 6.0, 8.0, c("#000000AA") )
        }
        vbox(12.0) {
            fitToParentSize()
            style {
                padding = box(10.px)
                borderWidth += box(0.px, 0.px, 2.px, 0.px)
                borderColor += box(c("#000000AA"))
            }
            imageContainer = hbox {
                vbox {
                    style {
                        padding = box(0.px, 16.px)
                    }
                    text(scope.model.title) {
                        style {
                            fontSize = 4.em
                            fill = highlight
                        }
                        wrappingWidth = 400.0
                    }
                    text(scope.model.author) {
                        style {
                            fontSize = 3.em
                            fill = primary
                        }
                        textAlignment = TextAlignment.RIGHT
                        wrappingWidth = 400.0
                    }
                }
            }

            hbox {
                alignment = Pos.CENTER
                text(scope.model.description) {
                    wrappingWidth = 450.0
                }
            }
        }
        vbox(4.0) {
            bindChildren(scope.model.downloaded) {
                find<EpisodeFragment>(scope, "model" to it).root
            }
        }
    }

    override fun onDock() {
//        wait for image files to download before building UI
        launch {
            withTimeout(30000) {
                while(!File("${Configuration.path.path}/images/${scope.model.imageUrl.value}").exists() && this.isActive) {
                    delay(10)
                }
                withContext(Dispatchers.Main) {
                    imageContainer.add(
                        imageview("file://${Configuration.path.path}/images/${scope.model.imageUrl.value}") {
                            fitWidth = 150.0
                            fitHeight = 150.0
                        }
                    )
                }
            }
        }
    }
}