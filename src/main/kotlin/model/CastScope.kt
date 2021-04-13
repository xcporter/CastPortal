package model

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class CastScope(init: RSS) : Scope() {
    val model: SyndicationModel = SyndicationModel(init, this)
    val currentDescription = SimpleStringProperty()
    val currentTitle = SimpleStringProperty()
    val episodesToDisplay = observableListOf<EpisodeModel>()
}