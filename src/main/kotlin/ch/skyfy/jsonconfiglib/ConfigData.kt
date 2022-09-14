package ch.skyfy.jsonconfiglib

import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.Delegates
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

/**
 * Set the value for the chosen member property by accessing her delegate
 */
inline fun <reified DATA : Validatable, reified TYPE> ConfigData<DATA>.setValue(kProperty0: KMutableProperty0<TYPE>, value: TYPE) {
    delegates.forEach { entry ->
        if (entry.key.name == kProperty0.name) {
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
    ) {
        companion object {
            inline operator fun <reified DATA : Validatable> invoke(
                delegates: MutableMap<KProperty1<*, *>, () -> AtomicReference<Any>>,
                onChangeCallbacks: MutableMap<KProperty1<*, *>, MutableList<(KProperty<*>, Any?, Any?, DATA) -> Unit>>
            ) = InitializeObject(delegates, onChangeCallbacks)
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

        fun getBasePackageName(string: String) : String{
            var count = 0
            string.toCharArray().forEachIndexed { index, c ->
                if(c.toString() == "."){
                    if(count == 1){
                        @Suppress("UnnecessaryVariable") val basePackageName = string.substring(0, index)
                        return basePackageName
                    }
                    count++
                }
            }
            return string
        }

        /**
         * Create delegate for each member property found in [DATA]
         */
        inline fun <reified DATA : Validatable> initialize(`data`: DATA, relativeFilePath: Path, automaticallySave: Boolean): InitializeObject<DATA> {

            val delegates = mutableMapOf<KProperty1<*, *>, () -> AtomicReference<Any>>()
            val onChangeCallbacks: MutableMap<KProperty1<*, *>, MutableList<(KProperty<*>, Any?, Any?, DATA) -> Unit>> = mutableMapOf()

            val basePackageName = getBasePackageName(DATA::class.java.`package`.name)

            DATA::class.memberProperties.forEach {kp1 ->
                kp1.returnType.arguments.forEach {kTypeProjection ->
                    val nestedMemberPackageName = kTypeProjection.toString()

                    if(getBasePackageName(nestedMemberPackageName) == basePackageName) {
                        val JClass = kTypeProjection.type?.javaClass?.kotlin
                        JClass?.memberProperties?.forEach { kp2 ->

                            println()
                        }
                        println()
                    }
                }
//                val all = it.get(data)!!::class.allSupertypes
//                all.forEach { p ->
//                    p.arguments.forEach { kTypeProjection ->
//                        val t = kTypeProjection.type
//                        println()
//                    }
//                }
//
//                val nestMembers = it.get(data)?.javaClass?.nestMembers
//                nestMembers?.forEach { jClass ->
//                    val p2 = jClass.`package`
//                    println()
//                }
                println()
            }

//            doStuff(data, relativeFilePath, automaticallySave, delegates, onChangeCallbacks)

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
        }

        inline fun <reified DATA : Validatable> doStuff(
            data: DATA,
            relativeFilePath: Path,
            automaticallySave: Boolean,
            delegates: MutableMap<KProperty1<*, *>, () -> AtomicReference<Any>>,
            onChangeCallbacks: MutableMap<KProperty1<*, *>, MutableList<(KProperty<*>, Any?, Any?, DATA) -> Unit>>
        ) {
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
        }
    }

}
