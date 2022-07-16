package ch.skyfy.jsonconfig

import mu.KotlinLogging

object JsonConfig {
    val LOGGER = KotlinLogging.logger {}
    fun loadConfigs(classesToLoad: Array<Class<*>>) = ReflectionUtils.loadClassesByReflection(classesToLoad)

    inline fun <reified DATA : Validatable> reloadConfig(jsonData: JsonData<DATA>) {
        try {
            jsonData.data = JsonManager.get(jsonData.relativeFilePath, shouldCrash = false)
        } catch (e: Exception) {
            e.printStackTrace()
            LOGGER.error("The configuration cannot be reloaded due to errors")
        }
    }
}