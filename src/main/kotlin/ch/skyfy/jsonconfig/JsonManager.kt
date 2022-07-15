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

    inline fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> getOrCreateConfig(
        file: Path,
        json: Json = Json {
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
        }
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
        json: Json = Json {
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
        }
    ): DATA {
        try {
            val d: DATA = if (file.exists()) get(file, json)
            else get(extractResource(file, defaultFile, DATA::class.java.classLoader), json)
            d.validate(mutableListOf())
            return d
        } catch (e: java.lang.Exception) { throw RuntimeException(e) }
    }

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> get(file: Path, json: Json): DATA =
        json.decodeFromStream(file.inputStream())
//        Files.newBufferedReader(file).use { reader ->
//            return json.decodeFromStream(file.inputStream())
//            return gson.fromJson(reader, DATA::class.java)
//        }

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> save(
        config: DATA,
        file: Path,
        json: Json = Json {
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
        }
    ): DATA {
        file.parent.createDirectories()
        json.encodeToStream(config , file.outputStream())
//        Files.newBufferedWriter(file).use { writer ->
//            gson.toJson(config, DATA::class.java, writer)
//        }
        return config
    }

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> save(
        jsonData: JsonData<DATA>,
        json: Json = Json {
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
        }
    ) {
        json.encodeToStream(jsonData.data , jsonData.relativeFilePath.outputStream())
//        Files.newBufferedWriter(jsonData.relativeFilePath).use { writer ->
//            gson.toJson(jsonData.data, DATA::class.java, writer)
//        }
    }

    fun extractResource(file: Path, resource: String, classLoader: ClassLoader) : Path {
        val s = Objects.requireNonNull(classLoader.getResourceAsStream(resource))
        Files.copy(s, file)
        return file
    }

}