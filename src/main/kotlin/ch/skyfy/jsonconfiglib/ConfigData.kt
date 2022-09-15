package ch.skyfy.jsonconfiglib

import java.nio.file.Path
import kotlin.reflect.KMutableProperty1

inline fun <reified DATA : Validatable, reified TYPE> ConfigData<DATA>.update(prop: KMutableProperty1<DATA, TYPE>, value: TYPE) {
    val oldValue = prop.get(serializableData)
    prop.set(serializableData, value)
    this.onUpdateCallbacks.forEach {
        it.invoke(prop, oldValue, value, serializableData)
    }
    this.onUpdateCallbacksMap.forEach { entry ->
        if (entry.key.name == prop.name) entry.value.forEach { it.invoke(prop, oldValue, value, serializableData) }
    }
}

inline fun <reified DATA : Validatable, reified NESTED_DATA : Validatable, reified TYPE> ConfigData<DATA>.update(prop: KMutableProperty1<NESTED_DATA, TYPE>, nested: NESTED_DATA, value: TYPE) {
    val oldValue = prop.get(nested)
    prop.set(nested, value)
    this.onUpdateCallbacks.forEach {
        it.invoke(prop, oldValue, value, serializableData)
    }
    this.onUpdateCallbacksMap.forEach { entry ->
        if (entry.key.name == prop.name) entry.value.forEach { it.invoke(prop, oldValue, value, serializableData) }
    }
}

/**
 * Allow user to add a block of code that will be called every time a member property of [DATA] is set
 */
fun <DATA : Validatable> ConfigData<DATA>.addGlobalNotifier(notifier: (KMutableProperty1<*, *>, Any?, Any?, DATA) -> Unit) = onUpdateCallbacks.add(notifier)

fun <DATA : Validatable> ConfigData<DATA>.addNotifierOn(prop: KMutableProperty1<*, *>, notifier: (KMutableProperty1<*, *>, Any?, Any?, DATA) -> Unit) {
    this.onUpdateCallbacksMap.compute(prop) { _, value ->
        return@compute if (value == null) mutableListOf(notifier) else { value.add(notifier); value }
    }
}

/**
 * A data class representing a specific configuration
 *
 * To create instance of a ConfigData object, we use special fun called invoke that accept reified generic type
 *
 * @property serializableData An object of type [DATA] representing the configuration
 * @property relativeFilePath A [Path] object representing where the configuration file is located
 */
data class ConfigData<DATA : Validatable>(
    var serializableData: DATA,
    val relativeFilePath: Path,
    val onUpdateCallbacks: MutableList<(KMutableProperty1<*, *>, Any?, Any?, DATA) -> Unit>,
    val onUpdateCallbacksMap: MutableMap<KMutableProperty1<*, *>, MutableList<(KMutableProperty1<*, *>, Any?, Any?, DATA) -> Unit>>
) {
    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path, automaticallySave: Boolean): ConfigData<DATA> {
            val serializableData = ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath)

            val onUpdateCallbacks = mutableListOf<(KMutableProperty1<*, *>, Any?, Any?, DATA) -> Unit>()
            if (automaticallySave) onUpdateCallbacks.add { _, _, _, _ -> ConfigManager.save(serializableData, relativeFilePath) }

            val onUpdateCallbacksMap = mutableMapOf<KMutableProperty1<*, *>, MutableList<(KMutableProperty1<*, *>, Any?, Any?, DATA) -> Unit>>()

            return ConfigData(serializableData, relativeFilePath, onUpdateCallbacks, onUpdateCallbacksMap)
        }

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String, automaticallySave: Boolean): ConfigData<DATA> {
            val serializableData = ConfigManager.getOrCreateConfig<DATA>(relativeFilePath, defaultFile)

            val onUpdateCallbacks = mutableListOf<(KMutableProperty1<*, *>, Any?, Any?, DATA) -> Unit>()
            if (automaticallySave) onUpdateCallbacks.add { _, _, _, _ -> ConfigManager.save(serializableData, relativeFilePath) }

            val onUpdateCallbacksMap = mutableMapOf<KMutableProperty1<*, *>, MutableList<(KMutableProperty1<*, *>, Any?, Any?, DATA) -> Unit>>()

            return ConfigData(serializableData, relativeFilePath, onUpdateCallbacks, onUpdateCallbacksMap)
        }
    }

}
