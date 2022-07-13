package ch.skyfy.jsonconfig.example2.config

import ch.skyfy.jsonconfig.Validatable

data class Player(
    var homes: MutableList<Home>,
    var uuid: String,
    var maxHomes: Int = 3,
    var cooldown: Int = 15,
    var standStill: Int = 5
) : Validatable {

    /*
     * When gson convert a json to kotlin dataclass, some field can be null
     * even if it is not specified with the question mark
     */
    @Suppress("SENSELESS_COMPARISON", "ControlFlowWithEmptyBody")
    override fun validate(errors: MutableList<String>) {
        println("validate Player ...")

        validateNonNull(errors) // All field of Player cannot be null !

        if (homes != null) homes.forEach { it.validate(errors) }

        if (uuid != null) {
            // TODO check in mojang database if this uuid is a real and premium minecraft account
        }

        if (maxHomes != null)
            if (maxHomes < 0) errors.add("maxHome cannot have a negative value")

        if (cooldown != null)
            if (cooldown < 0) errors.add("cooldown cannot have a negative value")

        if (standStill != 0)
            if (standStill < 0) errors.add("standStill cannot have a negative value")
    }
}
