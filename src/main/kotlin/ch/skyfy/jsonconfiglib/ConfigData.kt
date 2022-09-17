@file:Suppress("unused")

package ch.skyfy.jsonconfiglib

import java.nio.file.Path
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

/**
 * @see updateNested
 */
inline fun <reified DATA : Validatable, reified TYPE> ConfigData<DATA>.update(kMutableProperty1: KMutableProperty1<DATA, TYPE>, value: TYPE) = updateNested(kMutableProperty1, serializableData, value)

/**
 * Set the [newValue] for the specified [kMutableProperty1] on the [receiver] object passed as parameter
 *
 * Also call registered callbacks, the global registered callbacks and specific registered callback if needed
 *
 * @param kMutableProperty1 A [KMutableProperty1] use to identify on which property the value must be set
 * @param receiver An object of type [NESTED_DATA] on which the value need to be set
 * @param newValue An object of type [TYPE] which will be used as the new value
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA : Validatable, reified TYPE> ConfigData<DATA>.updateNested(kMutableProperty1: KMutableProperty1<NESTED_DATA, TYPE>, receiver: NESTED_DATA, newValue: TYPE) {
    val operation = SetOperation(kMutableProperty1, receiver, kMutableProperty1.get(receiver), newValue, serializableData)
    kMutableProperty1.set(receiver, newValue)
    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == kMutableProperty1.name) entry.value.forEach { it.invoke(operation) } }
}


/**
 * @see updateMapNested
 */
inline fun <reified DATA : Validatable, reified MAP_KEY, reified MAP_VALUE, reified MAP : Map<MAP_KEY, MAP_VALUE>> ConfigData<DATA>.updateMap(kProperty1: KProperty1<DATA, MAP>, crossinline block: (MAP) -> Unit) = updateMapNested(kProperty1, kProperty1.get(serializableData), block)

/**
 * Update the [map] for the specified [kProperty1]
 *
 * Also call registered callbacks, the global registered callbacks and specific registered callback if needed
 *
 * @param kProperty1 A [KProperty1] use to identify on which property the update must be done
 * @param map An object of type [MAP] on which the update will be done
 * @param block A block of code use to update the [map] (put, compute, remove, etc.)
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA, reified MAP_KEY, reified MAP_VALUE, reified MAP : Map<MAP_KEY, MAP_VALUE>> ConfigData<DATA>.updateMapNested(kProperty1: KProperty1<NESTED_DATA, MAP>, map: MAP, crossinline block: (MAP) -> Unit) {
    // TODO make a deep copy for map, so we can add oldValue
    val operation = UpdateMapOperation(kProperty1, map, serializableData)
    block.invoke(map)
    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == kProperty1.name) entry.value.forEach { it.invoke(operation) } }
}


/**
 * @see updateIterableNested
 */
inline fun <reified DATA : Validatable, reified ITERABLE_TYPE, reified ITERABLE : Iterable<ITERABLE_TYPE>> ConfigData<DATA>.updateIterable(kProperty1: KProperty1<DATA, ITERABLE>, crossinline block: (ITERABLE) -> Unit) = updateIterableNested(kProperty1, kProperty1.get(serializableData), block)

/**
 * Update the [iterable] for the specified [kProperty1]
 *
 * Also call registered callbacks, the global registered callbacks and specific registered callback if needed
 *
 * @param kProperty1 A [KProperty1] use to identify on which property the update must be done
 * @param iterable An object of type [ITERABLE] on which the update will be done
 * @param block A block of code use to update the [iterable] (add, remove)
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA, reified ITERABLE_TYPE, reified ITERABLE : Iterable<ITERABLE_TYPE>> ConfigData<DATA>.updateIterableNested(kProperty1: KProperty1<NESTED_DATA, ITERABLE>, iterable: ITERABLE, crossinline block: (ITERABLE) -> Unit) {
    // TODO make a deep copy for iterable, so we can add oldValue
    val operation = UpdateIterableOperation(kProperty1, iterable, serializableData)
    block.invoke(iterable)
    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == kProperty1.name) entry.value.forEach { it.invoke(operation) } }
}


/**
 * @see updateCustomNested
 */
inline fun <reified DATA : Validatable, reified CUSTOM_TYPE> ConfigData<DATA>.updateCustom(kProperty1: KProperty1<DATA, CUSTOM_TYPE>, customObject: CUSTOM_TYPE, crossinline block: (CUSTOM_TYPE) -> Unit) = updateCustomNested(kProperty1, customObject, block)

