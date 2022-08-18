package ch.skyfy.jsonconfiglib

/**
 * An interface used to create by reflection a default configuration object base on type [DATA]
 */
fun interface Defaultable<DATA> {
    /**
     * @return An object of type [DATA] that represent the configuration
     */
    fun getDefault(): DATA
}