package ch.skyfy.jsonconfig.test1

import ch.skyfy.jsonconfig.ReflectionUtils
import kotlin.test.Test

class Tests {

    @Test
    fun test1(){
        ReflectionUtils.loadClassesByReflection(arrayOf(Configs::class.java))
        val config = Configs.USER_CONFIG.data
        println()
    }

}