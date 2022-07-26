package ch.skyfy.jsonconfig.core

import mu.KotlinLogging
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.createInstance

object JsonConfig {
    val LOGGER = KotlinLogging.logger {}

    fun loadConfigs(classesToLoad: Array<Class<*>>) {

        for (item in Package.getPackages()) {
            println(item.name)

            // Implementation for kotlinx.serialization
            if (item.name == "ch.skyfy.jsonconfig.kotlinxserialization") {
                val clazz = Class.forName("ch.skyfy.jsonconfig.kotlinxserialization.Loader")
//                val inst = clazz.kotlin.companionObject.


            }

            // Implementation for Google Gson
            if (item.name == "ch.skyfy.jsonconfig.gson") {
                // TODO
            }

            // Implementation for Jackson
            if (item.name == "ch.skyfy.jsonconfig.jackson") {
                // TODO
            }

        }

        ReflectionUtils.loadClassesByReflection(classesToLoad)
    }

    inline fun <reified DATA : Validatable> reloadConfig(jsonData: JsonData<DATA>) : Boolean {

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