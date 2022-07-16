package ch.skyfy.jsonconfig

import java.nio.file.Path

data class JsonData<DATA : Validatable>(var data: DATA, val relativeFilePath: Path) {
    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path): JsonData<DATA> =
            JsonData(JsonManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath), relativeFilePath)

        inline operator fun <reified DATA : Validatable> invoke(relativeFilePath: Path, defaultFile: String): JsonData<DATA> =
            JsonData(JsonManager.getOrCreateConfig(relativeFilePath, defaultFile), relativeFilePath)
    }

}
