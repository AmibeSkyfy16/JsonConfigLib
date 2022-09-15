package ch.skyfy.jsonconfiglib.example2

import ch.skyfy.jsonconfiglib.*
import ch.skyfy.jsonconfiglib.example2.config.Configs
import ch.skyfy.jsonconfiglib.example2.config.Home
import ch.skyfy.jsonconfiglib.example2.config.Player
import ch.skyfy.jsonconfiglib.example2.config.PlayersHomesConfig
import ch.skyfy.jsonconfiglib.example4.config.Database
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
        Configs.PLAYERS_HOMES.addGlobalNotifier { kMutableProperty1, oldValue, newValue, database ->
            println("Hey, member property: ${kMutableProperty1.name} for ${database::class.jvmName} has been modified from $oldValue to $newValue")
            println("Updating sideboard...")
            println("Updating game...")
        }

        // You can also add a notifier on a custom property
        // Here we add a notifier on url property, mean each time url is set, the code below will be invoked
        Configs.PLAYERS_HOMES.addNotifierOn(Player::maxHomes) { kMutableProperty1, oldValue, newValue, playersHomesConfig ->
            println("Hey, maxHomes has been modified to $newValue")
        }

        // Now we can access the config
        val configData = Configs.PLAYERS_HOMES
        val playersHomesConfig = configData.serializableData

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

        // Here we update maxHome property to 100 for first player in the list
        val playerToModify = playersHomesConfig.players.first()
        @Suppress("RemoveExplicitTypeArguments")
        configData.update<PlayersHomesConfig, Player, Int>(Player::maxHomes, playerToModify, 100)

        // When you modify a config, you have to save it to make sure the next time you stop and restart the server it's there
//        ConfigManager.save(Configs.PLAYERS_HOMES)

        // Let's now sleep a short time, so we can edit manually players-homes.json file
        Thread.sleep(20_000L)
        // Now if we want to be sure that the things we have edited are loaded
        ConfigManager.reloadConfig(Configs.PLAYERS_HOMES)

        println(Configs.PLAYERS_HOMES.serializableData.players)
    }

}