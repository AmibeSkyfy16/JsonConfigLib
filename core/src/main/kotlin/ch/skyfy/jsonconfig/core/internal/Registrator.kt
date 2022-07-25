package ch.skyfy.jsonconfig.core.internal

import ch.skyfy.jsonconfig.core.JsonConfig
import ch.skyfy.jsonconfig.core.JsonData
import ch.skyfy.jsonconfig.core.Validatable
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import kotlin.io.path.outputStream
import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction1

data class Registrator<DATA : Validatable>(
    val kf3_get: KFunction2<Path, Boolean, DATA>,
    val kf3_save: KFunction2<DATA, Path, DATA>,
    val kf1_save: KFunction1<JsonData<DATA>, Unit>
) {

//    lateinit var kf3_get: KFunction3<DATA, Path, Boolean, DATA>
//    lateinit var kf3_save: KFunction3<DATA, DATA, Path, DATA>
//        lateinit var kf1_save: KFunction1<DATA, JsonData<DATA>>

    inline fun <reified DATA : Validatable> get(file: Path, shouldCrash: Boolean): DATA {
        return kf3_get.call(file, shouldCrash) as DATA
    }

    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(
        config: DATA,
        file: Path
    ): DATA {
        return kf3_save.call(config, file) as DATA
    }

    @Throws(Exception::class)
    fun <DATA : Validatable> save(
        jsonData: JsonData<DATA>

    ) {
        kf1_save.call(jsonData)
    }


}