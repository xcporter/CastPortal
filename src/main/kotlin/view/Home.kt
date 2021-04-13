package view

import BaseStyle.Companion.highlight
import BaseStyle.Companion.invisibleButtons
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import model.PrimaryViewModel
import model.ShowDescription
import tornadofx.*
import view.fragments.CastFragment
import view.fragments.CastInfoFragment
import view.fragments.GridFragment
import view.fragments.ListFragment

class Home : View() {

    var displayPanel by singleAssign<VBox>()

    val gridPanel = flowpane {
        style {
            padding = box(20.px)
        }
        bindChildren(PrimaryViewModel.castScopes) {
            find<GridFragment>(it).root
        }
    }

    val listPanel = vbox {
        style {
            padding = box(20.px)
        }
        bindChildren(PrimaryViewModel.castScopes) {
            find<ListFragment>(it).root
        }
    }

    init {
        subscribe<ShowDescription> {
            openInternalWindow<CastInfoFragment>(
                icon = hbox {
                    style { padding = box(0.px, 10.px) }
                    text("${it.title} by ${it.author}") {
                        style { fontSize = 2.em }
                        wrappingWidth = 400.0
                    }
                },
                movable = false,
                params = mapOf("info" to it.description)
            )
        }

        PrimaryViewModel.castArrangementState.onChange {
            it?.let {
                displayPanel.replaceChildren (
                    when(it) {
                        CastArrangement.LIST -> listPanel
                        CastArrangement.GRID -> gridPanel
                    }
                )
            }
        }
    }

    override val root = stackpane {
        fitToParentSize()
        vbox {
            hbox {
                useMaxWidth = true
                alignment = Pos.CENTER_RIGHT
                style {
                    padding = box(5.px, 10.px)
                }
                button {
                    addClass(invisibleButtons)
                    graphic = cache {
                        vbox(2.0) {
                            repeat(3) {
                                hbox(2.0) {
                                    repeat(3) { rectangle(0, 0, 5.0, 5.0) { fill = highlight } }
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
                        fill = highlight
                    }
                    action {
                        PrimaryViewModel.castArrangementState.value = CastArrangement.LIST
                    }
                }
                visibleWhen(!PrimaryViewModel.isDownloadRss)
                managedWhen(!PrimaryViewModel.isDownloadRss)
            }
            scrollpane {
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
                style {
                    backgroundColor += primary
                }

                alignment = Pos.CENTER

                isFitToHeight = true
                isFitToWidth = true
                displayPanel = vbox{
                    add(gridPanel)
                }
            }
        }
        vbox {
            useMaxSize = true
            alignment = Pos.CENTER
            label("Checking for new episodes...") {
                style {
                    fontSize = 3.em
                    textFill = midHigh
                }
            }
            progressindicator()
            visibleWhen(PrimaryViewModel.isDownloadRss)
            managedWhen(PrimaryViewModel.isDownloadRss)
        }
    }
}
