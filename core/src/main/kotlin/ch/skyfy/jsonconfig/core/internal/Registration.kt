package ch.skyfy.jsonconfig.core.internal

import ch.skyfy.jsonconfig.core.JsonData
import ch.skyfy.jsonconfig.core.Validatable
import java.nio.file.Path

class Registration<DATA : Validatable>(
//    val block: (Path, Boolean) -> () -> DATA,
//    val invoked1: Companion,
//    val kf2_get: KFunction2<Path, Boolean, DATA>,
//    val kf2_save: KFunction2<DATA, Path, DATA>,
//    val kf1_save: KFunction1<JsonData<DATA>, Unit>
) {

    companion object {
//        inline operator fun <reified DATA : Validatable> invoke(
////             block: Any,
//             block: (Path, Boolean) -> DATA,
////            invoked1: Any,
////            kf2_get: KFunction2<Path, Boolean, DATA>,
////            kf2_save: KFunction2<DATA, Path, DATA>,
////            kf1_save: KFunction1<JsonData<DATA>, Unit>
//        ): Registration<DATA> {
//
//            return Registration(block)
//        }
    }

    class Get<DATA : Validatable>(
        val block: (Path, Boolean) -> DATA
    ) {
        inline  fun <reified DATA : Validatable> get(file: Path, shouldCrash: Boolean): DATA {
            return block.invoke(file, shouldCrash) as DATA
        }
    }

    class Save<DATA : Validatable>(
        val block: (Any, Path) -> DATA
    ) {
        inline fun <reified DATAR : Validatable> save(config: DATAR, file: Path): DATAR {
            return block.invoke((config as DATA), file) as DATAR
        }
    }


    inline fun <reified DATA : Validatable> get(file: Path, shouldCrash: Boolean): DATA {
        return Registrators.get.get(file, shouldCrash)
//        return Get.invoke(file, shouldCrash, block = { r, l ->
//            return block.invoke(r, l) as DATA
//        })
//        return kf2_get.call(file, shouldCrash) as DATA
    }

    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(config: DATA, file: Path): DATA {
//        return kf2_save.call(config, file) as DATA
        throw Exception("adaskjfdhfsjfsfs")
    }

    @Suppress("unused")
    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(jsonData: JsonData<DATA>) {
//        kf1_save.call(jsonData)
    }

}


