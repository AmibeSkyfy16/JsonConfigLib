package ch.skyfy.jsonconfig.core

import java.nio.file.Path

data class JsonData<DATA : Validatable>(var data: DATA, val relativeFilePath: Path) {
    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path): JsonData<DATA> =
            JsonData(JsonManagerCore.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath), relativeFilePath)

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String): JsonData<DATA> =
            JsonData(JsonManagerCore.getOrCreateConfig(relativeFilePath, defaultFile), relativeFilePath)
    }

}
