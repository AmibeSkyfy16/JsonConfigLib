package ch.skyfy.jsonconfig

import mu.KotlinLogging
import java.io.IOException

object JsonConfig {
    val LOGGER = KotlinLogging.logger {}
    fun loadConfigs(classesToLoad: Array<Class<*>>) = ReflectionUtils.loadClassesByReflection(classesToLoad)

    inline fun <reified DATA : Validatable> reloadConfig(jsonData: JsonData<DATA>) {
        try {
            jsonData.data = JsonManager.get(jsonData.relativeFilePath)
        } catch (_: IOException) { }
    }
}