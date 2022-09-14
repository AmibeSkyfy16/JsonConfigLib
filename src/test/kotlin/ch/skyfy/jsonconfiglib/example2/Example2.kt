package ch.skyfy.jsonconfiglib.example2

import ch.skyfy.jsonconfiglib.ConfigManager
import ch.skyfy.jsonconfiglib.addGlobalNotifier
import ch.skyfy.jsonconfiglib.example2.config.Configs
import ch.skyfy.jsonconfiglib.example2.config.Home
import ch.skyfy.jsonconfiglib.example2.config.Player
import ch.skyfy.jsonconfiglib.example2.config.PlayersHomesConfig
import ch.skyfy.jsonconfiglib.setValue
import kotlin.reflect.jvm.jvmName
import kotlin.test.Test

class Example2 {

    @Test
    fun example2() {

        // First, you have to load the configs. After that we can access them from anywhere in the code
        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        // add a global notifier. This means that every time a value is modified, the code will be called
        Configs.PLAYERS_HOMES.addGlobalNotifier { kProperty, oldValue, newValue, data ->
            println("Hey, member property: ${kProperty.name} for ${data::class.jvmName} has been modified from $oldValue to $newValue")
            println("Updating sideboard...")
            println("Updating game...")
        }

        // Now we can access the config
        val configData = Configs.PLAYERS_HOMES
        val playersHomesConfig = configData.`data`

        println("Number of player home currently registered : ${playersHomesConfig.players.size}")

        println("config will be updated in 10 seconds")
        Thread.sleep(10_000)

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

        // When you used a list, you will have to manually save the configuration
        // There is two-way to save data to the file
        // First with computeAndSave
        ConfigManager.computeAndSave(configData) {
            it.players.add(
                Player(
                    mutableListOf(Home(100, 100, 100, 0.0f, 0.0f, "secret base")),
                    "ebb5c153-3f6f-4fb6-9062-20ac564e7490", // uuid for skyfy16 (me)
                    5, // 5 for me, but by default its 3
                    0, // 0 for me, but by default its 15
                    0 // 0 for me, but by default its 5
                )
            )
            it.players.add(
                Player(
                    mutableListOf(Home(100, 100, 100, 0.0f, 0.0f, "secret base")),
                    "8faaf447-227f-486d-be86-789ec2acb507"
                )
            )
        }

        // Second with save
        ConfigManager.save(Configs.PLAYERS_HOMES)

        println("Number of player home currently registered : ${playersHomesConfig.players.size}")

        val o = Player::cooldown
        val firstPlayer = playersHomesConfig.players.first()
        configData.setValue(firstPlayer::cooldown, 1000)

        println("sleeping 20 seconds, then reloading")
        // Let's now sleep a short time, so we can edit manually players-homes.json file
        Thread.sleep(20_000L)
        // Now if we want to be sure that the things we have edited are loaded
        ConfigManager.reloadConfig(Configs.PLAYERS_HOMES)

        println(Configs.PLAYERS_HOMES.`data`.players)
    }

}