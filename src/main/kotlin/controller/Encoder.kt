package controller

import org.joda.time.Duration
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import ws.schild.jave.AudioAttributes
import ws.schild.jave.Encoder
import ws.schild.jave.EncodingAttributes
import ws.schild.jave.MultimediaObject
import java.io.File
import java.security.MessageDigest
import java.util.*

object Encoder {
    val base64Encoder = Base64.getEncoder()
    val base64Decoder = Base64.getDecoder()

    fun String.fileNameEncode() : String {
        val bytes = MessageDigest.getInstance("sha-256").digest(this.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun String?.formatTime () : String {
        return if (this?.contains(':') == true) {
            if (this.split(':')[0] == "00")
                this.split(':').drop(1).joinToString(":")
            else this
        } else {
            val s = Duration.standardSeconds(this?.toLong() ?: 0)
            "${s.standardHours.let { if (it > 0) "$it:" else "" }}${s.standardHours.let { if(it == 0L) s.standardMinutes % 60 else "%02d".format(s.standardMinutes % 60)}}:${"%02d".format(s.standardSeconds % 60)}"
        }
    }

    fun String?.formatDate () : String {
        return this?.split(' ')?.let {
            "${it[2]} ${it[1]}, ${it[3]}"
        } ?: ""
    }

    fun String?.stripHtml () : String = Jsoup.clean(this ?: "", Whitelist.none()).replace("&nbsp;", " ")


    val encoder = Encoder()

    fun File.toWav () : File {
        val target = createTempFile(suffix = ".wav")
        encoder.encode(MultimediaObject(this), target, WavFormat.attr)
        return target
    }


    object WavFormat {
        private val audio = AudioAttributes().apply {
            setCodec("pcm_s16le")
            setBitRate(2000)
            setChannels(2)
            setSamplingRate(48000)
        }
        val attr = EncodingAttributes().apply {
            format = "wav"
            audioAttributes = audio
        }
    }
}