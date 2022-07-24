package ch.skyfy.jsonconfig.example3.config

import ch.skyfy.jsonconfig.core.JsonData
import java.nio.file.Paths

object Configs {

    // example3/config.json5 is the default json5 file. it will be copied to the config folder of the minecraft server. Admin will be able to configure it and understand it will comment allowed by json5 format
    // json5 is not supported by kotlinx.serialization right now 😢😢😢
    val CONFIG = JsonData.invoke<Config>(Paths.get("C:\\temp\\players-homes.json5"), "./example3/config.json5")

    // generate a json5 config file from object
//    val CONFIG = JsonData.invoke<Config, DefaultConfig>(Paths.get("C:\\temp\\example3\\config.json5"))

}