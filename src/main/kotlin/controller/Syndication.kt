package controller

import model.RSS
import tornadofx.*

class Syndication : Controller() {
    val rssFeeds = observableListOf<RSS>()
}