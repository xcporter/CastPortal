package view

import BaseStyle.Companion.highlight
import BaseStyle.Companion.invisibleButtons
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import CastArrangement
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import model.CastScope
import model.PrimaryViewModel
import model.RenderDownloads
import tornadofx.*
import view.fragments.GridFragment
import view.fragments.ListFragment

class Downloads : View() {
    var displayPanel by singleAssign<VBox>()

    val castScopesWithDownloads = observableListOf<CastScope>()

    val gridPanel = flowpane {
        style {
            padding = box(20.px)
        }
        bindChildren(castScopesWithDownloads) {
            find<GridFragment>(it, mapOf("downloadsOnly" to true)).root
        }
    }

    val listPanel = vbox {
        style {
            padding = box(20.px)
        }
        bindChildren(castScopesWithDownloads) {
            find<ListFragment>(it, mapOf("downloadsOnly" to true)).root
        }
    }

    val gridButtonFill = SimpleObjectProperty<Color>(Color.ORANGE)

    val listButtonFill = SimpleObjectProperty<Color>(highlight)

    init {
        PrimaryViewModel.castArrangementState.onChange {
            it?.let {
                when(it) {
                    CastArrangement.LIST -> {
                        displayPanel.replaceChildren(listPanel)
                        listButtonFill.value = Color.ORANGE
                        gridButtonFill.value = highlight
                    }
                    CastArrangement.GRID -> {
                        displayPanel.replaceChildren(gridPanel)
                        listButtonFill.value = highlight
                        gridButtonFill.value = Color.ORANGE
                    }
                }

            }
        }

        subscribe<RenderDownloads> {
            castScopesWithDownloads.clear()
            castScopesWithDownloads.addAll(PrimaryViewModel.castScopes.filter { it.model.hasDownloads.value })
        }
    }

    override val root = scrollpane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        style {
            backgroundColor += primary
        }
        isFitToHeight = true
        isFitToWidth = true
        vgrow = Priority.ALWAYS
        vbox {
            style {
                padding = box(18.px)
            }
            vgrow = Priority.ALWAYS
            alignment = Pos.TOP_LEFT
            label ("Nothing yet downloaded") {
                visibleWhen(!PrimaryViewModel.hasDownloads)
                managedWhen(!PrimaryViewModel.hasDownloads)
                textFill = midHigh
                style {
                    fontSize = 3.em
                }
            }
            label ("Downloads") {
                visibleWhen(PrimaryViewModel.hasDownloads)
                managedWhen(PrimaryViewModel.hasDownloads)
                textFill = midHigh
                style {
                    fontSize = 3.em
                }
            }
            stackpane {
                visibleWhen(PrimaryViewModel.hasDownloads)
                managedWhen(PrimaryViewModel.hasDownloads)
                fitToParentSize()
                vbox {
                    hbox {
                        useMaxWidth = true
                        alignment = Pos.CENTER_RIGHT
                        style {
                            padding = box(5.px, 10.px)
                        }
                        region { hgrow = Priority.ALWAYS }
                        button {
                            addClass(invisibleButtons)
                            graphic = cache {
                                vbox(2.0) {
                                    repeat(3) {
                                        hbox(2.0) {
                                            repeat(3) { rectangle(0, 0, 5.0, 5.0) { fillProperty().bind(gridButtonFill) } }
                                        }
                                    }
                                }
                            }
                            action {
                                PrimaryViewModel.castArrangementState.value = CastArrangement.GRID
                            }
                        }
                        button {
                            addClass(invisibleButtons)
                            graphic = FontAwesomeIconView(FontAwesomeIcon.LIST_UL, "1.8em").apply {
                                fillProperty().bind(listButtonFill)
                            }
                            action {
                                PrimaryViewModel.castArrangementState.value = CastArrangement.LIST
                            }
                        }
                    }
                    scrollpane {
                        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                        vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS

                        vgrow = Priority.ALWAYS
                        style {
                            backgroundColor += primary
                        }

                        isFitToHeight = true
                        isFitToWidth = true
                        displayPanel = vbox {
                            add(gridPanel)
                        }
                    }
                }
            }
        }

    }

    override fun onDock() {
        castScopesWithDownloads.setAll(PrimaryViewModel.castScopes.filter { it.model.hasDownloads.value })
    }
}