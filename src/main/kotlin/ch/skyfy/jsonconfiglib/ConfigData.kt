package ch.skyfy.jsonconfiglib

import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * Set the value for the chosen member property by accessing her delegate
 */
inline fun <reified DATA : Validatable, reified TYPE> ConfigData<DATA>.setValue(kProperty1: KMutableProperty1<DATA, TYPE>, value: TYPE) {
    delegates.forEach { entry ->
        if (entry.key == kProperty1){
            val anonymous = entry.value.invoke().get()
            val delegated = anonymous::class.memberProperties.first() as KMutableProperty1
            delegated.setter.call(anonymous, value)
        }
    }
}

/**
 * Allow user to add a block of code that will be called every time a member property of [DATA] is set
 */
fun <DATA : Validatable> ConfigData<DATA>.addGlobalNotifier(notifier: (KProperty<*>, Any?, Any?, DATA) -> Unit) {
    this.onChangeCallbacks.forEach { entry -> entry.value.add(notifier) }
}

/**
 * A data class representing a specific configuration
 *
 * To create instance of a ConfigData object, we use special fun called invoke that accept reified generic type
 *
 * @property data An object of type [DATA] representing the configuration
 * @property relativeFilePath A [Path] object representing where the configuration file is located
 */
data class ConfigData<DATA : Validatable>(
    var `data`: DATA,
    val relativeFilePath: Path,
    val delegates: MutableMap<KProperty1<*, *>, () -> AtomicReference<Any>>,
    val onChangeCallbacks: MutableMap<KProperty1<*, *>, MutableList<(KProperty<*>, Any?, Any?, DATA) -> Unit>>
) {

    data class InitializeObject<DATA : Validatable>(
        val delegates: MutableMap<KProperty1<*, *>, () -> AtomicReference<Any>>,
        val onChangeCallbacks: MutableMap<KProperty1<*, *>, MutableList<(KProperty<*>, Any?, Any?, DATA) -> Unit>>
    ){
        companion object{
            inline operator fun <reified DATA : Validatable> invoke(
                delegates: MutableMap<KProperty1<*, *>, () -> AtomicReference<Any>>,
                onChangeCallbacks: MutableMap<KProperty1<*, *>, MutableList<(KProperty<*>, Any?, Any?, DATA) -> Unit>>
            ): InitializeObject<DATA> {
                return InitializeObject(delegates, onChangeCallbacks)
//                val `data` = ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath)
//                val initializeObject = initialize(`data`, relativeFilePath, automaticallySave)
//                return ConfigData(`data`, relativeFilePath, initializeObject.delegates, initializeObject.onChangeCallbacks)
            }
        }
    }

    companion object {

        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path, automaticallySave: Boolean): ConfigData<DATA> {
            val `data` = ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath)
            val initializeObject = initialize(`data`, relativeFilePath, automaticallySave)
            return ConfigData(`data`, relativeFilePath, initializeObject.delegates, initializeObject.onChangeCallbacks)
        }

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String, automaticallySave: Boolean): ConfigData<DATA> {
            val `data` = ConfigManager.getOrCreateConfig<DATA>(relativeFilePath, defaultFile)
            val initializeObject = initialize(`data`, relativeFilePath, automaticallySave)
            return ConfigData(`data`, relativeFilePath, initializeObject.delegates, initializeObject.onChangeCallbacks)
        }

        /**
         * Create delegate for each member property found in [DATA]
         */
        inline fun <reified DATA : Validatable> initialize(`data`: DATA, relativeFilePath: Path, automaticallySave: Boolean): InitializeObject<DATA> {
            val delegates = mutableMapOf<KProperty1<*, *>, () -> AtomicReference<Any>>()
            val onChangeCallbacks: MutableMap<KProperty1<*, *>, MutableList<(KProperty<*>, Any?, Any?, DATA) -> Unit>> = mutableMapOf()
            DATA::class.memberProperties.forEach { kProperty1 ->
                val anonymous = object {
                    @Suppress("unused")
                    var delegated by Delegates.observable(kProperty1.get(`data`)) { kProperty, oldValue, newValue ->
                        if (kProperty1 is KMutableProperty1) kProperty1.setter.call(`data`, newValue)
                        onChangeCallbacks.forEach { entry ->
                            if (entry.key === kProperty1) entry.value.forEach { it.invoke(kProperty, oldValue, newValue, data) }
                        }
                    }
                }
                delegates[kProperty1] = { AtomicReference(anonymous) }

                val mutableListOf: MutableList<(KProperty<*>, Any?, Any?, DATA) -> Unit> = mutableListOf()
                if (automaticallySave) mutableListOf.add { _, _, _, _ -> ConfigManager.save(`data`, relativeFilePath) }
                onChangeCallbacks[kProperty1] = mutableListOf
            }
            return InitializeObject.invoke(delegates, onChangeCallbacks)
//            return InitializeObject(delegates, onChangeCallbacks)
        }
    }

}
