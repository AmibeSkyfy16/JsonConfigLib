package ch.skyfy.jsonconfig.test.example1

import ch.skyfy.jsonconfig.core.JsonData
import java.nio.file.Paths

object Configs {
    val PLAYERS_HOMES = JsonData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(Paths.get("C:\\temp\\players-homes.json"))
}