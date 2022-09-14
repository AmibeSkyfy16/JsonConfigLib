package ch.skyfy.jsonconfiglib

import java.nio.file.Path

inline fun <reified DATA : Validatable> ConfigData<DATA>.change(block: (DATA) -> Unit) {
    block.invoke(data)
    this.onChangesCallback.forEach {
        it.invoke(data)
    }
}

/**
 * A data class representing a specific configuration
 *
 * To create instance of a ConfigData object, we use special fun called invoke that accept reified generic type
 *
 * @property data An object of type [DATA] representing the configuration
 * @property relativeFilePath A [Path] object representing where the configuration file is located
 */
data class ConfigData<DATA : Validatable>(var data: DATA, val relativeFilePath: Path, val onChangesCallback: MutableList<(DATA) -> Unit>) {
    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path, automaticallySave: Boolean): ConfigData<DATA> {
            val data = ConfigManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath)

            val onChangesCallbacks = mutableListOf<(DATA)->Unit>()
            if(automaticallySave)onChangesCallbacks.add { ConfigManager.save(data, relativeFilePath) }

            return ConfigData(data, relativeFilePath, onChangesCallbacks)
        }

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String, automaticallySave: Boolean): ConfigData<DATA> {
            val data = ConfigManager.getOrCreateConfig<DATA>(relativeFilePath, defaultFile)

            val onChangesCallbacks = mutableListOf<(DATA)->Unit>()
            if(automaticallySave)onChangesCallbacks.add { ConfigManager.save(data, relativeFilePath) }

            return ConfigData(data, relativeFilePath, onChangesCallbacks)
        }
    }

    init {
        onChangesCallback.add {

        }
    }

}
