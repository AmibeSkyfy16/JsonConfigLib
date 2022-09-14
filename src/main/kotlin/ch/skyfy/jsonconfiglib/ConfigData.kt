package ch.skyfy.jsonconfiglib

import java.lang.RuntimeException
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

//inline fun <reified DATA : Validatable, reified TYPE> ConfigData<DATA>.setVal(value: TYPE, block: (DATA) -> TYPE?) {
//    val member = block.invoke(this.`data`)
//    delegates.filter { entry -> entry.key == member }.firstNotNullOf { entry ->
//        val anonymous = entry.value.invoke().get()
//        if (anonymous != null) {
//            val delegated = anonymous::class.memberProperties.first()
//            if (delegated is KMutableProperty1) {
//                println("set with success")
//                delegated.setter.call(anonymous, value)
//            }
//        }
//    }
//}

/**
 * Set the value for the chosen member property returned by [block] by accessing her delegate
 */
inline fun <reified DATA : Validatable, reified TYPE> ConfigData<DATA>.setVal2(crossinline block: (DATA) -> TYPE, value: TYPE) {
    val member = block.invoke(this.`data`)
    delegates.filter { entry -> entry.key == member }.firstNotNullOf { entry ->
        val anonymous = entry.value.invoke().get()
        val delegated = anonymous::class.memberProperties.first()
        if (delegated is KMutableProperty1) {
            if (value!!::class !== TYPE::class)
                throw RuntimeException("The type you try to set doesn't match the type of the member property")
            delegated.setter.call(anonymous, value)
            println("set")
        }
    }
}

/**
 * Allow user to add a block of code that will be called every time a member property of [DATA] is set
 */
 fun < DATA : Validatable> ConfigData<DATA>.addGlobalNotifier( notifier: (KProperty<*>, Any?, Any?) -> Unit) {
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
    val delegates: MutableMap<Any?, () -> AtomicReference<Any>>,
    val onChangeCallbacks: MutableMap<Any, MutableList<(KProperty<*>, Any?, Any?) -> Unit>>
) {

    data class InitializeObject(
        val delegates: MutableMap<Any?, () -> AtomicReference<Any>>,
        val onChangeCallbacks: MutableMap<Any, MutableList<(KProperty<*>, Any?, Any?) -> Unit>>
    )

    companion object {

        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path, automaticallySave: Boolean): ConfigData<DATA> {
            val `data` = ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath)
            val initializeObject = initialize(`data`, relativeFilePath,automaticallySave)
            return ConfigData(`data`, relativeFilePath, initializeObject.delegates, initializeObject.onChangeCallbacks)
        }

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String, automaticallySave: Boolean): ConfigData<DATA> {
            val `data` = ConfigManager.getOrCreateConfig<DATA>(relativeFilePath, defaultFile)
            val initializeObject = initialize(`data`, relativeFilePath,automaticallySave)
            return ConfigData(`data`, relativeFilePath, initializeObject.delegates, initializeObject.onChangeCallbacks)
        }

        /**
         * Create delegate for each member property found in [DATA]
         */
        inline fun <reified DATA : Validatable> initialize(`data`: DATA,relativeFilePath: Path, automaticallySave: Boolean): InitializeObject {
            val delegates = mutableMapOf<Any?, () -> AtomicReference<Any>>()
            val onChangeCallbacks: MutableMap<Any, MutableList<(KProperty<*>, Any?, Any?) -> Unit>> = mutableMapOf()
            DATA::class.declaredMemberProperties.forEach { kProperty1 ->
                val anonymous = object {
                    @Suppress("unused")
                    var delegated by Delegates.observable(kProperty1.get(`data`)) { kProperty, oldValue, newValue ->
                        if(kProperty1 is KMutableProperty1){
                            kProperty1.setter.call(`data`, newValue)
                        }
                        onChangeCallbacks.filter { entry -> entry.key == kProperty1.get(`data`) }.firstNotNullOf { entry ->
                            entry.value.forEach { it.invoke(kProperty, oldValue, newValue) }
                        }
                    }
                }
                delegates[kProperty1.get(`data`)] = { AtomicReference(anonymous) }

                val mutableListOf: MutableList<(KProperty<*>,Any?, Any?)->Unit> = mutableListOf()
                if(automaticallySave){
                    mutableListOf.add { _, _, _ ->
                        println("save")
                        ConfigManager.save(`data`, relativeFilePath)
                    }
                }

                onChangeCallbacks[kProperty1.get(`data`) as Any] = mutableListOf
            }
            return InitializeObject(delegates, onChangeCallbacks)
        }
    }

    init {
//        // Add a first global notifier that will be used to save data to the configuration file
//        addGlobalNotifier { kProperty, any, any2 ->
//            ConfigManager.save(this)
//        }
    }

}
