package ch.skyfy.jsonconfig.example2.config

import ch.skyfy.jsonconfig.core.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    var x: Int,
    var y: Int,
    var z: Int,
    var pitch: Float,
    var yaw: Float,
    var name: String
) : Validatable