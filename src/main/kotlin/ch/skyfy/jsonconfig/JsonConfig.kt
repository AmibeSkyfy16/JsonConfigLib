package ch.skyfy.jsonconfig

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object JsonConfig {
    val LOGGER: Logger = LogManager.getLogger(JsonConfig::class.java)
    fun loadConfigs(classesToLoad: Array<Class<*>>) = ReflectionUtils.loadClassesByReflection(classesToLoad)
}