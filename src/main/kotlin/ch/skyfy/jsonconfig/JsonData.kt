package ch.skyfy.jsonconfig

import java.nio.file.Path

data class JsonData<DATA : Validatable>(val data: DATA, val relativeFilePath: Path) {
    companion object {
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(relativeFilePath: Path): JsonData<DATA> =
            JsonData(JsonManager.getOrCreateConfig<DATA, DEFAULT>(relativeFilePath), relativeFilePath)
    }
}
