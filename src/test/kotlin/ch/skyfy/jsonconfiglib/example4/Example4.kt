package ch.skyfy.jsonconfiglib.example4

import ch.skyfy.jsonconfiglib.ConfigManager
import ch.skyfy.jsonconfiglib.addGlobalNotifier
import ch.skyfy.jsonconfiglib.example4.config.Configs
import ch.skyfy.jsonconfiglib.example4.config.Database
import ch.skyfy.jsonconfiglib.setValue
import kotlin.reflect.jvm.jvmName
import kotlin.test.Test

class Example4 {

    @Test
    fun example4() {

        // First, you have to load the configs. After that we can access them from anywhere in the code
        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        // add a global notifier. This means that every time a value is modified, the code will be called
        ch.skyfy.jsonconfiglib.example3.config.Configs.CONFIG.addGlobalNotifier { kProperty, oldValue, newValue, data ->
            println("Hey, member property: ${kProperty.name} for ${data::class.jvmName} has been modified from $oldValue to $newValue")
            println("Updating sideboard...")
            println("Updating game...")
        }

        // Now we can access the config
        val configData = Configs.CONFIG
        val database = configData.`data`

        println("port : ${database.port}")
        println("url : ${database.url}")

        println("config will be updated in 5 seconds")
        Thread.sleep(5000)

        // See below how to set a value
        // If you set the value of automaticallySave to true in Configs, the file json will be updated
        configData.setValue(database::port, database.port + 10)

        // Things you should never do
        //      - Set a value like below !!!
        //        Always use delegate to set a value, otherwise, it will not automatically save in json file and all the global notifier that
        //        you register will not be invoked
        database.port = 6

        println("port : ${database.port}")
        println("url : ${database.url}")
    }

}