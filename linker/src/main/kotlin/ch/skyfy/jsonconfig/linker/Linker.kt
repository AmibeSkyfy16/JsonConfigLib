package ch.skyfy.jsonconfig.linker

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

abstract class Linker {

    abstract fun <DATA> get(file: Path, json: Any, shouldCrash: Boolean): DATA

    abstract fun < DATA > save(
        config: DATA,
        file: Path,
        json: Any
    ): DATA
}