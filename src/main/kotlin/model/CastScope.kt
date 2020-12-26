package model

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class CastScope(init: RSS) : Scope() {
    val model: SyndicationModel = SyndicationModel(init, this)
    val episodesToDisplay = observableListOf<EpisodeModel>()
    val isViewAll = SimpleBooleanProperty(false)
}