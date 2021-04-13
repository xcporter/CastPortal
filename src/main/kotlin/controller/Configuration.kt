package controller

import java.io.File

object Configuration {
    var path = File("${System.getProperty("user.home")}/.castportal")
    val platform = System.getProperty("os.name")

    val defaultStreams = listOf (
        "https://feeds.npr.org/510312/podcast.xml",
        "https://rss.art19.com/1619",
        "https://feeds.megaphone.fm/ADL9840290619",
        "https://feeds.megaphone.fm/unlocking-us"
    )
}