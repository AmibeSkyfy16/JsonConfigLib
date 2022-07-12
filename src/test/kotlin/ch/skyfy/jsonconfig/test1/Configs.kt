package ch.skyfy.jsonconfig.test1

import ch.skyfy.jsonconfig.Defaultable
import ch.skyfy.jsonconfig.JsonData
import ch.skyfy.jsonconfig.Loadable
import java.nio.file.Paths

object Configs : Loadable {

    val USER_CONFIG = JsonData.invoke<UserData, UserDataDefault>(Paths.get("C:\\temp\\userConfig.json"))

    class UserDataDefault : Defaultable<UserData> {
        override fun getDefault(): UserData =
            UserData(
                groupA = mutableListOf(User("Hadda", "Queloz", 56), User("Dridrou", "Queloz", 99)),
                groupB = mutableListOf(User("Hadda", "Queloz", 56), User("Dridrou", "Queloz", 99))
            )
    }
}