package ch.skyfy.jsonconfig

import ch.skyfy.jsonconfig.core.JsonConfig.LOGGER
import kotlin.test.Test

class LoggerTest {

    @Test
    fun test1(){
        LOGGER.trace { "This is trace log" }
        LOGGER.debug { "This is debug log" }
        LOGGER.info { "This is info log" }
        LOGGER.warn { "This is warn log" }
        LOGGER.error { "This is error log" }
    }

}