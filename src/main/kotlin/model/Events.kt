package model

import tornadofx.*

class ShowDescription (val description: String, val title: String, val author: String) : FXEvent()
class RenderDownloads () : FXEvent()
class UpdateProgress () : FXEvent()