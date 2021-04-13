package model

import controller.Configuration
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import tornadofx.*
import java.io.File

object PrimaryViewModel {
    val backgroundJobs = SupervisorJob()
    val castScopes = observableListOf<CastScope>()
    val viewState = SimpleObjectProperty(ViewState.HOME)
    val detailView = SimpleObjectProperty<CastScope>(null)
    val castArrangementState = SimpleObjectProperty(CastArrangement.GRID)
    val error = SimpleStringProperty()
    val isError = error.booleanBinding() { !it.isNullOrBlank() }

    val downloads = observableListOf<String>(File("${Configuration.path.path}/downloads/")
        .listFiles()
        ?.map { it.name.takeIf { it.length == 64 } }
        ?.filterNotNull()
        ?: listOf())

    val hasDownloads = SimpleBooleanProperty(downloads.isNotEmpty())

    val isDownloadMedia = SimpleBooleanProperty()
    val isLoadNew = SimpleBooleanProperty()
    val isDownloadRss = SimpleBooleanProperty()

    fun clearIsPlaying () {
        castScopes.forEach {
            it.model.items.forEach {
                it.isPlaying.value = false
            }
        }
    }

    fun setDetail(scope: CastScope) {
        detailView.value = scope
        viewState.value = ViewState.DETAIL
    }

    fun getFirst () = castScopes.first().model.items.firstOrNull()

    fun getNext () : EpisodeModel? = castScopes
        .find { it.model.items.any { it.isPlaying.value == true } }
        ?.model?.items?.let {
            it[((it.indexOf(it.find { it.isPlaying.value == true }) + 1) % it.size)]
        }

    fun getPrevious () : EpisodeModel? = castScopes
        .find { it.model.items.any { it.isPlaying.value == true } }
        ?.model?.items?.let {
            it[((it.indexOf(it.find { it.isPlaying.value == true }) - 1).takeIf { it >= 0} ?: 0)]
        }

    fun clearDetailsOnNonPlayingCasts() = castScopes
        .filter { !it.model.items.any { it.isPlaying.value } }
        .forEach {
            it.currentTitle.value = ""
            it.currentDescription.value = ""
        }
}