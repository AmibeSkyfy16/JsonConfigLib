package ch.skyfy.jsonconfiglib.example6

import ch.skyfy.jsonconfiglib.ConfigData
import ch.skyfy.jsonconfiglib.Validatable
import ch.skyfy.jsonconfiglib.Validatable3
import kotlinx.serialization.Serializable
import java.nio.file.Paths

/*
All the things to test

root config - ClashOfClansConfig
    property needed
        - var String
        - val String

        - var Int
        - val Int

        - var Double
        - val Double

        - var



 */

object Configs {
    val CONFIG = ConfigData.invokeSpecial<ClashOfClansConfig>(Paths.get("C:\\temp\\ClashOfClansConfig.json"), true)
}

@Serializable
data class ClashOfClansConfig(
    var player: Player = Player(
        RankTypes.BRONZE,
        10000,
        InnerPlayer(RankTypes.DIAMOND, 60000)
    ),

//    var configName: String = "BliBliConfig",
//    var priceOnTheChineseMarket: Double = 9800.7,
//
//
//    var mainVillage: MainVillage = MainVillage(
//        name = "BliBli",
//        gold = 10.0,
//        elixir = 10.0,
//        goldMines = mutableListOf(),
//        elixirMines = mutableListOf()
//    ),
//
//    var someOthersVillage: MutableList<OtherVillage> = mutableListOf(
//        OtherVillage("second village")
//    ),
//
//
//    var defaultGoldMineLevel1: GoldMine = GoldMine(0, 100, mutableMapOf(SpecialUpgradingResourceTypes.DUROP to 1)),
//    var defaultElixirMineLevel1: ElixirMine = ElixirMine(0, 100, mutableMapOf(SpecialUpgradingResourceTypes.DUROP to 1)),


    ) : Validatable() {

}

@Serializable
data class Player(
    var currentRank: RankTypes,
    var playerLevels: Int,
    var innerPlayer: InnerPlayer
) : Validatable() {


    override fun validateImpl(errors: MutableList<String>) {
//        if(playerLevels > 100)errors.add("playerLevels cannot be greater than 100 !")
    }
}

@Serializable
data class InnerPlayer(
    var currentRank: RankTypes,
    var playerLevels: Int,
) : Validatable() {
    init  { confirmValidation() }
    override fun validateImpl(errors: MutableList<String>) {
        if(playerLevels > 100)errors.add("playerLevels cannot be greater than 100 !")
    }
}


@Serializable
enum class RankTypes {
    MASTER,
    GRANDMASTER,
    DIAMOND,
    PLATINUM,
    GOLD,
    IRON,
    BRONZE
}

@Serializable
sealed class BaseVillage : Validatable() {
    abstract var name: String
}

@Serializable
class MainVillage(
    override var name: String,

    var gold: Double,
    var elixir: Double,

    var goldMines: MutableList<GoldMine>,
    var elixirMines: MutableList<ElixirMine>
) : BaseVillage() {

}


@Serializable
class OtherVillage(
    override var name: String,
) : BaseVillage() {

}

@Serializable
data class GoldMine(
    override var level: Int,
    override var perHourProduction: Int,
    override var neededResourcesForNextUpgrade: MutableMap<SpecialUpgradingResourceTypes, Int>
) : Mine() {

}

@Serializable
data class ElixirMine(
    override var level: Int,
    override var perHourProduction: Int,
    override var neededResourcesForNextUpgrade: MutableMap<SpecialUpgradingResourceTypes, Int>
) : Mine() {

}

@Serializable
abstract class Mine : Validatable() {
    abstract var level: Int
    abstract var perHourProduction: Int
    abstract var neededResourcesForNextUpgrade: MutableMap<SpecialUpgradingResourceTypes, Int>
}

@Serializable
enum class SpecialUpgradingResourceTypes {
    OKATIR,
    MOZAN,
    TABLE_OF_DUST,
    DUROP
}