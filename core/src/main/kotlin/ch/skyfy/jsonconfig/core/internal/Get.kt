package ch.skyfy.jsonconfig.core.internal

import ch.skyfy.jsonconfig.core.Validatable
import java.nio.file.Path

class Get<DATA : Validatable>(val block: (Path, Boolean) -> DATA) {
    inline fun <reified DATA : Validatable> get(file: Path, shouldCrash: Boolean): DATA {
        return block.invoke(file, shouldCrash) as DATA
    }
}