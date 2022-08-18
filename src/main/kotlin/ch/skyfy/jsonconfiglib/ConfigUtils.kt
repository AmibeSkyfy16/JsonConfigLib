package ch.skyfy.jsonconfiglib

object ConfigUtils {
    /**
     * An utils fun that will call [Class.forName] to load configuration for each [Class] inside the [Array]
     *
     * @param classesToLoad An [Array] of class representing singleton object that contains [ConfigData] fields that need to be loaded
     */
    fun loadClassesByReflection(classesToLoad: Array<Class<*>>) {
        for (config in classesToLoad) {
            val canonicalName = config.canonicalName
            try {
                Class.forName(canonicalName)
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            }
        }
    }
}