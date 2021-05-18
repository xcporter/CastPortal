package controller

object Time {
    private val pattern1 = Regex("^(\\d{2}):(\\d{2}):(\\d{2})$")
    private val pattern2 = Regex("^(\\d{2}):(\\d{2})$")
    private val pattern3 = Regex("(\\d+)")
    fun parse(str: String) : Double {
        var acc: Double? = null
        pattern1.find(str)?.let {
            val (hour, min, sec) = it.destructured
            acc = ((hour.toInt() * 60 * 60 * 1000) + (min.toInt() * 60 * 1000) + (sec.toInt() * 1000)).toDouble()
        }
        pattern2.find(str)?.let {
            if (acc == null) {
                val (min, sec) = it.destructured
                acc = ((min.toInt() * 60 * 1000) + (sec.toInt() * 1000)).toDouble()
            }
        }
        pattern3.find(str)?.let {
            if (acc == null) {
                val (sec) = it.destructured
                acc = ((sec.toInt() * 1000)).toDouble()
            }
        }
        return acc ?: error { "invalid time string $str" }
    }
}