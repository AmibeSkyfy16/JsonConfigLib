package ch.skyfy.jsonconfiglib.example4

import ch.skyfy.jsonconfiglib.ConfigManager
import ch.skyfy.jsonconfiglib.example4.config.Configs
import kotlin.test.Test

class Example4 {

    @Test
    fun example4() {
//        if (0 == 0) return // don't run this test

        // First, you have to load the configs. After that we can access them from anywhere in the code

        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        // Now we can access the config
        var config = Configs.CONFIG.data

        println("port : ${config.port}")
        println("url : ${config.url}")

        ConfigManager.computeAndSave(Configs.CONFIG, {data -> data.port = 3307})

        config = Configs.CONFIG.data

        println("port : ${config.port}")
        println("url : ${config.url}")
    }

}