package ch.skyfy.jsonconfiglib.example2.config

import ch.skyfy.jsonconfiglib.ConfigData
import java.nio.file.Paths

object Configs {
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(Paths.get("C:\\temp\\players-homes.json"), true)
}