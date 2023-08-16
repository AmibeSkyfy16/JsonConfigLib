@file:Suppress("unused")

package ch.skyfy.jsonconfiglib

import ch.skyfy.jsonconfiglib.ConfigManager.LOGGER
import kotlin.properties.Delegates
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf

abstract class Validatable3 : Cloneable {

    lateinit var l: Any

    abstract var test: String

    init {
        print("")

    }

    init {
//        confirmValidate<Validatable>(shouldThrowRuntimeException = true)
    }

    /**
     * A typo or a mistake can happen quickly.
     * If this is what happened when the user was setting up the files it could have bad repercussions on the game
     * and this should not happen at all
     *
     * @param errors A [MutableList] object representing a list of errors
     */
    open fun validateImpl(errors: MutableList<String>) {

    }

    /**
     * call [validateImpl] fun and will log in the console every error found
     *
     * @param errors A [MutableList] object representing a list of errors
     * @param shouldThrowRuntimeException A [Boolean] object that will throw a [RuntimeException] if it's true and at least one error is found
     * @return true if it's valid, false otherwise
     */
//    fun <DATA : Validatable3> confirmValidate(errors: MutableList<String> = mutableListOf(), operation: Operation<DATA, *>? = null, shouldThrowRuntimeException: Boolean = false): Boolean {
//        validateImpl(errors)
//
//        return if (errors.size != 0) {
//
//            if (operation != null) {
//                val message = """
//                    La modification sur la propriété ${operation.prop.toString()} présent dans la classe
//                    ${operation.receiver!!::class.qualifiedName} n'est pas considéré comme valide !
//                    Peut-être que le nom que vous avez modifié n'est pas autorisé, ou que le numéro est trop grand ou trop petit,
//                    ou encore que l'ajout ou la suppression d'une donné, n'est pas autorisé.
//                """.trimIndent()
//                LOGGER.error(message)
//            } else {
//                LOGGER.error("Config ${this::class.qualifiedName} has just been modified and the new data is not considered valid")
//            }
//
//            errors.forEach(LOGGER::error)
//
//            if (shouldThrowRuntimeException) throw RuntimeException("Oh oh oh ! Some issues with JSON configuration files got caught !")
//            else false
//
//        } else true
//    }

//    @Suppress("UNCHECKED_CAST")
//    fun <DATA : Validatable3> confirmValidateRec(kClass: KClass<DATA>) {
//        confirmValidate<Validatable3>(shouldThrowRuntimeException = true)
//
//        kClass.declaredMemberProperties.forEach {
//            if (it.returnType.isSubtypeOf(Validatable3::class.createType())) {
//                if (this::class.isSubclassOf(Validatable3::class)) {
//                    val p = it.get(this as DATA) as Validatable3
//                    p.confirmValidateRec(kClass = p::class)
//                }
//            }
//        }
//    }

//    fun  confirmValidateRec3(
//        errors: MutableList<String> = mutableListOf(),
//        kClass: Class<out Validatable>,
//        shouldThrowRuntimeException: Boolean = true): Boolean {
//
//        confirmValidate<Validatable>(shouldThrowRuntimeException = true)
//        val kk = kClass.declaredFields
//
//        kClass.declaredFields.forEach {
//
////            if (it.type.isAssignableFrom(Validatable::class.java)) {
//            if (it.type.superclass == Validatable::class.java) {
////                it.invoke(this as DATA)
////                val l  = it.get(this as DATA) as Validatable<*>
////                val d = it.invoke(data) as Validatable<*>
//                val p = it.get(this) as Validatable
//                p.confirmValidateRec(kClass= p::class.java, shouldThrowRuntimeException = true)
////                l.confirmValidateRec(kClass = d::class, shouldThrowRuntimeException = true)
//            }
//
//        }
//
//        return true
//    }

//    inline fun <reified DATA : Validatable> confirmValidateRec2(
//        errors: MutableList<String> = mutableListOf(),
//        kClass: KClass<DATA>,
//        shouldThrowRuntimeException: Boolean = true,
//        crossinline run: (KClass<DATA>, Validatable, Int, errors: MutableList<String>) -> Unit
//    ) {
//        var cnt = 0
//        val l = this
//        val x = object : Consumer<Int> {
//            override fun accept(i: Int) {
//                if (i > 1) {
//                    repeat(2) { accept(i shr 1) }
//                    return
//                }
//                run(DATA::class, l, cnt++, mutableListOf())
//            }
//        }
//        x.accept(64)
//    }
}