package ch.skyfy.jsonconfiglib.example5.config

import ch.skyfy.jsonconfiglib.Defaultable
import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class AConfig(
    var port: Int,
    var url: String,
    var b: B,
    var mutableList: MutableList<String>,
    var map: MutableMap<String, Float>,
    var bMap: MutableMap<String, B>
) : Validatable

@Serializable
data class B(
    val aInt: Int,
    val aString: String,
    var bb: BB,
    val mutableSet: MutableSet<String>,
    val mutableList: MutableList<String>,
    val mutableMap: MutableMap<String, String>
) : Validatable {
    companion object {
        fun createDefault(): B {
            return B(
                -1,
                "I am a default B",
                BB(
                    BBB("I am BBB from a default B"),
                    listOf(1, 2, 3, 4),
                    mutableListOf("A", "B", "C"),
                    mapOf("A" to "AA", "B" to "BB"),
                    mutableMapOf("A" to "AA", "B" to "BB"),
                    setOf("BB UUID"),
                    mutableSetOf("BB ANOTHER UUID")
                ),
                mutableSetOf("B UUID"),
                mutableListOf("Hey"),
                mutableMapOf("Hey" to "Hello")
            )
        }
    }
}

@Serializable
data class BB(
    var bbb: BBB,
    val list: List<Int>,
    val mutableList: MutableList<String>,
    val map: Map<String, String>,
    val mutableMap: MutableMap<String, String>,
    val set: Set<String>,
    val mutableSet: MutableSet<String>
) : Validatable {
    companion object {
        fun createDefault(): BB {
            return BB(
                BBB("I am BBB from a default B"),
                listOf(1, 2, 3, 4),
                mutableListOf("A", "B", "C"),
                mapOf("A" to "AA", "B" to "BB"),
                mutableMapOf("A" to "AA", "B" to "BB"),
                setOf("BB UUID"),
                mutableSetOf("BB ANOTHER UUID")
            )
        }
    }
}

@Serializable
data class BBB(
    val iAmBBB: String
) : Validatable

@Suppress("unused")
class DefaultAConfig : Defaultable<AConfig> {
    override fun getDefault() = AConfig(
        21,
        "127.0.0.1",
        B(
            18,
            "Im B",
            BB(
                BBB("I am BBB"),
                listOf(1, 2, 3, 4),
                mutableListOf("A", "B", "C"),
                mapOf("A" to "AA", "B" to "BB"),
                mutableMapOf("A" to "AA", "B" to "BB"),
                setOf("BB UUID"),
                mutableSetOf("BB ANOTHER UUID")
            ),
            mutableSetOf("B UUID"),
            mutableListOf("Hey"),
            mutableMapOf("Hey" to "Hello")
        ),
        mutableListOf("ROOT"),
        mutableMapOf("Pitch" to 90f),
        mutableMapOf(
            "B1" to B(
                188,
                "Im B",
                BB(
                    BBB("I am BBB"),
                    listOf(1, 2, 3, 4),
                    mutableListOf("A", "B", "C"),
                    mapOf("A" to "AA", "B" to "BB"),
                    mutableMapOf("A" to "AA", "B" to "BB"),
                    setOf("BB UUID"),
                    mutableSetOf("BB ANOTHER UUID")
                ),
                mutableSetOf("B UUID"),
                mutableListOf("Hey"),
                mutableMapOf("Hey" to "Hello")
            ),
            "b2" to B(
                199,
                "Im B",
                BB(
                    BBB("I am BBB"),
                    listOf(1, 2, 3, 4),
                    mutableListOf("A", "B", "C"),
                    mapOf("A" to "AA", "B" to "BB"),
                    mutableMapOf("A" to "AA", "B" to "BB"),
                    setOf("BB UUID"),
                    mutableSetOf("BB ANOTHER UUID")
                ),
                mutableSetOf("B UUID"),
                mutableListOf("Hey"),
                mutableMapOf("Hey" to "Hello")
            ),
        )
    )
}