package ch.skyfy.jsonconfiglib.example5.config

import ch.skyfy.jsonconfiglib.ConfigData
import java.nio.file.Paths

object Configs {

//    val CONFIG = ConfigData.invoke<AConfig>(Paths.get("C:\\temp\\aconfig.json"), "example5/aconfig.json", true)

    val CONFIG = ConfigData.invoke<AConfig, DefaultAConfig>(Paths.get("C:\\temp\\aconfig.json"), true)
}