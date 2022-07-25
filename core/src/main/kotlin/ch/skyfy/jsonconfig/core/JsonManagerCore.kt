@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalSerializationApi::class)

package ch.skyfy.jsonconfig.core


import ch.skyfy.jsonconfig.core.internal.Registrators
import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.reflect.full.createInstance


@Suppress("unused")
object JsonManagerCore  {


    /**
     *
     * This method try to deserialize a json file to a kotlin object (DATA).
     * If json file is not present, the kotlin object (DATA) will be created by a class that implement Defaultable<DATA>
     * Also a new json file will be created based on the DATA object
     *
     * If the json file does not match the json standard, an error will be thrown and the program will stop
     */
     inline fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> getOrCreateConfig(
        file: Path
    ): DATA {
        try {
            val d: DATA = if (file.exists())  Registrators.registrator.get(file, true)
            else Registrators.registrator.save(DEFAULT::class.createInstance().getDefault(), file)
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
    inline fun <reified DATA : Validatable> getOrCreateConfig(
        file: Path,
        defaultFile: String
    ): DATA {
        try {
            return if (file.exists()) Registrators.registrator.get(file, true)
//            else get(extractResource(file, defaultFile, DATA::class.java.classLoader), true)
            else Registrators.registrator.get(extractResource(file, defaultFile, DATA::class.java.classLoader), true)
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }

//    /**
//     * Try to convert a json data stored in a file to an object
//     *
//     */
//    @Throws(Exception::class)
//    inline fun <reified DATA : Validatable> get(file: Path, json: Json = this.json, shouldCrash: Boolean): DATA {
//        val `data`: DATA = json.decodeFromStream(file.inputStream())
//        if (!`data`.confirmValidate(mutableListOf(), shouldCrash)) throw Exception("The json file is not valid !!!")
//        return `data`
//    }


//    /**
//     * Use in getOrCreateConfig fun
//     */
//    @Throws(Exception::class)
//    inline fun <reified DATA : Validatable> save(
//        config: DATA,
//        file: Path,
//        json: Json = this.json
//    ): DATA {
//        config.confirmValidate(mutableListOf(), true)
//        file.parent.createDirectories()
//        json.encodeToStream(config, file.outputStream())
//        return config
//    }

//    /**
//     * Use by coder to save edited data.
//     * For example, you add a user in a list, you have the called this method to save it to the json file
//     *
//     */
//    @Throws(Exception::class)
//    inline fun <reified DATA : Validatable> save(
//        jsonData: JsonData<DATA>,
//        json: Json = this.json
//    ) {
//        if (!jsonData.data.confirmValidate(mutableListOf(), false)) {
//            JsonConfig.LOGGER.warn("The data you tried to save has not been saved, because something is not valid")
//            return
//        }
//        json.encodeToStream(jsonData.data, jsonData.relativeFilePath.outputStream())
//    }


    fun extractResource(file: Path, resource: String, classLoader: ClassLoader): Path {
        val s = Objects.requireNonNull(classLoader.getResourceAsStream(resource))
        Files.copy(s, file)
        return file
    }

}