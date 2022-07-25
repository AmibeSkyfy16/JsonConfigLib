package ch.skyfy.jsonconfig.test

import ch.skyfy.jsonconfig.core.JsonConfig
import ch.skyfy.jsonconfig.kotlinxserialization.JsonManager
import ch.skyfy.jsonconfig.test.example1.Configs
import ch.skyfy.jsonconfig.test.example1.Home
import ch.skyfy.jsonconfig.test.example1.Player
import ch.skyfy.jsonconfig.test.example1.PlayersHomesConfig

class App {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            println("PEEWEE")
            App().example1()
        }
    }
    fun example1(){
        JsonManager


        // First, you have to load the configs. After that we can access them from anywhere in the code

        // If this is the first time, then no json files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located in the jar will be copied where they are supposed to be
        JsonConfig.loadConfigs(arrayOf(Configs::class.java))

        // Now we can access the configs
        val playersHomesConfig: PlayersHomesConfig = Configs.PLAYERS_HOMES.data

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

        // When you modify a config, you have to save it
//        JsonManager.save(Configs.PLAYERS_HOMES)

        Thread.sleep(10000)
        JsonConfig.reloadConfig(Configs.PLAYERS_HOMES)

        println(Configs.PLAYERS_HOMES.data.players)
    }
}