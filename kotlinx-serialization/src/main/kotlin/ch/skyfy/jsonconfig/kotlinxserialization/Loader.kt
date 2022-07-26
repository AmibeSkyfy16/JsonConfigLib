package ch.skyfy.jsonconfig.kotlinxserialization

import ch.skyfy.jsonconfig.core.Validatable
import ch.skyfy.jsonconfig.core.internal.Registration
import ch.skyfy.jsonconfig.core.internal.Registrators

@Suppress("unused")
class Loader {



    init {
        println("LOADING")
        Registrators.registration = Registration.invoke(
//            JsonManager::get2,
//            JsonManager.Get,
            JsonManager::get,
//            JsonManager::save,
//            JsonManager::save
        )
    }

}