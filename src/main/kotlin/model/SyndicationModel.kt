package model

import controller.Encoder.fileNameEncode
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class SyndicationModel (init: RSS, override val scope: CastScope) : ViewModel() {
    val title = SimpleStringProperty(init.channel?.title)
    val author = SimpleStringProperty(init.channel?.author)
    val description = SimpleStringProperty(init.channel?.description)
    val imageUrl = SimpleStringProperty(init.channel?.image?.href?.fileNameEncode())

    val items = observableListOf(init.channel?.items?.map { EpisodeModel(it, scope) } ?: listOf())
    val downloaded = observableListOf(init.channel?.items?.map { EpisodeModel(it, scope) }?.filter { it.isDownload.value } ?: listOf())

    val hasDownloads = booleanBinding(downloaded) { isNotEmpty() }

    init {
        hasDownloads.onChange {
            if (it) PrimaryViewModel.hasDownloads.value = true
            else PrimaryViewModel.hasDownloads.value = PrimaryViewModel.castScopes.any { it.model.hasDownloads.value }
        }
    }
}