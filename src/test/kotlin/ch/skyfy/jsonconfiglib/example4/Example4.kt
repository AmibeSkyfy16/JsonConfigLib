package ch.skyfy.jsonconfiglib.example4

import ch.skyfy.jsonconfiglib.*
import ch.skyfy.jsonconfiglib.SetOperation
import ch.skyfy.jsonconfiglib.example4.config.Configs
import ch.skyfy.jsonconfiglib.example4.config.Database
import kotlin.reflect.jvm.jvmName
import kotlin.test.Test

class Example4 {

    @Suppress("UNUSED_VARIABLE")
    @Test
    fun example4() {

        // First, you have to load the configs. After that we can access them from anywhere in the code
        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        // add a global notifier. This means that every time the config is updated, the code will be called
        Configs.CONFIG.addGlobalNotifier { operation ->
            if(operation is SetOperation<Database>) {
                val kMutableProperty1 = operation.prop
                val oldValue = operation.oldValue
                val newValue = operation.newValue
                val database = operation.origin
                println("Hey, member property: ${kMutableProperty1.name} for ${database::class.jvmName} has been set from $oldValue to $newValue")
                println("Updating sideboard...")
                println("Updating game...")
            }
        }

        // You can also add a notifier on a custom property
        // Here we add a notifier on url property, mean each time url is set, the code below will be invoked
        Configs.CONFIG.addNotifierOn(Database::url) { operation ->
            if(operation is SetOperation<Database>) {
                val kMutableProperty1 = operation.prop
                val oldValue = operation.oldValue
                val newValue = operation.newValue
                val database = operation.origin
                println("Hey, url has been modified to $newValue")
            }
        }

        // Now we can access the config
        val configData = Configs.CONFIG
        val database = configData.serializableData

        println("port : ${database.port}")
        println("url : ${database.url}")

        // Updating is easy, just call update extension fun on a ConfigData<DATA> object and specify which property you want to update
        Configs.CONFIG.update(Database::port, 3307)
        Configs.CONFIG.update(Database::url, "127.0.0.1")

        // Another example
//        Configs.CONFIG.update<Database, Database, String>(Database::url, database,"127.0.0.1")

        println("port : ${database.port}")
        println("url : ${database.url}")
    }

}