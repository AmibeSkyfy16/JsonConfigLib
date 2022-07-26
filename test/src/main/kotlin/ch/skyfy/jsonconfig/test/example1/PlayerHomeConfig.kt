package ch.skyfy.jsonconfig.test.example1


import ch.skyfy.jsonconfig.core.Defaultable
import ch.skyfy.jsonconfig.core.Validatable
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Polymorphic
@Serializable
data class PlayersHomesConfig(var players: MutableList<Player>) : Validatable {

    override fun validateImpl(errors: MutableList<String>) {
        players.forEach { it.validateImpl(errors) } // validation for player object
    }

}

class DefaultPlayerHomeConfig : Defaultable<PlayersHomesConfig> {
    override fun getDefault(): PlayersHomesConfig = PlayersHomesConfig(mutableListOf())
}