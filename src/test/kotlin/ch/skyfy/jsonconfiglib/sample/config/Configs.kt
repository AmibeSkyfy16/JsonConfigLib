package ch.skyfy.jsonconfiglib.sample.config

import ch.skyfy.jsonconfiglib.ConfigData
import ch.skyfy.jsonconfiglib.example4.config.Database
import ch.skyfy.jsonconfiglib.example5.config.AConfig
import ch.skyfy.jsonconfiglib.example5.config.DefaultAConfig
import java.nio.file.Paths

object Configs {

    // The playerHomesConfig loaded from file C:\temp\players-homes.json or from class DefaultPlayerHomeConfig if the file not exist
    // If later, you decide to modify the config, adding player for example, the file will be saved with the modified data, because automaticallySave is set to true
    val PLAYERS_HOMES = ConfigData.invoke<PlayersHomesConfig, DefaultPlayerHomeConfig>(Paths.get("C:\\temp\\players-homes.json"), true)

    /**
     * In this example we use a default JSON file located inside the jar
     */
    val PLAYERS_HOMES_FROM_A_FILE = ConfigData.invoke<PlayersHomesConfig>(Paths.get("C:\\temp\\players-homes-from-a-file.json"), "sample/players-homes.json", true)
}