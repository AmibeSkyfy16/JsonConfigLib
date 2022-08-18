package ch.skyfy.jsonconfiglib.example2

import ch.skyfy.jsonconfiglib.ConfigManager
import ch.skyfy.jsonconfiglib.example2.config.Configs
import ch.skyfy.jsonconfiglib.example2.config.Home
import ch.skyfy.jsonconfiglib.example2.config.Player
import ch.skyfy.jsonconfiglib.example2.config.PlayersHomesConfig
import kotlin.test.Test

class Example2 {

    @Test
    fun example2() {
        // First, you have to load the configs. After that we can access them from anywhere in the code

        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else JSON files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        // Now we can access the configs
        val playersHomesConfig: PlayersHomesConfig = Configs.PLAYERS_HOMES.`data`

        // Here we add two new players with one new home
        playersHomesConfig.players.add(
            Player(
                mutableListOf(Home(100, 100, 100, 0.0f, 0.0f, "secret base")),
                "ebb5c153-3f6f-4fb6-9062-20ac564e7490", // uuid for skyfy16 (me)
                5, // 5 for me, but by default its 3
                0, // 0 for me, but by default its 15
                0 // 0 for me, but by default its 5
            )
        )
        playersHomesConfig.players.add(
            Player(
                mutableListOf(Home(100, 100, 100, 0.0f, 0.0f, "secret base")),
                "8faaf447-227f-486d-be86-789ec2acb507"
            )
        )

        // When you modify a config, you have to save it to make sure the next time you stop and restart the server it's there
        ConfigManager.save(Configs.PLAYERS_HOMES)

        // Let's now sleep a short time, so we can edit manually players-homes.json file
        Thread.sleep(20_000L)
        // Now if we want to be sure that the things we have edited are loaded
        ConfigManager.reloadConfig(Configs.PLAYERS_HOMES)

        println(Configs.PLAYERS_HOMES.`data`.players)
    }

}