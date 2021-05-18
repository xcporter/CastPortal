package model

import CastArrangement
import ViewState
import controller.Configuration
import controller.Syndication
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.*
import tornadofx.*
import java.io.File
import kotlin.coroutines.CoroutineContext

object PrimaryViewModel : CoroutineScope {
    val backgroundJobs = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    val castScopes = observableListOf<CastScope>()
    val viewState = SimpleObjectProperty(ViewState.HOME)
    val detailView = SimpleObjectProperty<CastScope>(null)
    val castArrangementState = SimpleObjectProperty(CastArrangement.GRID)
    val error = SimpleStringProperty()
    val isError = error.booleanBinding { !it.isNullOrBlank() }
    val warning = SimpleStringProperty("")
    var warnAction: (() -> Unit)? = null

    val offlineMode = SimpleBooleanProperty(false)

    init {
        offlineMode.onChange {
            if (it) launchOfflineLoop()
        }
    }

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

    fun setDetail(scope: CastScope, isDownload: Boolean = false) {
        detailView.value = scope
        viewState.value = if (isDownload) ViewState.DETAIL_DOWNLOAD else ViewState.DETAIL
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
            it[((it.indexOf(it.find { it.isPlaying.value == true }) - 1).takeIf { it >= 0 } ?: 0)]
        }

    fun getAnyUnplayed (prog : MutableMap<String, Double>) : EpisodeModel? = castScopes
        .find { it.model.items.any { it.isPlaying.value == true } }
        ?.model?.items?.firstOrNull { !prog.keys.contains(it.guid.value) }

    fun clearDetailsOnNonPlayingCasts() = castScopes
        .filter { !it.model.items.any { it.isPlaying.value } }
        .forEach {
            it.currentTitle.value = ""
            it.currentDescription.value = ""
        }

    fun setError(error: String) =
        launch {
            withContext(Dispatchers.Main) { PrimaryViewModel.error.value = error }
            offlineMode.value = true
            delay(Configuration.errorReadTime)
            withContext(Dispatchers.Main) { PrimaryViewModel.error.value = null }
        }

    fun autoplay(prog: MutableMap<String, Double>) {
        val newer = getPrevious()
        val older = getNext()

        println("Prev: ${newer?.let { prog[it.guid.value] }} Next: ${older?.let { prog[it.guid.value] }}")
        when {
            newer?.let { prog[it.guid.value] != -1.0 } == true -> {
                println("auto prev")
                newer.startPlayback()
            }
            older?.let { prog[it.guid.value] != -1.0 } == true -> {
                println("auto next")
                older.startPlayback()
            }
            else -> getAnyUnplayed(prog)?.apply {
                println("auto any")
                startPlayback()
            }
        }
    }

    fun launchOfflineLoop() =
        launch(coroutineContext + backgroundJobs) {
            var attempts = 0
            while(offlineMode.value && isActive && attempts < Configuration.maxAttempts) {
                try {
                    println("trying to use internet...${attempts} / ${Configuration.maxAttempts}")
                    if (find<Syndication>().refreshRss() == true) {
                        offlineMode.value = false
                    }
                } catch (e: Throwable) {
                    attempts++
                    delay(Configuration.retryIntervals[attempts scaleDelay Configuration.maxAttempts])
                }
            }
        }

    private infix fun Int.scaleDelay(maxDelay: Int) : Int {
        return (maxDelay/16).let { step ->
            when(this) {
                in 0..step -> 0
                in step..(2*step) -> 1
                in (2*step)..(4*step) -> 2
                in (4*step)..(8*step) -> 3
                else -> 4
            }
        }
    }

}