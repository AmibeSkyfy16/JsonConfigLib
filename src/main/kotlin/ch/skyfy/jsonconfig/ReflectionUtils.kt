package ch.skyfy.jsonconfig

import ch.skyfy.jsonconfig.JsonConfig.LOGGER

object ReflectionUtils {
    fun loadClassesByReflection(classesToLoad: Array<Class<*>>) {
        for (config in classesToLoad) {
            val canonicalName = config.canonicalName
            try {
                Class.forName(canonicalName)
            } catch (e: ClassNotFoundException) {
                LOGGER.fatal("A FATAL ERROR OCCURRED")
                LOGGER.fatal("A FATAL ERROR OCCURRED")
                LOGGER.fatal("A FATAL ERROR OCCURRED")
                LOGGER.fatal("A FATAL ERROR OCCURRED")
                throw RuntimeException(e)
            }
        }
    }
}