package ch.skyfy.jsonconfig.example2.config

import ch.skyfy.jsonconfig.JsonData
import java.nio.file.Paths

object Configs {

//    val PLAYERS_HOMES = JsonData.invoke<PlayersHomesConfig>(Paths.get("C:\\temp\\players-homes.json5"), "")

    val PLAYERS_HOMES = JsonData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(Paths.get("C:\\temp\\players-homes.json5"))

}