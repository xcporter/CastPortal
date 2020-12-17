import tornadofx.*
import view.Primary

class CastPortal : App(Primary::class, BaseStyle::class)

fun main () {
    launch<CastPortal>()
}