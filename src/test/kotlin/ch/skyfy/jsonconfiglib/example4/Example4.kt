package ch.skyfy.jsonconfiglib.example4

import ch.skyfy.jsonconfiglib.*
import ch.skyfy.jsonconfiglib.example4.config.Configs
import ch.skyfy.jsonconfiglib.example4.config.Database
import kotlin.test.Test

class Example4 {

    @Test
    fun example4() {

        // First, you have to load the configs. After that we can access them from anywhere in the code
        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        // add a global notifier. This means that every time a value is modified, the code will be called
        Configs.CONFIG.addGlobalNotifier { _, _, _,data->
            println("Hey, a member property of ${data.port} has been set")
            println("Updating sideboard with newValue ...")
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
        configData.setValue(Database::port, database.port + 10)

        println("port : ${database.port}")
        println("url : ${database.url}")
    }

}

private fun Int.setValue(i: Int) {

}
