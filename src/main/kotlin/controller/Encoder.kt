package controller

import org.joda.time.Duration
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
}