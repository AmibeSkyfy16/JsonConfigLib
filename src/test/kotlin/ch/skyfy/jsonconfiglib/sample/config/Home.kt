package ch.skyfy.jsonconfiglib.sample.config

import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    var x: Int,
    var y: Int,
    var z: Int,
    var pitch: Float,
    var yaw: Float,
    var name: String
) : Validatable(){
    override fun validateImpl(errors: MutableList<String>) {
        if(x >= 10_000 || x <= -10_000) errors.add("A home can't be more than 10,000 blocs away")
    }
}