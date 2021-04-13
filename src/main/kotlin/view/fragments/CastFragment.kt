package view.fragments

import BaseStyle.Companion.highlight
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import controller.Configuration
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import kotlinx.coroutines.*
import model.CastScope
import model.EpisodeModel
import model.PrimaryViewModel
import model.RenderDownloads
import tornadofx.*
import java.io.File
import java.net.URL

class CastFragment() : Fragment(), CoroutineScope {
    override val scope = super.scope as CastScope
    override val coroutineContext = Dispatchers.IO
    private val downloadsOnly: Boolean by param(false)

    var imageContainer = hbox {}

    val items = observableListOf<EpisodeModel>(if(!downloadsOnly) scope.model.items else scope.model.items.filter { it.isDownload.value })

    init {
        subscribe<RenderDownloads> {
            items.setAll(if(!downloadsOnly) scope.model.items else scope.model.items.filter { it.isDownload.value })
            if (items.isEmpty()) PrimaryViewModel.viewState.value = ViewState.DOWNLOADS
        }
    }


    override val root = vbox(10.0){
        vgrow = Priority.ALWAYS
        label("Offline") {
            visibleWhen { PrimaryViewModel.offlineMode }
            managedWhen { PrimaryViewModel.offlineMode }
            style {
                textFill = midHigh
                fontSize = 2.em
                padding = box(5.px, 10.px)
            }
        }
        hbox(10.0) {
            style {
                padding = box(10.px)
            }
            add(imageContainer)
            vbox {
                hgrow = Priority.ALWAYS
                text(scope.model.author) {
                    style {
                        fill = highlight
                        fontSize = 1.em
                    }
                    wrappingWidth = 450.0
                }
                text(scope.model.title) {
                    style {
                        fill = highlight
                        fontSize = 2.em
                    }
                    wrappingWidth = 450.0

                }
                scrollpane {
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
                    fitToParentSize()
                    style {
                        backgroundColor += primary
                        padding = box(5.px, 10.px)
                    }
                    vbox {
                        text(scope.model.description) {
                            style {
                                fill = highlight
                            }
                            wrappingWidth = 500.0
                        }
                    }
                }
            }
        }

        hbox {
            alignment = Pos.CENTER_RIGHT
            vbox() {
                style {
                    padding = box(10.px)
                }
                hgrow = Priority.ALWAYS
                scrollpane {
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
                    style {
                        backgroundColor += primary
                    }
                    vbox(5.0) {
                        text(scope.currentTitle) {
                            style {
                                fill = highlight
                                fontSize = 1.5.em
                            }
                            wrappingWidth = 200.0
                        }
                        text(scope.currentDescription) {
                            style {
                                fill = highlight
                                fontSize = 1.em
                            }
                            wrappingWidth = 200.0
                        }
                    }
                }
            }
            vbox {
                scrollpane {
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    minViewportWidth = 500.0
                    style {
                        backgroundColor += primary
                    }
                    vbox {
                        hgrow = Priority.ALWAYS
                        bindChildren(items) {
                            find<EpisodeFragment>(scope, "model" to it).root
                        }
                    }
                }
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
                        imageview(URL("file:///${Configuration.path.path}/images/${scope.model.imageUrl.value}").toExternalForm()) {
                            fitWidth = 150.0
                            fitHeight = 150.0
                        }
                    )
                }
            }
        }
    }
}
