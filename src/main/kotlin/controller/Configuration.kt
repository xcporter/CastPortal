package controller

import java.io.File

object Configuration {
    var path = File("${System.getProperty("user.home")}/.castportal")
    val platform = System.getProperty("os.name")
    val timeout : Long = 10000
    val errorReadTime: Long = 5000

//    Offline mode--hierarchy of delay vs attempts
//    after many attempts, delay increases
    val retryIntervals = listOf<Long>(1000, 5000, 10000, 30000, 60000)
    val maxAttempts: Int = 40

    val defaultStreams = listOf (
        "https://feeds.npr.org/510312/podcast.xml",
        "https://rss.art19.com/1619",
        "https://feeds.megaphone.fm/ADL9840290619",
        "https://feeds.megaphone.fm/unlocking-us"
    )
}