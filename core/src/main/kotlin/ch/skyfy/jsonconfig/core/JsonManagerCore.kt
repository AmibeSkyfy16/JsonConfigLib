@file:Suppress("UnstableApiUsage")

package ch.skyfy.jsonconfig.core

import ch.skyfy.jsonconfig.core.internal.Registrators
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.reflect.full.createInstance

@Suppress("unused")
object JsonManagerCore {

    /**
     *
     * This method try to deserialize a json file to a kotlin object (DATA).
     * If json file is not present, the kotlin object (DATA) will be created by a class that implement Defaultable<DATA>
     * Also a new json file will be created based on the DATA object
     *
     * If the json file does not match the json standard, an error will be thrown and the program will stop
     */
    inline fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> getOrCreateConfig(file: Path): DATA {
        try {
            val d: DATA = if (file.exists()) Registrators.get.get(file, true)
            else Registrators.save.save(DEFAULT::class.createInstance().getDefault(), file)
            d.confirmValidate(mutableListOf(), true)
            return d
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * This method try to deserialize a json file to a kotlin object (DATA).
     * But this time, if the json file is not present, the kotlin object (DATA)
     * will be created from another json file (a default json, stored inside the jar (in resources folder))
     *
     * If the json file does not match the json standard, an error will be thrown and the program will stop
     */
    inline fun <reified DATA : Validatable> getOrCreateConfig(file: Path, defaultFile: String): DATA {
        try {
            return if (file.exists())  Registrators.get.get(file, true)
            else Registrators.get.get(extractResource(file, defaultFile, DATA::class.java.classLoader), true)
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }

    fun extractResource(file: Path, resource: String, classLoader: ClassLoader): Path {
        val s = Objects.requireNonNull(classLoader.getResourceAsStream(resource))
        Files.copy(s, file)
        return file
    }

}