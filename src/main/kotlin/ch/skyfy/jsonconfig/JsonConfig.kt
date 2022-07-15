package ch.skyfy.jsonconfig

import mu.KotlinLogging

object JsonConfig {
    val LOGGER = KotlinLogging.logger {}
    fun loadConfigs(classesToLoad: Array<Class<*>>) = ReflectionUtils.loadClassesByReflection(classesToLoad)
}