package ch.skyfy.jsonconfig.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.outputStream
import kotlin.reflect.KClass
import kotlin.reflect.KTypeParameter
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
    inline fun <reified DATA : Validatable> save(
        config: DATA,
        file: Path

    ): DATA {
        println("ssdsds HEY")
        for (item in Package.getPackages()) {
            if (item.name == "ch.skyfy.jsonconfig.kotlinxserialization") {
                val jsonConfigClass = Class.forName("ch.skyfy.jsonconfig.kotlinxserialization.JsonManager")
                val f = jsonConfigClass.kotlin.functions.find { (it.name == "save") }
                val obj = jsonConfigClass.kotlin.objectInstance ?: jsonConfigClass.kotlin.createInstance()
//                val m = jsonConfigClass.getMethod("save", Any::class.java, Path::class.java)
//                val m = jsonConfigClass.getMethod("save")
//                return m.invoke(obj, config, file) as DATA
                if (f != null) {
                    f.call(config, file)
                }
            }
        }
        throw IOException("sss")
    }

    @Throws(Exception::class)
    inline fun <reified DATA : Validatable> save(
        jsonData: JsonData<DATA>

    ) {

        for (item in Package.getPackages()) {
            if (item.name == "ch.skyfy.jsonconfig.kotlinxserialization") {
//                val jsonConfigClass = Class.forName("ch.skyfy.jsonconfig.kotlinxserialization.JsonManager")
//                val obj = jsonConfigClass.kotlin.objectInstance ?: jsonConfigClass.kotlin.createInstance()
//                val m = jsonConfigClass.getMethod("save", JsonData::class.java)
//                 m.invoke(obj, jsonData)
            }
        }
        throw IOException("sss")


    }

}