package ch.skyfy.jsonconfig.core.internal

import ch.skyfy.jsonconfig.core.Defaultable
import ch.skyfy.jsonconfig.core.JsonData
import ch.skyfy.jsonconfig.core.Validatable
import java.nio.file.Path
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

class Registration<DATA : Validatable, DEFAULT : Defaultable<DATA>>(
    val kf2_get: KFunction2<Path, Boolean, DATA>,
    val kf2_save: KFunction2<DATA, Path, DATA>,
    val kf1_save: KFunction1<JsonData<DATA>, Unit>
) {

    companion object{
        inline operator fun <reified DATA : Validatable, reified DEFAULT : Defaultable<DATA>> invoke(
            kf2_get: KFunction2<Path, Boolean, DATA>,
            kf2_save: KFunction2<DATA, Path, DATA>,
             kf1_save: KFunction1<JsonData<DATA>, Unit>
        ) : Registration<DATA, DEFAULT>{
            return Registration(kf2_get, kf2_save, kf1_save)
        }
    }

    inline fun <reified DATA : Validatable> get(file: Path, shouldCrash: Boolean): DATA {
        return kf2_get.call(file, shouldCrash) as DATA
    }

    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(config: DATA, file: Path): DATA {
        return kf2_save.call(config, file) as DATA
    }

    @Suppress("unused")
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(jsonData: JsonData<DATA>) {
        kf1_save.call(jsonData)
    }

}