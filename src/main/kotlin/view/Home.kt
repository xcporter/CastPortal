package view

import BaseStyle.Companion.invisibleButtons
import BaseStyle.Companion.midHigh
import BaseStyle.Companion.primary
import BaseStyle.Companion.shadow
import BaseStyle.Companion.textColor
import CastArrangement
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import model.PrimaryViewModel
import tornadofx.*
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

    val gridButtonFill = SimpleObjectProperty<Color>(Color.ORANGE)

    val listButtonFill = SimpleObjectProperty<Color>(textColor)

    init {

        PrimaryViewModel.castArrangementState.onChange {
            it?.let {
                when(it) {
                    CastArrangement.LIST -> {
                        displayPanel.replaceChildren(listPanel)
                        listButtonFill.value = Color.ORANGE
                        gridButtonFill.value = textColor
                    }
                    CastArrangement.GRID -> {
                        displayPanel.replaceChildren(gridPanel)
                        listButtonFill.value = textColor
                        gridButtonFill.value = Color.ORANGE
                    }
                }

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
                    backgroundColor += shadow
                }
                label("Offline") {
                    visibleWhen { PrimaryViewModel.offlineMode }
                    style {
                        textFill = midHigh
                        fontSize = 2.em
                    }
                }
                region { hgrow = Priority.ALWAYS }
                button {
                    addClass(invisibleButtons)
                    graphic = FontAwesomeIconView(FontAwesomeIcon.TH, "1.8em").apply {
                        fillProperty().bind(gridButtonFill)
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
                displayPanel = vbox {
                    add(gridPanel)
                }
            }
        }
        vbox {
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
