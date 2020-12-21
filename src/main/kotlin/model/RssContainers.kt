package model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

interface RssContainer

data class RSS(val channel : Channel? = null) : RssContainer

data class Channel(var title: String? = null,
                   var link: String? = null,
                   var description: String? = null,
                   var copyright: String? = null,
                   var generator: String? = null,
                   var language: String? = null,
                   var author: String? = null,
                   @JsonAlias("owner", "itunes:owner")
                   var owner: RssOwner? = null,
                   var image: RssImage? = null,
                   var lastBuildDate: String? = null,
                   @JsonProperty("item")
                   var items: List<RssItem> = listOf()) : RssContainer

data class RssItem (var title: String? = null,
                    var description: String? = null,
                    var pubDate: String? = null,
                    var copyright: String? = null,
                    var guid: String? = null,
                    var link: String? = null,
                    var author: String? = null,
                    @JsonAlias("duration", "itunes:duration")
                    var duration: String? = null,
                    var explicit: String? = null,
                    var enclosure: RssEnclosure? ) : RssContainer

data class RssEnclosure (var url: String? = null,
                         var length: Long? = null,
                         var type: String? = null) : RssContainer

data class RssImage (@JsonAlias("href", "url") var href: String? = null) : RssContainer

data class RssOwner (var name:String? = null,
                     var email: String? = null) : RssContainer