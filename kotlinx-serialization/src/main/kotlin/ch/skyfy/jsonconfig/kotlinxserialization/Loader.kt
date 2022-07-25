package ch.skyfy.jsonconfig.kotlinxserialization

import ch.skyfy.jsonconfig.core.internal.Registrator
import ch.skyfy.jsonconfig.core.internal.Registrators

object Loader {

    init {
        println("LOADED")
        Registrators.registrator = Registrator(
            JsonManager::get,
            JsonManager::save,
            JsonManager::save
        )
    }

}