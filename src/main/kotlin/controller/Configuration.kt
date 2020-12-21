package controller

import java.io.File

object Configuration {
    var path = File("${System.getProperty("user.home")}/.castportal")
    var displayNumberOfEpisodes : Int = 5
}