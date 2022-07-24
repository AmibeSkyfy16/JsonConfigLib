package ch.skyfy.jsonconfig.core

import mu.KotlinLogging

object JsonConfig {
    val LOGGER = KotlinLogging.logger {}
    fun loadConfigs(classesToLoad: Array<Class<*>>) = ReflectionUtils.loadClassesByReflection(classesToLoad)

    inline fun <reified DATA : Validatable> reloadConfig(jsonData: JsonData<DATA>) : Boolean {

        val className =

//        try {
//            jsonData.data = JsonManager.get(jsonData.relativeFilePath, shouldCrash = false)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            LOGGER.error("The configuration cannot be reloaded due to errors")
//            return false
//        }
        return true
    }
}