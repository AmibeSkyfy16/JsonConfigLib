package ch.skyfy.jsonconfiglib.sample.config

import ch.skyfy.jsonconfiglib.Defaultable
import ch.skyfy.jsonconfiglib.Validatable
import kotlinx.serialization.Serializable
import java.lang.RuntimeException

@Serializable
data class PlayersHomesConfig(var players: MutableSet<Player>) : Validatable() {

//    init { startValidation() } // Start the validation when the object is created

    // Using some recursion to validate all nested Validatable object
    init {
        try {
            // Start the validation when the object is created
            validateAll(PlayersHomesConfig::class)
        }catch (e: RuntimeException){
            e.printStackTrace()
        }
    }

    override fun validateImpl(errors: MutableList<String>) {
        // if validateAll(PlayersHomesConfig::class) is used, we don't need the line below
//        players.forEach { it.validateImpl(errors) } // validation for player object.
    }
}

class DefaultPlayerHomeConfig : Defaultable<PlayersHomesConfig> {
    override fun getDefault() = PlayersHomesConfig(mutableSetOf(
        Player(
            mutableListOf(
                Home(900, 900, 900, 9.0f, 9.0f, "999")
            ),
            "ebb5c153-3f6f-4fb6-9062-90ac964e7990", 9, 9, 9
        )
    ))
}