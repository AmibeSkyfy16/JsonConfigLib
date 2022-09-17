@file:Suppress("unused")

package ch.skyfy.jsonconfiglib

import java.nio.file.Path
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

/**
 * Update/Set a value for a member property of [DATA] and call all registered callbacks. See ConfigData onUpdateCallbacks
 */
inline fun <reified DATA : Validatable, reified TYPE> ConfigData<DATA>.update(prop: KMutableProperty1<DATA, TYPE>, value: TYPE) = updateNested(prop, serializableData, value)

/**
 * Use to update a [List]
 *
 * @param prop A [KProperty1] use to identify on which property the value must be set
 * @param list The [List] where to modification will be done
 * @param block A block of code use to update the list (remove or add an object of type [LIST_TYPE])
 */
inline fun <reified DATA : Validatable, reified LIST_TYPE : Validatable, reified LIST : List<LIST_TYPE>> ConfigData<DATA>.updateList(prop: KProperty1<DATA, LIST>, list: LIST, crossinline block: (LIST) -> Unit) = updateListNested(prop, list, block)

/**
 * Use to update a [Map]
 *
 * @param prop A [KProperty1] use to identify on which property the value must be set
 * @param map The [Map] where to modification will be done
 * @param block A block of code use to update the list (remove or add an object of type [MAP_VALUE])
 */
inline fun <reified DATA : Validatable, reified MAP_KEY, reified MAP_VALUE, reified MAP : Map<MAP_KEY, MAP_VALUE>> ConfigData<DATA>.updateMap(prop: KProperty1<DATA, MAP>, map: MAP, crossinline block: (MAP) -> Unit) = updateMapNested(prop, map, block)


/**
 * @see update
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA : Validatable, reified TYPE> ConfigData<DATA>.updateNested(prop: KMutableProperty1<NESTED_DATA, TYPE>, nested: NESTED_DATA, value: TYPE) {
    val operation = SetOperation(prop, prop.get(nested), value, serializableData)
    prop.set(nested, value)
    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == prop.name) entry.value.forEach { it.invoke(operation) } }
}

/**
 * @see updateList
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA, reified LIST_TYPE : Validatable, reified LIST : List<LIST_TYPE>> ConfigData<DATA>.updateListNested(prop: KProperty1<NESTED_DATA, LIST>, list: LIST, crossinline block: (LIST) -> Unit) {
    // TODO make a deep copy for list, so we can add oldValue

    val operation = UpdateListOperation(prop, list, serializableData)

    block.invoke(list) // Updating member property
    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == prop.name) entry.value.forEach { it.invoke(operation) } }
}

/**
 * @see updateMap
 */
inline fun <reified DATA : Validatable, reified NESTED_DATA, reified MAP_KEY, reified MAP_VALUE, reified MAP : Map<MAP_KEY, MAP_VALUE>> ConfigData<DATA>.updateMapNested(prop: KProperty1<NESTED_DATA, MAP>, map: MAP, crossinline block: (MAP) -> Unit) {
    // TODO make a deep copy for map, so we can add oldValue

    val operation = UpdateMapOperation(prop, map, serializableData)

    block.invoke(map) // Updating member property
    this.onUpdateCallbacks.forEach { it.invoke(operation) }
    this.onUpdateCallbacksMap.forEach { entry -> if (entry.key.name == prop.name) entry.value.forEach { it.invoke(operation) } }
}

abstract class Operation<DATA : Validatable>

class SetOperation<DATA : Validatable>(
    val prop: KMutableProperty1<*, *>,
    val oldValue: Any?,
    val newValue: Any?,
    val origin: DATA
) : Operation<DATA>()

class UpdateListOperation<DATA : Validatable, LIST_TYPE : Validatable>(
    val prop: KProperty1<*, *>,
    val newValue: List<LIST_TYPE>,
    val origin: DATA
) : Operation<DATA>()

class UpdateMapOperation<DATA : Validatable, MAP_KEY, MAP_VALUE>(
    val prop: KProperty1<*, *>,
    val newValue: Map<MAP_KEY, MAP_VALUE>,
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
            return@compute if (value == null) mutableListOf(callback) else { value.add(callback); value }
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