/**
 * Use only this fun in the case of the others one like [updateNested], [updateIterable] don't work
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA, reified CUSTOM_TYPE> ConfigData<DATA>.updateCustomNested(kProperty1: KProperty1<NESTED_DATA, CUSTOM_TYPE>, custom: CUSTOM_TYPE, crossinline block: (CUSTOM_TYPE) -> Unit) {
    // TODO make a deep copy for map, so we can add oldValue
    val operation = UpdateCustomOperation(kProperty1, custom, serializableData)
    block.invoke(custom)
    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == kProperty1.name) entry.value.forEach { it.invoke(operation) } }
}

abstract class Operation<DATA : Validatable>

class SetOperation<DATA : Validatable, NESTED_DATA : Validatable>(
    val prop: KMutableProperty1<*, *>,
    val receiver: NESTED_DATA,
    val oldValue: Any?,
    val newValue: Any?,
    val origin: DATA
) : Operation<DATA>()

class UpdateIterableOperation<DATA : Validatable, ITERABLE_TYPE>(
    val prop: KProperty1<*, *>,
    val newValue: Iterable<ITERABLE_TYPE>,
    val origin: DATA
) : Operation<DATA>()

class UpdateMapOperation<DATA : Validatable, MAP_KEY, MAP_VALUE>(
    val prop: KProperty1<*, *>,
    val newValue: Map<MAP_KEY, MAP_VALUE>,
    val origin: DATA
) : Operation<DATA>()

class UpdateCustomOperation<DATA : Validatable, CUSTOM_TYPE>(
    val prop: KProperty1<*, *>,
    val newValue: CUSTOM_TYPE,
    val origin: DATA
) : Operation<DATA>()

/**
 * A serializable data class representing a specific configuration
 *
 * To create instance of a ConfigData object, we use special fun called invoke that accept reified generic type
 *
 * @property _serializableData An object of type [DATA] representing the configuration
 * @property relativePath A [Path] object representing where the configuration file is located
 * @property onUpdateCallbacks A [List] of callbacks which will be called every time a member property of [DATA] is updated
 * @property onUpdateCallbacksMap A [Map] of callbacks for specific member property of [DATA] which will be called whenever one of them is updated
 */
data class ConfigData<DATA : Validatable>(
    private var _serializableData: DATA,
    val relativePath: Path,
    val onUpdateCallbacks: MutableList<(Operation<DATA>) -> Unit>
) {

    var serializableData by Delegates.observable(_serializableData) { _, _, newValue ->
        _serializableData = newValue
        onReloadCallbacks.forEach { it.invoke(_serializableData) }
    }

    val onUpdateCallbacksMap = mutableMapOf<KMutableProperty1<*, *>, MutableList<(Operation<DATA>) -> Unit>>()

    private var onReloadCallbacks: MutableList<(DATA) -> Unit> = mutableListOf()

    /**
     * Allow user to add a block of code that will be called every time a DATA is reloaded from a file
     */
    fun registerOnReload(callback: (DATA) -> Unit) = onReloadCallbacks.add(callback)

    /**
     * Allow user to add a block of code that will be called every time the specified member property is set
     */
    fun registerOnUpdateOn(prop: KMutableProperty1<*, *>, callback: (Operation<DATA>) -> Unit) {
        this.onUpdateCallbacksMap.compute(prop) { _, value ->
            return@compute if (value == null) mutableListOf(callback) else {
                value.add(callback); value
            }
        }
    }

    /**
     * Allow user to add a block of code that will be called every time a member property of [DATA] is set
     */
    fun registerOnUpdate(callback: (Operation<DATA>) -> Unit) = onUpdateCallbacks.add(callback)

    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativePath: Path, automaticallySave: Boolean) =
            invokeImpl(ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativePath), relativePath, automaticallySave)

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String, automaticallySave: Boolean) =
            invokeImpl(ConfigManager.getOrCreateConfig<DATA>(relativeFilePath, defaultFile), relativeFilePath, automaticallySave)

        inline fun <reified DATA : Validatable> invokeImpl(serializableData: DATA, relativePath: Path, automaticallySave: Boolean): ConfigData<DATA> {
            val onUpdateCallbacks = mutableListOf<(Operation<DATA>) -> Unit>()
            if (automaticallySave) onUpdateCallbacks.add { ConfigManager.save(serializableData, relativePath) }
            return ConfigData(serializableData, relativePath, onUpdateCallbacks)
        }
    }

}
