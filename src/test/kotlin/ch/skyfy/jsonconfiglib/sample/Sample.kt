package ch.skyfy.jsonconfiglib.sample

import ch.skyfy.jsonconfiglib.*
import ch.skyfy.jsonconfiglib.example5.config.AConfig
import ch.skyfy.jsonconfiglib.sample.config.Configs
import ch.skyfy.jsonconfiglib.sample.config.Home
import ch.skyfy.jsonconfiglib.sample.config.Player
import ch.skyfy.jsonconfiglib.sample.config.PlayersHomesConfig
import kotlin.test.Test

class Sample {

    @Test
    fun sample() {
        // First, you have to load the configs. After that we can access them from anywhere in the code
        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        // Now we can access the config
        val configData = Configs.PLAYERS_HOMES
        val playersHomesConfig = configData.serializableData

        // Registering callback that will be called when a config is modified, or a file is reloaded
        registers(configData)

        addPlayerInPlayersHomesConfig(configData)

        // Adding a no valid home, a warn message will appear in console and the added no valid home will be discarded. Also, file will not be saved
        addAnNoValidHome(configData)

        deleteSomePlayersAndSomeHomes(configData)

        // Setting the cooldown for the first player
        configData.updateNested(Player::cooldown, configData.serializableData.players.first(), 3)

        val configData2 = Configs.PLAYERS_HOMES_FROM_A_FILE

        ConfigManager.reloadConfig(configData)
    }

    private fun registers(configData: ConfigData<PlayersHomesConfig>) {
        configData.registerOnReload {
            println("The configuration has been reloaded from file: ${configData.relativePath.any()}")
        }

        configData.registerOnUpdate {
            println("An update has been done!")
            when (it) {
                is UpdateMutableMapOperation<PlayersHomesConfig, *, *, *> -> {
                    println("And it was about a map object")
                }

                is UpdateMutableCollectionOperation<PlayersHomesConfig, *, *> -> {
                    println("And it was about a mutableCollection object")
                }

                is SetOperation<PlayersHomesConfig, *> -> {
                    println("And it was about a member property of ${it.prop} on object ${it::receiver.name}")
                }
            }
        }

        // Will be call only if the cooldown property is set
        configData.registerOnUpdateOn(Player::cooldown){
            println("The property cooldown has been modified")
        }
    }

    private fun addPlayerInPlayersHomesConfig(configData: ConfigData<PlayersHomesConfig>) {

        configData.updateMutableCollection<PlayersHomesConfig, Player, MutableSet<Player>>(PlayersHomesConfig::players) {
            // Adding a first player with one home called secret base
            it.add(
                Player(
                    mutableListOf(
                        Home(100, 100, 100, 0.0f, 0.0f, "secret base")
                    ),
                    "ebb5c153-3f6f-4fb6-9062-20ac564e7490", 5, 0, 0
                )
            )

            // Adding a second player with one home called noob's base. The default value will be used for maxHomes, cooldown and standStill
            it.add(
                Player(
                    mutableListOf(
                        Home(100, 100, 100, 0.0f, 0.0f, "noob's base")
                    ),
                    "8faaf447-227f-486d-be86-789ec2acb507"
                )
            )
        }

        // Adding a second home for the first player we previously added
        configData.updateNestedMutableCollection<PlayersHomesConfig, Player, Home, MutableList<Home>>(Player::homes, configData.serializableData.players.first()){
            it.add(Home(1000, 100, 1000, 0.0f, 0.0f, "Another Secret Base"))
        }

    }

    private fun addAnNoValidHome(configData: ConfigData<PlayersHomesConfig>) {
        // Adding a no valid home for the first player we previously added
        configData.updateNestedMutableCollection<PlayersHomesConfig, Player, Home, MutableList<Home>>(Player::homes, configData.serializableData.players.first()){
            it.add(Home(10_000, 100, 1000, 0.0f, 0.0f, "No valid home because x is 10_000"))
        }
    }

    private fun deleteSomePlayersAndSomeHomes(configData: ConfigData<PlayersHomesConfig>) {
        configData.updateMutableCollection<PlayersHomesConfig, Player, MutableSet<Player>>(PlayersHomesConfig::players) {
            val firstPlayer = it.first()
            it.removeAll { player ->  player != firstPlayer}
        }

        configData.updateNestedMutableCollection<PlayersHomesConfig, Player, Home, MutableList<Home>>(Player::homes, configData.serializableData.players.first()){
            val firstHome = it.first()
            it.removeAll { home -> home != firstHome }
        }
    }

}