package ch.skyfy.jsonconfiglib.example3.config

import ch.skyfy.jsonconfiglib.ConfigData
import java.nio.file.Paths

object Configs {
    val CONFIG = ConfigData.invoke<Config, DefaultConfig>(Paths.get("C:\\temp\\games.json"))
}