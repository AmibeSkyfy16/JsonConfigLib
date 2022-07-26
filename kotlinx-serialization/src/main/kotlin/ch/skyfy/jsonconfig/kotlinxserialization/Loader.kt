package ch.skyfy.jsonconfig.kotlinxserialization

import ch.skyfy.jsonconfig.core.Validatable
import ch.skyfy.jsonconfig.core.internal.Registration
import ch.skyfy.jsonconfig.core.internal.Registrators

@Suppress("unused")
object Loader {

    init {
        println("LOADING")
        Registrators.get = Registration.Get(block = { l, p->
            return@Get JsonManager.Get.invoke(l, p)
        })
        Registrators.save = Registration.Save { l, p ->
            return@Save JsonManager.Save.invoke(l as Validatable, p)
        }
    }

}