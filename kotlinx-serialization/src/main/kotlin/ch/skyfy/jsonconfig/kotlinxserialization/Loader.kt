package ch.skyfy.jsonconfig.kotlinxserialization

import ch.skyfy.jsonconfig.core.Validatable
import ch.skyfy.jsonconfig.core.internal.Get
import ch.skyfy.jsonconfig.core.internal.Registrators
import ch.skyfy.jsonconfig.core.internal.Save

@Suppress("unused")
object Loader {

    init {
        println("LOADING")
        Registrators.get = Get(block = { l, p->
            return@Get JsonManager.Get.invoke(l, p)
        })
        Registrators.save = Save { l, p ->
            return@Save JsonManager.Save.invoke(l as Validatable, p)
        }
    }

}