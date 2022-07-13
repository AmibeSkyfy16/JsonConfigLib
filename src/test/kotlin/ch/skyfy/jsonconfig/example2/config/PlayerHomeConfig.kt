package ch.skyfy.jsonconfig.example2.config

import ch.skyfy.jsonconfig.Defaultable
import ch.skyfy.jsonconfig.Validatable

data class PlayersHomesConfig(var players: MutableList<Player>) : Validatable {

    override fun validate(errors: MutableList<String>) {
        println("validate PlayersHomesConfig ...")

        validateNonNull(errors) // players field cannot be null

        @Suppress("SENSELESS_COMPARISON") // When gson convert a json to kotlin dataclass, some field can be null
        if(players != null) players.forEach { it.validate(errors) } // validation for player object

        confirmValidate(errors) // print all errors found and throw a runtime exception if there are errors
    }

}

class DefaultPlayerHomeConfig : Defaultable<PlayersHomesConfig> {
    override fun getDefault(): PlayersHomesConfig = PlayersHomesConfig(mutableListOf())
}