package ch.skyfy.jsonconfig.core.internal

import ch.skyfy.jsonconfig.core.Validatable
import java.nio.file.Path

class Save<DATA : Validatable>(val block: (Any, Path) -> DATA) {
    inline fun <reified DATA_REIFIED > save(config: DATA_REIFIED, file: Path): DATA_REIFIED {
        return block.invoke((config as DATA), file) as DATA_REIFIED
    }
}