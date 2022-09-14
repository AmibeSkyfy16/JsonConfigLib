package ch.skyfy.jsonconfiglib.example4

import ch.skyfy.jsonconfiglib.*
import ch.skyfy.jsonconfiglib.example4.config.Configs
import ch.skyfy.jsonconfiglib.example4.config.Database
import java.io.ObjectInputFilter.Config
import javax.xml.crypto.Data
import kotlin.test.Test

class Example4 {

    @Test
    fun example4() {

        // First, you have to load the configs. After that we can access them from anywhere in the code
        // If this is the first time, then no JSON files representing the configs exist.
        // They will be generated from the classes that implement the Defaultable interface or else json files that are located inside the jar will be copied where they are supposed to be
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        Configs.CONFIG.addGlobalNotifier { _, _, _ ->
            println("Hey, a member property of ${Configs.CONFIG.`data`} has been set")
            println("Updating sideboard with newValue ...")
        }

        // Now we can access the config
        val config = Configs.CONFIG.`data`

        println("port : ${config.port}")
        println("url : ${config.url}")

//        ConfigManager.computeAndSave(Configs.CONFIG, { data -> data.port = 3307 })
//        Thread.sleep(5000)
        Configs.CONFIG.setVal2({ it.port }, 3307)

        println("port : ${config.port}")
//        println("url : ${config.url}")
    }

}

private fun Int.setValue(i: Int) {

}
