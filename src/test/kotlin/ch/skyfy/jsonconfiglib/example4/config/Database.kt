package ch.skyfy.jsonconfiglib.example4.config

import ch.skyfy.jsonconfiglib.Defaultable
import ch.skyfy.jsonconfiglib.Validatable

@kotlinx.serialization.Serializable
data class Database(
    var port: Int,
    var url: String,
    var map: MutableMap<String, Float>
) : Validatable {
    override fun validateImpl(errors: MutableList<String>) {
        if(port != 3306 && port != 3307)
            errors.add("Port doesn't follow the rule")
    }
}

@Suppress("unused")
class DefaultDatabase : Defaultable<Database> {
    override fun getDefault() = Database(3306, "localhost", mutableMapOf())
}