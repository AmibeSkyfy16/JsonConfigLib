package ch.skyfy.jsonconfiglib.example4.config

import ch.skyfy.jsonconfiglib.ConfigData
import java.nio.file.Paths

object Configs {

    /**
     * In this example we use a default JSON file located inside the jar
     */
    val CONFIG = ConfigData.invoke<Database>(Paths.get("C:\\temp\\database.json"), "example4/database.json", true)
}