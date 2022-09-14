package ch.skyfy.jsonconfiglib.example3

import ch.skyfy.jsonconfiglib.ConfigManager
import ch.skyfy.jsonconfiglib.addGlobalNotifier
import ch.skyfy.jsonconfiglib.example3.config.Configs
import kotlin.reflect.jvm.jvmName
import kotlin.test.Test

class Example3 {

    @Test
    fun example3() {

        // First, you have to load the configs. After that we can access them from anywhere in the code
        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(ch.skyfy.jsonconfiglib.example4.config.Configs::class.java))

        // add a global notifier. This means that every time a value is modified, the code will be called
        Configs.CONFIG.addGlobalNotifier { kProperty, oldValue, newValue, data ->
            println("Hey, member property: ${kProperty.name} for ${data::class.jvmName} has been modified from $oldValue to $newValue")
            println("Updating sideboard...")
            println("Updating game...")
        }

        // Now we can access the config
        // Now we can access the config
        val configData = Configs.CONFIG
        val config = configData.`data`

        println("dayOfAuthorizationOfThePvP : ${config.dayOfAuthorizationOfThePvP}")
        println("dayOfAuthorizationOfTheEntryInTheNether : ${config.dayOfAuthorizationOfTheEntryInTheNether}")
        println("allowEnderPearlAssault : ${config.allowEnderPearlAssault}")

        // See below how to set a value
        // If you set the value of automaticallySave to true in Configs, the file json will be updated
        // In this example, I decided that the user only can modify the json file when the minecraft server isn't started
        // So you cannot modify the value from the code
        /* configData.setValue(Config::allowEnderPearlAssault, false) */

        // Things you should never do
        //      - Set a value like below !!!
        //        Always use delegate to set a value, otherwise, it will not automatically save in json file and all the global notifier that
        //        you register will not be invoked
        /* config.allowEnderPearlAssault = false */

    }

}