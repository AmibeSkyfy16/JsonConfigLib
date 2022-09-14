package ch.skyfy.jsonconfiglib.example3

import ch.skyfy.jsonconfiglib.example3.config.Configs
import ch.skyfy.jsonconfiglib.ConfigManager
import kotlin.test.Test

class Example3 {

    @Test
    fun example3() {
//        if (0 == 0) return // don't run this test

        // First, you have to load the configs. After that we can access them from anywhere in the code

        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else JSON files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        // Now we can access the config
        val config = Configs.CONFIG.`data`

        println("dayOfAuthorizationOfThePvP : ${config.dayOfAuthorizationOfThePvP}")
        println("dayOfAuthorizationOfTheEntryInTheNether : ${config.dayOfAuthorizationOfTheEntryInTheNether}")
        println("allowEnderPearlAssault : ${config.allowEnderPearlAssault}")
    }

}