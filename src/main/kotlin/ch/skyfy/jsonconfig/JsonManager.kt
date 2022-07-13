@file:Suppress("UnstableApiUsage")

package ch.skyfy.jsonconfig

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Objects
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.reflect.full.createInstance

object JsonManager {

    inline fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> getOrCreateConfig(
        file: Path,
        gson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
    ): DATA {
        try {
            val d: DATA = if (file.exists()) get(file, gson)
            else save(DEFAULT::class.createInstance().getDefault(), file, gson)
            d.validate(mutableListOf())
            return d
        } catch (e: java.lang.Exception) { throw RuntimeException(e) }
    }

    inline fun <reified DATA : Validatable> getOrCreateConfig(
        file: Path,
        defaultFile: String,
        gson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
    ): DATA {
        try {
            val d: DATA = if (file.exists()) get(file, gson)
            else get(extractResource(file, defaultFile, DATA::class.java.classLoader), gson)
            d.validate(mutableListOf())
            return d
        } catch (e: java.lang.Exception) { throw RuntimeException(e) }
    }

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> get(file: Path, gson: Gson): DATA =
        Files.newBufferedReader(file).use { reader -> return gson.fromJson(reader, DATA::class.java) }

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> save(config: DATA, file: Path, gson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()): DATA {
        file.parent.createDirectories()
        Files.newBufferedWriter(file).use { writer -> gson.toJson(config, DATA::class.java, writer) }
        return config
    }

    @Throws(IOException::class)
    inline fun <reified DATA : Validatable> save(jsonData: JsonData<DATA>, gson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()) {
        Files.newBufferedWriter(jsonData.relativeFilePath).use { writer -> gson.toJson(jsonData.data, DATA::class.java, writer) }
    }

    fun extractResource(file: Path, resource: String, classLoader: ClassLoader) : Path {
        val s = Objects.requireNonNull(classLoader.getResourceAsStream(resource))
        Files.copy(s, file)
        return file
    }

}