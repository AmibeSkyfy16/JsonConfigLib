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
    val oldValue = kMutableProperty1.get(receiver)
    val operation = SetOperation(kMutableProperty1, receiver, oldValue, newValue, serializableData)

    kMutableProperty1.set(receiver, newValue)

    if (!receiver.validate(operation = operation, shouldThrowRuntimeException = false)) {
        kMutableProperty1.set(receiver, oldValue)
        return
    }

    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == kMutableProperty1.name) entry.value.forEach { it.invoke(operation) } }
}

/**
 * @see updateMapNested
 */
inline fun <reified DATA : Validatable, reified MAP_KEY, reified MAP_VALUE, reified MAP : MutableMap<MAP_KEY, MAP_VALUE>> ConfigData<DATA>.updateMap(kProperty1: KProperty1<DATA, MAP>, crossinline block: (MAP) -> Unit) = updateMapNested(kProperty1, serializableData, block)

/**
 * Update the [map] for the specified [kProperty1]
 *
 * Also call registered callbacks, the global registered callbacks and specific registered callback if needed
 *
 * @param kProperty1 A [KProperty1] use to identify on which property the update must be done
 * @param block A block of code use to update the [map] (put, compute, remove, etc.)
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA : Validatable, reified MAP_KEY, reified MAP_VALUE, reified MAP : MutableMap<MAP_KEY, MAP_VALUE>> ConfigData<DATA>.updateMapNested(kProperty1: KProperty1<NESTED_DATA, MAP>, receiver: NESTED_DATA, crossinline block: (MAP) -> Unit) {
    val map = kProperty1.get(receiver)
    val oldValue = map.toMutableMap()
    val operation = UpdateMutableMapOperation(kProperty1, receiver, oldValue, map, serializableData)

    block.invoke(map)

    if (!receiver.validate(operation = operation, shouldThrowRuntimeException = false)) {
        map.clear()
        map.putAll(oldValue)
        return
    }

    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == kProperty1.name) entry.value.forEach { it.invoke(operation) } }
}

/**
 * @see updateNestedMutableCollection
 */
inline fun <reified DATA : Validatable, reified MUTABLE_COLLECTION_TYPE, reified MUTABLE_COLLECTION : MutableCollection<MUTABLE_COLLECTION_TYPE>> ConfigData<DATA>.updateMutableCollection(kProperty1: KProperty1<DATA, MUTABLE_COLLECTION>, crossinline block: (MUTABLE_COLLECTION) -> Unit) = updateNestedMutableCollection(kProperty1, serializableData, block)

