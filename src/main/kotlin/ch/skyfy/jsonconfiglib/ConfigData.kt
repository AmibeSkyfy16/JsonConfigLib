package ch.skyfy.jsonconfiglib

import java.nio.file.Path
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty1

inline fun <reified DATA : Validatable, reified TYPE> ConfigData<DATA>.update(prop: KMutableProperty1<DATA, TYPE>, value: TYPE) = update(prop, serializableData, value)

inline fun <reified DATA : Validatable, reified NESTED_DATA : Validatable, reified TYPE> ConfigData<DATA>.update(prop: KMutableProperty1<NESTED_DATA, TYPE>, nested: NESTED_DATA, value: TYPE) {
    val oldValue = prop.get(nested)
    prop.set(nested, value)

    val operation = SetOperation(prop, oldValue, value, serializableData)

    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == prop.name) entry.value.forEach { it.invoke(operation) } }
}

inline fun <reified DATA : Validatable, reified NESTED_DATA, reified LIST_TYPE : Validatable, reified TYPE : List<LIST_TYPE>> ConfigData<DATA>.updateList(
    prop: KMutableProperty1<NESTED_DATA, TYPE>,
    type: TYPE,
    crossinline block: (TYPE) -> Unit
) {

    // TODO make a deep copy for type, so we can add oldValue
//    member.map{it.clone}.toList()

    val operation = UpdateListOperation(prop, type, serializableData)

    block.invoke(type) // Updating code
    this.onUpdateCallbacks.forEach {
        it.invoke(operation)
    }
    this.onUpdateCallbacksMap.forEach { entry ->
        if (entry.key.name == prop.name) entry.value.forEach { it.invoke(operation) }
    }
}

/**
 * Allow user to add a block of code that will be called every time a member property of [DATA] is set
 */
fun <DATA : Validatable> ConfigData<DATA>.addGlobalNotifier(notifier: (Operation<DATA>) -> Unit) = onUpdateCallbacks.add(notifier)

fun <DATA : Validatable> ConfigData<DATA>.addNotifierOn(prop: KMutableProperty1<*, *>, notifier: (Operation<DATA>) -> Unit) {
    this.onUpdateCallbacksMap.compute(prop) { _, value ->
        return@compute if (value == null) mutableListOf(notifier) else { value.add(notifier); value }
    }
}

abstract class Operation<DATA : Validatable>

class SetOperation<DATA : Validatable>(
    val prop: KMutableProperty1<*, *>,
    val oldValue: Any?,
    val newValue: Any?,
    val origin: DATA
) : Operation<DATA>()

class UpdateListOperation<DATA : Validatable, LIST_TYPE : Validatable>(
    val prop: KMutableProperty1<*, *>,
    val newValue: List<LIST_TYPE>,
    val origin: DATA
) : Operation<DATA>()

/**
 * A data class representing a specific configuration
 *
 * To create instance of a ConfigData object, we use special fun called invoke that accept reified generic type
 *
 * @property _serializableData An object of type [DATA] representing the configuration
 * @property relativeFilePath A [Path] object representing where the configuration file is located
 */
data class ConfigData<DATA : Validatable>(
    private var _serializableData: DATA,
    val relativeFilePath: Path,
    val onUpdateCallbacks: MutableList<(Operation<DATA>) -> Unit>,
    val onUpdateCallbacksMap: MutableMap<KMutableProperty1<*, *>, MutableList<(Operation<DATA>) -> Unit>>
) {

    var serializableData by Delegates.observable(_serializableData) { _, _, newValue ->
        _serializableData = newValue
        onReloadCallbacks.forEach { it.invoke(_serializableData) }
    }

    private var onReloadCallbacks: MutableList<(DATA) -> Unit> = mutableListOf()

    fun registerOnReloadCallback(callback: (DATA) -> Unit) = onReloadCallbacks.add(callback)

    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path, automaticallySave: Boolean) =
            invokeImpl(ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath), relativeFilePath, automaticallySave)

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String, automaticallySave: Boolean) =
            invokeImpl(ConfigManager.getOrCreateConfig<DATA>(relativeFilePath, defaultFile), relativeFilePath, automaticallySave)

        inline fun <reified DATA : Validatable> invokeImpl(serializableData: DATA, relativeFilePath: Path, automaticallySave: Boolean): ConfigData<DATA> {
            val onUpdateCallbacks = mutableListOf<(Operation<DATA>) -> Unit>()
            if (automaticallySave) onUpdateCallbacks.add { ConfigManager.save(serializableData, relativeFilePath) }
            val onUpdateCallbacksMap = mutableMapOf<KMutableProperty1<*, *>, MutableList<(Operation<DATA>) -> Unit>>()
            return ConfigData(serializableData, relativeFilePath, onUpdateCallbacks, onUpdateCallbacksMap)
        }
    }

}
