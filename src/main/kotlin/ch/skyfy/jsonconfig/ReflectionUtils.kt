package ch.skyfy.jsonconfig

import ch.skyfy.jsonconfig.JsonConfig.LOGGER
import java.lang.reflect.Method
import kotlin.jvm.internal.Reflection

object ReflectionUtils {
    fun loadClassesByReflection(classesToLoad: Array<Class<*>>) {
        for (config in classesToLoad) {
            val canonicalName = config.canonicalName
            try {
                Class.forName(canonicalName)
            } catch (e: ClassNotFoundException) {
                LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod")
                LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod")
                LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod")
                LOGGER.fatal("A FATAL ERROR OCCURRED WITH FKMod")
                throw RuntimeException(e)
            }
        }
    }

    fun loadConfigs() {
        val name = Throwable().stackTrace[1].className
        val c = Class.forName(name)
        val p = c.`package`
        val listPackages = mutableListOf<String>()
        val args = p.name.split(".")
        for ((index, value) in p.name.split(".").withIndex()) {
            val sub = args.subList(0, args.size - index)
            val realPackageName = sub.joinToString(".")
            listPackages.add(realPackageName)
//            println(value)
        }


        for (item in Package.getPackages()) {
            if(listPackages.contains(item.name)){
                for (clazz in item.javaClass.classes) {
                    if(clazz.isAssignableFrom(Loadable::class.java)){
                        println("loading class: ${clazz.toString()}")
                    }
                }
                println()
            }
        }
        println(name)
    }
}