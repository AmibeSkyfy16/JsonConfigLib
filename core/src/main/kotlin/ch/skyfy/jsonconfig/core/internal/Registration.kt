package ch.skyfy.jsonconfig.core.internal

import ch.skyfy.jsonconfig.core.Defaultable
import ch.skyfy.jsonconfig.core.JsonData
import ch.skyfy.jsonconfig.core.Validatable
import kotlinx.serialization.json.decodeFromStream
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.full.functions

class Registration<DATA : Validatable>(
    val block: (Path, Boolean) -> DATA,
    val invoked1: Companion,
    val kf2_get: KFunction2<Path, Boolean, DATA>,
    val kf2_save: KFunction2<DATA, Path, DATA>,
    val kf1_save: KFunction1<JsonData<DATA>, Unit>
) {

    companion object{
        inline operator fun <reified DATA : Validatable> invoke(
            block: (Path, Boolean) -> () -> DATA,
//            block: (Path, Boolean) -> DATA,
             invoked1: Any,
            kf2_get: KFunction2<Path, Boolean, DATA>,
            kf2_save: KFunction2<DATA, Path, DATA>,
            kf1_save: KFunction1<JsonData<DATA>, Unit>
        ) : Registration<DATA>{

            return Registration(block, invoked1, kf2_get, kf2_save, kf1_save)
        }
    }

    class Get<DATA : Validatable>{
        companion object{
            inline operator fun <reified DATA : Validatable> invoke(file: Path, shouldCrash: Boolean, block: (Path, Boolean) -> DATA) : DATA{
                return block.invoke(file, shouldCrash)
            }
        }
    }


    inline fun <reified DATA : Validatable> get(file: Path, shouldCrash: Boolean): DATA {
        return Get.invoke(file, shouldCrash, block = {r,l->
            return block.invoke(r,l) as DATA
        })
//        return kf2_get.call(file, shouldCrash) as DATA
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