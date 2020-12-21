package model

import controller.Encoder.formatDate
import controller.Encoder.formatTime
import javafx.beans.property.SimpleStringProperty

class EpisodeModel(init: RssItem) {
    val title = SimpleStringProperty(init.title)
    val author = SimpleStringProperty(init.author)
    val description = SimpleStringProperty(init.description)
    val duration = SimpleStringProperty(init.duration.formatTime())
    val pubDate = SimpleStringProperty(init.pubDate.formatDate())
}