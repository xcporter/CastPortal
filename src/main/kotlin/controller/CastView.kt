package controller

import controller.Encoder.fileNameEncode
import kotlinx.coroutines.SupervisorJob
import model.CastScope
import model.EpisodeModel
import tornadofx.*
import java.io.File
import java.net.URL

object CastView {
    val castScopes = observableListOf<CastScope>()

    fun clearIsPlaying () {
        castScopes.forEach {
            it.episodesToDisplay.forEach {
                it.isPlaying.value = false
            }
        }
    }

    fun clearDetail () {
        castScopes.forEach {
            it.episodesToDisplay.forEach {
                it.detail.value = false
            }
        }
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
}