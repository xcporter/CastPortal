package view.fragments

import BaseStyle.Companion.highlight
import BaseStyle.Companion.invisibleButtons
import BaseStyle.Companion.mid
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import controller.Configuration
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
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

    private val reverse = SimpleBooleanProperty(false)

    private val searchFilter = SimpleStringProperty("")

    val sortUpFill = SimpleObjectProperty<Color>(Color.ORANGE)
    val sortDownFill = SimpleObjectProperty<Color>(highlight)


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
                sortUpFill.value = highlight
            }
            else {
                items.setAll(
                    if (!downloadsOnly) scope.model.items
                    else scope.model.items.filter { it.isDownload.value }
                )
                sortDownFill.value = highlight
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
            vbox(5.0) {
                hbox(5.0) {
                    alignment = Pos.CENTER_RIGHT
                    style {
                        padding = box(0.px, 10.px)
                    }
                    label("Sort By:") {
                        style {
                            fontSize = 1.5.em
                            textFill = midHigh
                        }
                    }
                    button("New") {
                        addClass(invisibleButtons)
                        style {
                            fontSize = 1.5.em
                            textFillProperty().bind(sortUpFill)
                        }
                        action {
                            reverse.value = false
                        }
                    }
                    button("Old") {
                        addClass(invisibleButtons)
                        style {
                            fontSize = 1.5.em
                            textFillProperty().bind(sortDownFill)
                        }
                        action {
                            reverse.value = true
                        }
                    }
                    region { hgrow = Priority.ALWAYS }
                    label("Search:") {
                        style {
                            fontSize = 1.5.em
                            textFill = midHigh
                        }
                    }
                    textfield (searchFilter) {
                        maxWidth = 200.0
                        style {
                            backgroundColor += mid
                            textFill = highlight
                        }
                    }
                }

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
