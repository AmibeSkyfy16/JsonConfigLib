package ch.skyfy.jsonconfig.example3.config

import ch.skyfy.jsonconfig.Defaultable
import ch.skyfy.jsonconfig.Validatable

@kotlinx.serialization.Serializable
data class Config(
    val dayOfAuthorizationOfThePvP: Int,
    val dayOfAuthorizationOfTheEntryInTheNether: Int,
    val allowEnderPearlAssault: Boolean
) : Validatable

@Suppress("unused")
class DefaultConfig : Defaultable<Config>{
    override fun getDefault() = Config(2, 4, false)
}