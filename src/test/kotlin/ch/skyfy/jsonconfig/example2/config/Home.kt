package ch.skyfy.jsonconfig.example2.config

import ch.skyfy.jsonconfig.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    var x: Int,
    var y: Int,
    var z: Int,
    var pitch: Float,
    var yaw: Float,
    var name: String
) : Validatable {

    /*
     * When gson convert a json to kotlin dataclass, some field can be null
     * even if it is not specified with the question mark
     */
    @Suppress("SENSELESS_COMPARISON", "ControlFlowWithEmptyBody")
    override fun validate(errors: MutableList<String>) {
        println("validate Home ...")

        validateNonNull(errors)
    }
}
