package ch.skyfy.jsonconfig.test.example1

import ch.skyfy.jsonconfig.core.Validatable
import kotlinx.serialization.Polymorphic
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