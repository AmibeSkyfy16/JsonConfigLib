package ch.skyfy.jsonconfig.kotlinxserialization

import ch.skyfy.jsonconfig.core.Validatable
import ch.skyfy.jsonconfig.core.internal.Registration
import ch.skyfy.jsonconfig.core.internal.Registrators

@Suppress("unused")
class Loader {

    companion object{
        inline operator fun <reified DATA: Validatable> invoke(){
            Registrators.registration = Registration.invoke<DATA>(
                JsonManager::get2,
                JsonManager.Get,
                JsonManager::get,
                JsonManager::save,
                JsonManager::save
            )
        }
    }

    init {
        println("LOADING")
//        Registrators.registration = Registration.invoke<String>(
////            block = {f,g ->
////                    return@invoke JsonManager.get2(f, g)
////            },
//            JsonManager::get2,
//            JsonManager.Get,
//            JsonManager::get,
//            JsonManager::save,
//            JsonManager::save
//        )
    }

}