@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalSerializationApi::class)

package ch.skyfy.jsonconfiglib

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*
import kotlin.reflect.full.createInstance

@Suppress("unused")
object ConfigManager {

    var json = Json {
        prettyPrint = true
        isLenient = true
        encodeDefaults = true
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
    }

    val LOGGER = KotlinLogging.logger {}

    /**
     * Used to load one or multiple configurations.
     *
     * @param classesToLoad An [Array] of class representing singleton object that contains [ConfigData] fields that need to be loaded
     */
    fun loadConfigs(classesToLoad: Array<Class<*>>) = ConfigUtils.loadClassesByReflection(classesToLoad)

    /**
     * Used to reload a configuration when a JSON file has been modified
     *
     * @param configData A [ConfigData] object that represent a configuration
     * @return A [Boolean] that will be true if the configuration has been successfully reloaded
     */
    inline fun <reified DATA : Validatable, reified DELEGATE : DelegateData<DATA>> reloadConfig(configData: ConfigData<DATA, DELEGATE>): Boolean {
        try {
            configData.delegateData.data = get(configData.relativeFilePath, shouldThrowRuntimeException = false)
        } catch (e: Exception) {
            e.printStackTrace()
            LOGGER.error("The configuration cannot be reloaded due to errors")
            return false
        }
        return true
    }

    /**
     * This method try to deserialize a JSON file to an object of type [DATA].
     * If the JSON file is not found, a new object will be created provided by the type [DEFAULT]
     * and a new JSON file will be created
     *
     * If the JSON file does not match the JSON standard or your specific implementation that you override in your data classes,
     * a [RuntimeException] will be thrown
     *
     * @param file A [Path] object representing where the configuration file is located
     * @param json A [Json] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     * @return An object of type [DATA] that represent the configuration
     */
    inline fun <reified DATA : Validatable, reified DELEGATE : DelegateData<DATA>, reified DEFAULT : Defaultable<DATA>> getOrCreateConfig(
        file: Path,
        json: Json = ConfigManager.json,
    ): DELEGATE {

        try {
            val d: DATA = if (file.exists()) get(file, json, true)
            else save(DEFAULT::class.createInstance().getDefault(), file, json)
            d.confirmValidate(shouldThrowRuntimeException = true)
            val delegate = DELEGATE::class.constructors.first().call(d)
            return delegate
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * This method try to deserialize a JSON file to an object of type [DATA]
     * But this time, if the JSON file is not found, the object will be created from another
     * JSON file (a default JSON, stored inside the jar (in resources folder))
     *
     * If the JSON file does not match the JSON standard or your specific implementation that you override in your data classes,
     * a [RuntimeException] will be thrown
     *
     * @param file A [Path] object representing where the configuration file is located
     * @param defaultFile A [String] object representing a path inside a local jar to a default JSON file
     * @param json A [Json] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     * @return An object of type [DATA] that represent the configuration
     */
    inline fun <reified DATA : Validatable, reified DELEGATE : DelegateData<DATA>> getOrCreateConfig(
        file: Path,
        defaultFile: String,
        json: Json = ConfigManager.json,
    ): DELEGATE {
        try {
            val data:DATA = if (file.exists()) get(file, json, true)
            else get(extractResource(file, defaultFile, DATA::class.java.classLoader), json, true)
            val delegate = DELEGATE::class.constructors.first().call(data)
            return delegate
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * Try to deserialize a JSON file to an object of type [DATA]
     *
     * @param file A [Path] object representing where the configuration file is located
     * @param json A [Json] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     * @param shouldThrowRuntimeException A [Boolean] object which specifies whether to throw a [RuntimeException] if the configuration is considered as invalid
     * @return An object of type [DATA] that represent the configuration
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> get(file: Path, json: Json = ConfigManager.json, shouldThrowRuntimeException: Boolean): DATA {
        val `data`: DATA = json.decodeFromStream(file.inputStream())
        if (!`data`.confirmValidate(shouldThrowRuntimeException = shouldThrowRuntimeException)) throw Exception("The json file is not valid !!!")
        return `data`
    }

    /**
     * Try to save a configuration object representing by an object of type [DATA]
     *
     * In a first time, a checking is made to verify if the configuration is valid or not.
     * If it's not valid a [RuntimeException] will be thrown
     *
     * @param config An object of type [DATA] that representing the configuration to save
     * @param file A [Path] object representing where the configuration file is located
     * @param json A [Json] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     * @return An object of type [DATA] that represent the configuration
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(
        config: DATA,
        file: Path,
        json: Json = ConfigManager.json,
    ): DATA {
        config.confirmValidate(mutableListOf(), true)
        file.parent.createDirectories()
        json.encodeToStream(config, file.outputStream())
        return config
    }

    /**
     * Try to save a configuration object representing by an object of type [DATA]
     *
     * This fun will generally be used by the developer later in the code when data has been modified and needs to be saved
     *
     * @param configData A [ConfigData] object that represent a configuration
     * @param json A [Json] object that is used to serialize and deserialize the file representing the configuration (Already has a default value and does not have to be specified)
     */
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable, reified DELEGATE : DelegateData<DATA>> save(
        configData: ConfigData<DATA, DELEGATE>,
        json: Json = ConfigManager.json,
    ) {
        if (!configData.delegateData.data.confirmValidate(mutableListOf(), false)) {
            LOGGER.warn("The data you tried to save has not been saved, because something is not valid")
            return
        }
        json.encodeToStream(configData.delegateData.data, configData.relativeFilePath.outputStream())
    }

//    inline fun <reified DATA : Validatable> computeAndSave(
//        configData: ConfigData<DATA>,
//        block: (DATA) -> Unit,
//        json: Json = ConfigManager.json,
//    ) {
//        block.invoke(configData.`data`)
//        save(configData, json)
//    }

    /**
     * A simple fun that will be used to extract a file from inside a jar to somewhere outside the jar
     *
     * @param file A [Path] object that represent where the embedded file we be extracted
     * @param resource A [String] object representing a path inside a local jar to a default JSON file
     * @return return A [Path] object representing the extracted file
     */
    fun extractResource(file: Path, resource: String, classLoader: ClassLoader): Path {
        Files.copy(Objects.requireNonNull(classLoader.getResourceAsStream(resource)), file)
        return file
    }

}