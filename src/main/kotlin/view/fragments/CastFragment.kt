package view.fragments

import BaseStyle.Companion.invisibleButtons
import BaseStyle.Companion.mid
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import BaseStyle.Companion.textColor
import ViewState
import controller.Configuration
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
    private val epList: VBox by singleAssign()

    private val reverse = SimpleBooleanProperty(false)

    private val searchFilter = SimpleStringProperty("")

    val sortUpFill = SimpleObjectProperty<Color>(Color.ORANGE)
    val sortDownFill = SimpleObjectProperty<Color>(textColor)


    var imageContainer = hbox {}

    val items = observableListOf<EpisodeModel>(if(!downloadsOnly) scope.model.items else scope.model.items.filter { it.isDownload.value })

    init {
        subscribe<RenderDownloads> {
            items.setAll(if(!downloadsOnly) scope.model.items else scope.model.items.filter { it.isDownload.value })
            if (items.isEmpty()) PrimaryViewModel.viewState.value = ViewState.DOWNLOADS
        }

        searchFilter.onChange { search ->
            if(!search.isNullOrBlank()) {
                items.setAll(
                    items.filter { it.title.value.contains(search, true) || it.pubDate.value.contains(search, true) }
                )
            } else {
                items.setAll(
                    if(!downloadsOnly) scope.model.items
                    else scope.model.items.filter { it.isDownload.value }
                )
            }
        }

        reverse.onChange {
            if (it) {
                items.reverse()
                sortDownFill.value = Color.ORANGE
                sortUpFill.value = textColor
            }
            else {
                items.setAll(
                    if (!downloadsOnly) scope.model.items
                    else scope.model.items.filter { it.isDownload.value }
                )
                sortDownFill.value = textColor
                sortUpFill.value = Color.ORANGE
            }
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
                        fill = textColor
                        fontSize = 1.em
                    }
                    wrappingWidthProperty().bind(widthProperty().minus(100.0))
                }
                text(scope.model.title) {
                    style {
                        fill = textColor
                        fontSize = 2.em
                    }
                    wrappingWidthProperty().bind(widthProperty().minus(100.0))
                }
                scrollpane {
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
                    isFitToWidth = true
                    minViewportWidth = 100.0
                    minWidth = 100.0
                    style {
                        backgroundColor += primary
                        padding = box(5.px, 10.px)
                    }
                    text(scope.model.description) {
                        style {
                            fill = textColor
                        }
                        wrappingWidthProperty().bind(widthProperty().minus(100.0))
                    }
                }
            }
        }

        hbox {
            alignment = Pos.CENTER_RIGHT
            vbox() {
                prefWidthProperty().bind(PrimaryViewModel.contentWidth.multiply(0.33))
                visibleWhen {
                    PrimaryViewModel.width.greaterThan(910.0)
                }
                managedWhen {
                    PrimaryViewModel.width.greaterThan(910.0)
                }
                style {
                    padding = box(10.px)
                }
                hgrow = Priority.ALWAYS
                scrollpane {
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
                    minViewportWidth = 100.0
                    style {
                        backgroundColor += primary
                    }
                    vbox(5.0) {
                        text(scope.currentTitle) {
                            style {
                                fill = textColor
                                fontSize = 1.5.em
                            }
                            wrappingWidthProperty().bind(PrimaryViewModel.contentWidth.multiply(0.25))
                        }
                        text(scope.currentDescription) {
                            style {
                                fill = textColor
                                fontSize = 1.em
                            }
                            wrappingWidthProperty().bind(PrimaryViewModel.contentWidth.multiply(0.22))
                        }
                    }
                }
            }
            vbox(5.0) {
                hgrow = Priority.ALWAYS
                hbox(5.0) {
                    alignment = Pos.CENTER_RIGHT
                    style {
                        padding = box(0.px, 10.px)
                    }
                    label("Sort By:") {
                        style {
                            fontSize = 1.3.em
                            textFill = midHigh
                        }
                    }
                    button("New") {
                        addClass(invisibleButtons)
                        style {
                            fontSize = 1.3.em
                            textFillProperty().bind(sortUpFill)
                        }
                        action {
                            reverse.value = false
                        }
                    }
                    button("Old") {
                        addClass(invisibleButtons)
                        style {
                            fontSize = 1.3.em
                            textFillProperty().bind(sortDownFill)
                        }
                        action {
                            reverse.value = true
                        }
                    }
                    region {
                        hgrow = Priority.ALWAYS
                    }
                    label("Search:") {
                        style {
                            fontSize = 1.3.em
                            textFill = midHigh
                        }
                    }
                    textfield (searchFilter) {
                        maxWidth = 200.0
                        minWidth = 50.0
                        style {
                            backgroundColor += mid
                            textFill = textColor
                        }
                    }
                }

                scrollpane {
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    minViewportWidth = 100.0
                    minWidth = 100.0
                    isFitToWidth = true
                    style {
                        backgroundColor += primary
                    }
                    vbox {
                        hgrow = Priority.ALWAYS
                        style {
                            padding = box(0.px, 10.px)
                        }
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
