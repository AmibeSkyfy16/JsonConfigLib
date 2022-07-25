package ch.skyfy.jsonconfig.core

import kotlin.reflect.KFunction3
import kotlin.reflect.KFunction2


import java.io.IOException
import java.nio.file.Path
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

object Utils {

    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> get(file: Path, shouldCrash: Boolean): DATA {
        for (item in Package.getPackages()) {
            if (item.name == "ch.skyfy.jsonconfig.kotlinxserialization") {
                val jsonConfigClass = Class.forName("ch.skyfy.jsonconfig.kotlinxserialization.JsonManager")
                val obj = jsonConfigClass.kotlin.objectInstance ?: jsonConfigClass.kotlin.createInstance()
                val m = jsonConfigClass.getMethod("get", Path::class.java, Boolean::class.java)
                return m.invoke(obj, file, shouldCrash) as DATA
            }
            println(item.name)
        }
        throw IOException("dgdgdg")
    }

    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save1(
        config: DATA,
        file: Path
    ): DATA {

        for (item in Package.getPackages()) {
            println(item.name)

            // Implementation for kotlinx.serialization
            if (item.name == "ch.skyfy.jsonconfig.kotlinxserialization") {
                return callFunction("ch.skyfy.jsonconfig.kotlinxserialization", config, file)
            }

            // Implementation for Google Gson
            if (item.name == "ch.skyfy.jsonconfig.gson") {
                // TODO
            }

            // Implementation for Jackson
            if (item.name == "ch.skyfy.jsonconfig.jackson") {
                // TODO
            }

        }
        throw IOException("temporary exception")
    }

    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> saveK(
        config: DATA,
        file: Path
    ): DATA {

        for (item in Package.getPackages()) {
            println(item.name)

            // Implementation for kotlinx.serialization
            if (item.name == "ch.skyfy.jsonconfig.kotlinxserialization") {
                val vk: KFunction2<DATA, Path, DATA> = ::save1

                return callFunction("ch.skyfy.jsonconfig.kotlinxserialization", config, file)
            }

            // Implementation for Google Gson
            if (item.name == "ch.skyfy.jsonconfig.gson") {
                // TODO
            }

            // Implementation for Jackson
            if (item.name == "ch.skyfy.jsonconfig.jackson") {
                // TODO
            }

        }
        throw IOException("temporary exception")
    }

    inline fun <reified DATA : Validatable> callFunction(classPath: String, config: DATA, file: Path): DATA {
        val clazz = Class.forName("$classPath.JsonManager")
        val instance = clazz.kotlin.objectInstance ?: clazz.kotlin.createInstance()
        val saveFun = clazz.kotlin.functions.find { (it.name == "save") }
//        val saveFun = clazz.kotlin.functions.find { (it.name == "save_KFun2") }
        if (saveFun != null) {
            println("calling save fun")
            return saveFun.call(instance, config, file) as DATA
//            val l = saveFun.call(instance) as KFunction2<*, *, *>
//            l.call(config, file)

        }
        throw IOException("temporary exception")
    }



    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(
        jsonData: JsonData<DATA>
    ) {

        for (item in Package.getPackages()) {
            if (item.name == "ch.skyfy.jsonconfig.kotlinxserialization") {

            }
        }
        throw IOException("sss")
    }

}