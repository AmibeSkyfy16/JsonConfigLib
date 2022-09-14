package ch.skyfy.jsonconfiglib.example4.config

import ch.skyfy.jsonconfiglib.DelegateData
import kotlin.properties.Delegates

class DelegateDataDatabase(
    private val database: Database
) : DelegateData<Database>(database) {
    var port by Delegates.observable(database.port) { _, _, newValue ->
        database.port = newValue
    }
}


//class DelegateDataDatabase(
////    private val database: Database
//) : {
//    var _port by Delegates.observable(port) { _, _, newValue ->
//        database.port = newValue
//    }
//}