/**
 * Update the [MUTABLE_COLLECTION] (a MutableMap or a MutableSet) for the specified [kProperty1]
 *
 * Also call registered callbacks, the global registered callbacks and specific registered callback if needed
 *
 * @param kProperty1 A [KProperty1] use to identify on which property the update must be done
// * @param mutableCollection An object of type [MUTABLE_COLLECTION] on which the update will be done
 * @param block A block of code use to update the [MUTABLE_COLLECTION] (add, remove)
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA : Validatable, reified MUTABLE_COLLECTION_TYPE, reified MUTABLE_COLLECTION : MutableCollection<MUTABLE_COLLECTION_TYPE>> ConfigData<DATA>.updateNestedMutableCollection(kProperty1: KProperty1<NESTED_DATA, MUTABLE_COLLECTION>, receiver: NESTED_DATA, crossinline block: (MUTABLE_COLLECTION) -> Unit) {
    val mutableCollection = kProperty1.get(receiver)

    val oldValue = if (mutableCollection is MutableSet<*>) mutableCollection.toMutableSet() else mutableCollection.toMutableList()
    val operation = UpdateMutableCollectionOperation(kProperty1, receiver, oldValue, mutableCollection, serializableData)

    block.invoke(mutableCollection)

    if (!receiver.validate(operation = operation, shouldThrowRuntimeException = false)) {
        mutableCollection.clear()
        mutableCollection.addAll(oldValue)
        return
    }

    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == kProperty1.name) entry.value.forEach { it.invoke(operation) } }
}

abstract class Operation<DATA : Validatable, NESTED_DATA : Validatable> {
    abstract val prop: KProperty1<*, *>
    abstract val receiver: NESTED_DATA
    abstract val oldValue: Any?
    abstract val newValue: Any?
    abstract val origin: DATA
}

class SetOperation<DATA : Validatable, NESTED_DATA : Validatable>(
    override val prop: KMutableProperty1<*, *>,
    override val receiver: NESTED_DATA,
    override val oldValue: Any?,
    override val newValue: Any?,
    override val origin: DATA
) : Operation<DATA, NESTED_DATA>()

class UpdateMutableCollectionOperation<DATA : Validatable, NESTED_DATA : Validatable, MUTABLE_COLLECTION_TYPE>(
    override val prop: KProperty1<*, *>,
    override val receiver: NESTED_DATA,
    override val oldValue: MutableCollection<MUTABLE_COLLECTION_TYPE>,
    override val newValue: MutableCollection<MUTABLE_COLLECTION_TYPE>,
    override val origin: DATA
) : Operation<DATA, NESTED_DATA>()

class UpdateMutableMapOperation<DATA : Validatable, NESTED_DATA : Validatable, MAP_KEY, MAP_VALUE>(
    override val prop: KProperty1<*, *>,
    override val receiver: NESTED_DATA,
    override val oldValue: MutableMap<MAP_KEY, MAP_VALUE>,
    override val newValue: MutableMap<MAP_KEY, MAP_VALUE>,
    override val origin: DATA
) : Operation<DATA, NESTED_DATA>()

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
data class ConfigData<DATA : Validatable>(private var _serializableData: DATA, val relativePath: Path, val onUpdateCallbacks: MutableList<(Operation<DATA, *>) -> Unit>) {

    var serializableData by Delegates.observable(_serializableData) { _, _, newValue ->
        _serializableData = newValue
        onReloadCallbacks.forEach { it.invoke(_serializableData) }
    }

    val onUpdateCallbacksMap = mutableMapOf<KMutableProperty1<*, *>, MutableList<(Operation<DATA, *>) -> Unit>>()

    private val onReloadCallbacks: MutableList<(DATA) -> Unit> = mutableListOf()

    /**
     * Allow user to add a block of code that will be called every time a DATA is reloaded from a file
     */
    fun registerOnReload(callback: (DATA) -> Unit) = onReloadCallbacks.add(callback)

    /**
     * Allow user to add a block of code that will be called every time the specified member property is set
     */
    fun registerOnUpdateOn(prop: KMutableProperty1<*, *>, callback: (Operation<DATA, *>) -> Unit) {
        this.onUpdateCallbacksMap.compute(prop) { _, value ->
            return@compute if (value == null) mutableListOf(callback) else {
                value.add(callback); value
            }
        }
    }

    /**
     * Allow user to add a block of code that will be called every time a member property of [DATA] is set
     */
    fun registerOnUpdate(callback: (Operation<DATA, *>) -> Unit) = onUpdateCallbacks.add(callback)

    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativePath: Path, automaticallySave: Boolean) = invokeImpl(ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativePath), relativePath, automaticallySave)

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String, automaticallySave: Boolean) = invokeImpl(ConfigManager.getOrCreateConfig<DATA>(relativeFilePath, defaultFile), relativeFilePath, automaticallySave)

        /**
         * Use [DATA] default assigned values to create the default object
         */
        inline fun <reified DATA> invokeSpecial(relativePath: Path, automaticallySave: Boolean) where DATA : Validatable = invokeImpl(ConfigManager.getOrCreateConfigSpecial<DATA>(relativePath), relativePath, automaticallySave)

        inline fun <reified DATA : Validatable> invokeImpl(serializableData: DATA, relativePath: Path, automaticallySave: Boolean): ConfigData<DATA> {
            val onUpdateCallbacks = mutableListOf<(Operation<DATA, *>) -> Unit>()
            val configData = ConfigData(serializableData, relativePath, onUpdateCallbacks)
            if (automaticallySave) onUpdateCallbacks.add { ConfigManager.save(configData.serializableData, configData.relativePath) }
            return configData
        }
    }

}
