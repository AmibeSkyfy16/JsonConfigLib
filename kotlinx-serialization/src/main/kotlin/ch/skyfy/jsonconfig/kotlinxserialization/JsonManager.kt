@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalSerializationApi::class)

package ch.skyfy.jsonconfig.kotlinxserialization

import ch.skyfy.jsonconfig.core.JsonConfig
import ch.skyfy.jsonconfig.core.JsonData
import ch.skyfy.jsonconfig.core.Validatable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

@Suppress("unused")
object JsonManager {

    var json = Json {
        prettyPrint = true
        isLenient = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
    }


    class Get {
        companion object{
            inline operator fun <reified DATA : Validatable> invoke(file: Path, shouldCrash: Boolean) : DATA{
                val `data`: DATA = json.decodeFromStream(file.inputStream())
                if (!`data`.confirmValidate(mutableListOf(), shouldCrash)) throw Exception("The json file is not valid !!!")
                return `data`
            }
        }
    }

    class Save{
        companion object{
            inline operator fun <reified DATA : Validatable> invoke(config: DATA, file: Path) : DATA{
                config.confirmValidate(mutableListOf(), true)
                json.encodeToStream(config, file.outputStream())
                return config
            }
        }
    }

    /**
     * Try to convert a json data stored in a file to an object
     *
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> get(file: Path, shouldCrash: Boolean): DATA {
        val `data`: DATA = json.decodeFromStream(file.inputStream())
        if (!`data`.confirmValidate(mutableListOf(), shouldCrash)) throw Exception("The json file is not valid !!!")
        return `data`
    }

    /**
     * Use in getOrCreateConfig fun
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(config: DATA, file: Path): DATA {
        config.confirmValidate(mutableListOf(), true)
        json.encodeToStream(config, file.outputStream())
        return config
    }

    /**
     * Use by coder to save edited data.
     * For example, you add a user in a list, you have the called this method to save it to the json file
     *
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(jsonData: JsonData<DATA>) {
        if (!jsonData.data.confirmValidate(mutableListOf(), false)) {
            JsonConfig.LOGGER.warn("The data you tried to save has not been saved, because something is not valid")
            return
        }
        json.encodeToStream(jsonData.data, jsonData.relativeFilePath.outputStream())
    }

}