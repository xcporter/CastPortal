package view

import BaseStyle.Companion.highlight
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.moreEpisodes
import BaseStyle.Companion.primary
import BaseStyle.Companion.shadow
import controller.Configuration
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.*
import model.EpisodeModel
import model.SyndicationModel
import tornadofx.*
import java.io.File

class CastFragment() : Fragment(), CoroutineScope {
    val model: SyndicationModel by param()
    override val coroutineContext = Dispatchers.IO

    val episodesToDisplay = observableListOf<EpisodeModel>()

    var imageContainer: HBox by singleAssign()

    val isViewAll = SimpleBooleanProperty(false)

    init {
        isViewAll.onChange {
            if (it) {
                episodesToDisplay.addAll(model.items.drop(Configuration.displayNumberOfEpisodes))
            } else {
                episodesToDisplay.remove((Configuration.displayNumberOfEpisodes - 1), episodesToDisplay.size)
            }
        }
    }

    override val root = vbox(10.0){
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
                    text(model.title) {
                        style {
                            fontSize = 4.em
                            fill = highlight
                        }
                        wrappingWidth = 400.0
                    }
                    text(model.author) {
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
                text(model.description) {
                    wrappingWidth = 450.0
                }
            }
        }
        vbox(12.0)  {
            fitToParentSize()
            vbox(4.0) {
                bindChildren(episodesToDisplay) {
                    find<EpisodeFragment>("model" to it).root
                }
            }
            hbox {
                alignment = Pos.CENTER
                button("view all episodes") {
                    textProperty().bind(isViewAll.stringBinding { if (it == true) "view fewer episodes" else "view all episodes" })
                    addClass(moreEpisodes)
                    action {
                        isViewAll.value = !isViewAll.value
                    }
                }
            }
        }
    }

    override fun onDock() {
        episodesToDisplay.addAll(model.items.take(Configuration.displayNumberOfEpisodes))

//        wait for image files to download before building UI
        launch {
            withTimeout(30000) {
                while(!File("${Configuration.path.path}/images/${model.imageUrl.value}").exists() && this.isActive) {
                    delay(10)
                }
                withContext(Dispatchers.Main) {
                    imageContainer.add(
                        imageview("file://${Configuration.path.path}/images/${model.imageUrl.value}") {
                            fitWidth = 150.0
                            fitHeight = 150.0
                        }
                    )
                }
            }
        }
    }
}
