package ch.skyfy.jsonconfiglib

import java.nio.file.Path

/**
 * A data class representing a specific configuration
 *
 * To create instance of a ConfigData object, we use special fun called invoke that accept reified generic type
 *
 * @property data An object of type [DATA] representing the configuration
 * @property relativeFilePath A [Path] object representing where the configuration file is located
 */
data class ConfigData<DATA : Validatable>(var data: DATA, val relativeFilePath: Path) {
    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path): ConfigData<DATA> =
            ConfigData(ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath), relativeFilePath)

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String): ConfigData<DATA> =
            ConfigData(ConfigManager.getOrCreateConfig(relativeFilePath, defaultFile), relativeFilePath)
    }

}
