package ch.skyfy.jsonconfig.core

import ch.skyfy.jsonconfig.core.JsonConfig.LOGGER

object ReflectionUtils {
    fun loadClassesByReflection(classesToLoad: Array<Class<*>>) {
        for (config in classesToLoad) {
            val canonicalName = config.canonicalName
            try {
                Class.forName(canonicalName)
            } catch (e: ClassNotFoundException) {
                LOGGER.error("A FATAL ERROR OCCURRED")
                LOGGER.error("A FATAL ERROR OCCURRED")
                LOGGER.error("A FATAL ERROR OCCURRED")
                LOGGER.error("A FATAL ERROR OCCURRED")
                throw RuntimeException(e)
            }
        }
    }
}