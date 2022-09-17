package ch.skyfy.jsonconfiglib.example5

import ch.skyfy.jsonconfiglib.*
import ch.skyfy.jsonconfiglib.example5.config.AConfig
import ch.skyfy.jsonconfiglib.example5.config.B
import ch.skyfy.jsonconfiglib.example5.config.BB
import ch.skyfy.jsonconfiglib.example5.config.Configs
import kotlin.test.Test

class Example5 {

    @Test
    fun example5() {

        ConfigManager.loadConfigs(arrayOf(Configs::class.java))

        val configData = Configs.CONFIG
        val serializableData = configData.serializableData

        configData.registerOnReload {
            println("The configuration has been reloaded from file: ${configData.relativePath.any()}")
        }

        configData.registerOnUpdate {
            println("An update has been done!")
            when (it) {
                is UpdateMapOperation<*, *, *> -> {
                    println("And it was about a map object")
                }

                is UpdateIterableOperation<*, *> -> {
                    println("And it was about a itarable object")
                }

                is UpdateCustomOperation<*, *> -> {
                    println("And it was about a custom object")
                }

                is SetOperation<AConfig, *> -> {
                    println("And it was about a member property of ${it.prop} on object ${it::receiver.name}")
                }
            }
        }

        configData.update(AConfig::port, 22)
        configData.update(AConfig::b, B.createDefault())
        // or
        configData.update<AConfig, Int>(AConfig::port, 22)
        configData.update<AConfig, B>(AConfig::b, B.createDefault())


        configData.updateNested(B::bb, serializableData.b, BB.createDefault())
        // or
        configData.updateNested<AConfig, B, BB>(B::bb, serializableData.b, BB.createDefault())


        configData.updateIterable(AConfig::mutableList) { it.add("Size is two now") }
        // or
        configData.updateIterable<AConfig, String, MutableList<String>>(AConfig::mutableList) { it.add("Size is three now") }

        configData.updateIterableNested(BB::mutableList, serializableData.b.bb.mutableList) { it.add("Hello buddies") }
        // or
        configData.updateIterableNested<AConfig, BB, String, MutableList<String>>(BB::mutableList, serializableData.b.bb.mutableList) { it.add("Hello buddies") }


        configData.updateMap(AConfig::bMap) { it["z"] = B.createDefault() }
        // or
        configData.updateMap<AConfig, String, B, MutableMap<String, B>> (AConfig::bMap) { it["zz"] = B.createDefault() }

        configData.updateMapNested(BB::mutableMap, serializableData.b.bb.mutableMap) { it["bwah-bwah"] = "bwah" }
        // or
        configData.updateMapNested<AConfig, BB, String, String, MutableMap<String, String>>(BB::mutableMap, serializableData.b.bb.mutableMap) { it["bwah-bwah"] = "bwah" }
    }

}