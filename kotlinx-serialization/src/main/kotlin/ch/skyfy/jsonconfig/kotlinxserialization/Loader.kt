package ch.skyfy.jsonconfig.kotlinxserialization

import ch.skyfy.jsonconfig.core.internal.Registration
import ch.skyfy.jsonconfig.core.internal.Registrators

@Suppress("unused")
object Loader {

    init {
        println("LOADING")
        Registrators.registration = Registration.invoke(
            JsonManager.Get,
            JsonManager::get,
            JsonManager::save,
            JsonManager::save
        )
    }

}