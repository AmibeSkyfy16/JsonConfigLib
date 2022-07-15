@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalSerializationApi::class)

package ch.skyfy.jsonconfig

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Objects
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlin.reflect.full.createInstance

object JsonManager {

    val defaultJson = Json {
        prettyPrint = true
        isLenient = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
    }

    inline fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> getOrCreateConfig(
        file: Path,
        json: Json = defaultJson
    ): DATA {

        try {
            val d: DATA = if (file.exists()) get(file, json)
            else save(DEFAULT::class.createInstance().getDefault(), file, json)
            d.validate(mutableListOf())
            return d
        } catch (e: java.lang.Exception) { throw RuntimeException(e) }
    }

    inline fun <reified DATA : Validatable> getOrCreateConfig(
        file: Path,
        defaultFile: String,
        json: Json = defaultJson
    ): DATA {
        try {
            val d: DATA = if (file.exists()) get(file, json)
            else get(extractResource(file, defaultFile, DATA::class.java.classLoader), json)
            d.validate(mutableListOf())
            return d
        } catch (e: java.lang.Exception) { throw RuntimeException(e) }
    }

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> get(file: Path, json: Json = defaultJson): DATA = json.decodeFromStream(file.inputStream())

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> save(
        config: DATA,
        file: Path,
        json: Json = defaultJson
    ): DATA {
        file.parent.createDirectories()
        json.encodeToStream(config , file.outputStream())
        return config
    }

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> save(
        jsonData: JsonData<DATA>,
        json: Json = defaultJson
    ) = json.encodeToStream(jsonData.data , jsonData.relativeFilePath.outputStream())


    fun extractResource(file: Path, resource: String, classLoader: ClassLoader) : Path {
        val s = Objects.requireNonNull(classLoader.getResourceAsStream(resource))
        Files.copy(s, file)
        return file
    }

}