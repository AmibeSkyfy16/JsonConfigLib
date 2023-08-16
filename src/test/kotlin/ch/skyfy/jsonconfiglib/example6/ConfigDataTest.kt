package ch.skyfy.jsonconfiglib.example6

import ch.skyfy.jsonconfiglib.*
import ch.skyfy.jsonconfiglib.example5.config.AConfig
import kotlin.test.Test

class ConfigDataTest {

    @Test
    fun test_update_fun() {
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

//        InnerPlayer(RankTypes.DIAMOND, 1009)

//        val configData = Configs.CONFIG
//        val serializableData = configData.serializableData

//        configData.registerOnUpdate {
//            println("An update has been done!")
//            when (it) {
//                is UpdateMutableMapOperation<ClashOfClansConfig, *, *, *> -> {
//                    println("And it was about a map object")
//                }
//
//                is UpdateMutableCollectionOperation<ClashOfClansConfig, *, *> -> {
//                    println("And it was about a itarable object")
//                }
//
////
//                is SetOperation<ClashOfClansConfig, *> -> {
//                    println("And it was about a member property of ${it.prop} on object ${it::receiver.name}")
//                }
//            }
//        }
//
//        configData.updateNested<ClashOfClansConfig, Player, Int>(Player::playerLevels, serializableData.player, 101)
//
//        configData.updateNestedMutableCollection<ClashOfClansConfig, MainVillage, GoldMine, MutableCollection<GoldMine>>(
//            MainVillage::goldMines,
//            serializableData.mainVillage,
//            serializableData.mainVillage.goldMines
//        ) {
//            it.add(
//                GoldMine(10, 1000, mutableMapOf(SpecialUpgradingResourceTypes.MOZAN to 12))
//            )
//        }
//
//
//        ConfigManager.reloadConfig(configData)
//
//        configData.registerOnReload {
//            println("The configuration has been reloaded from file: ${configData.relativePath.any()}")
//        }
    }

}