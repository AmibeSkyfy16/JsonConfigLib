@file:Suppress("unused")

package ch.skyfy.jsonconfiglib

import ch.skyfy.jsonconfiglib.ConfigManager.LOGGER
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.*

abstract class Validatable : Cloneable {


    /**
     * A typo or a mistake can happen quickly.
     * If this is what happened when the user was setting up the files it could have bad repercussions on the game
     * and this should not happen at all.
     *
     * As developer, you can implement this fun in your data class or class extending Validatable
     * to create your own validation rules. If some field contain data that you don't want like a negative value,
     * you can just add an error message in [errors] explaining the thing.
     *
     * @see startValidation
     * @see validateAll
     *
     * @param errors A [MutableList] object representing a list of error messages that will be displayed to the console
     */
    open fun validateImpl(errors: MutableList<String>) {}

    /**
     * As developer, you don't need to use this fun. You can use [startValidation] or [validateAll] inside
     * an init bloc for the data class acting as your configuration
     *
     * In general, when a data class object is constructed and [startValidation] or [validateAll] has been set up
     * in it, and there are validation errors, an exception will be thrown
     *
     * This fun will call [validateImpl] fun and will log in the console every error found.
     *
     * @param errors A [MutableList] object representing a list of errors
     * @param shouldThrowRuntimeException A [Boolean] object that will throw a [RuntimeException] if it's true and at least one error is found
     * @return true if it's valid, false otherwise
     */
    fun <DATA : Validatable> validate(errors: MutableList<String> = mutableListOf(), operation: Operation<DATA, *>? = null, shouldThrowRuntimeException: Boolean = false): Boolean {
        validateImpl(errors)

        return if (errors.size != 0) {

            if (operation != null) {

                val messageEN = """
                    Modification to the «${operation.prop}» property in
                    class: «${operation.receiver::class.qualifiedName}» is not considered valid !
                    newValue: ${operation.newValue.toString()}
                    oldValue: ${operation.oldValue.toString()}
                    origin config: ${operation.origin}
                    Perhaps the name you modified is not authorized, or the number is too large or too small,
                    or the addition or deletion of a data item is not authorized.
                """.trimIndent()

                LOGGER.error(messageEN)
            } else {
                LOGGER.error("Config «${this::class.qualifiedName}» has just been modified and the new data is not considered valid")
            }

            errors.forEach(LOGGER::error)

            if (shouldThrowRuntimeException) throw RuntimeException("Oh oh oh ! Some issues with JSON configuration files got caught ! ❌❌❌")
            else false

        } else true
    }

    /**
     * Use this fun inside the init block of your data class.
     * It will call the [validateImpl] fun that you implemented just below.
     * If the config is not valid, a runtime exception will be thrown
     */
    fun startValidation(shouldThrowRuntimeException: Boolean = true) = validate<Validatable>(shouldThrowRuntimeException = shouldThrowRuntimeException)


    /**
     * you can use this fun in your root data class config, the [validate] fun of every field type that extend [Validatable]
     * will be called.
     *
     * @see validate
     */
    @Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
    fun <DATA : Validatable> validateAll(kClass: KClass<DATA>) {
        validate<Validatable>(shouldThrowRuntimeException = true)

        kClass.declaredMemberProperties.forEach {
            if (it.returnType.isSubtypeOf(Validatable::class.createType())) {
                if (this::class.isSubclassOf(Validatable::class)) {
                    val p = it.get(this as DATA) as Validatable
                    p.validateAll(kClass = p::class)
                }
            } else {
//                if(it.returnType.isSubtypeOf(Collection::class.createType(listOf(KTypeProjection.invariant(it.returnType.classifier!!.starProjectedType))))){
//                    println("tgg")
//                }
//                if(it.returnType.isSubtypeOf(Collection::class.createType(listOf(KTypeProjection.invariant(it.returnType))))){
//                    println("455")
//                }
//
//                if(it.returnType.isSubtypeOf(Collection::class.createType(listOf(KTypeProjection.covariant(it.returnType))))){
//                    println("s")
//                }
//                if(it.returnType.isSubtypeOf(Collection::class.createType(listOf(KTypeProjection.covariant(it.returnType.classifier!!.starProjectedType))))){
//                    println("ssss")
//                }
//                if(it.returnType == Collection::class.createType(listOf(KTypeProjection.covariant(it.returnType.classifier!!.starProjectedType)))){
//                    println("ssssffefdfdf")
//                }

//                ----- WORKS ----
//                if (it.returnType.isSubtypeOf(Collection::class.createType(listOf(KTypeProjection.contravariant(it.returnType))))) {
//                    println("3444")
//                }
//                if(it.returnType.isSubtypeOf(Collection::class.createType(listOf(KTypeProjection.contravariant(it.returnType.classifier!!.starProjectedType))))){
//                    println("11")
//                }
//                ----- WORKS ----

//                if(it.returnType.isSubtypeOf(Map::class.createType(mapOf(KTypeProjection.contravariant()))))

                if (it.returnType.isSubtypeOf(Collection::class.createType(listOf(KTypeProjection.contravariant(it.returnType))))) {
                    it.returnType.arguments.forEach { kTypeProjection ->
                        if (kTypeProjection.type != null && kTypeProjection.type!!.isSubtypeOf(Validatable::class.createType())) {
                            val listOfValidatable = it.get(this as DATA) as Collection<Validatable>
                            listOfValidatable.forEach { validatable ->
                                validatable.validateAll(kClass = validatable::class)
                            }
                        }
                    }
                } else {
                    it.returnType.arguments.forEach { kTypeProjection ->
                        if (kTypeProjection.type != null && kTypeProjection.type!!.isSubtypeOf(Validatable::class.createType())) {
                            if (it.returnType.classifier == Map::class) {
                                val map = it.get(this as DATA) as Map<Validatable, Validatable>
                                map.forEach { (t, u) ->
                                    t.validateAll(kClass = t::class)
                                    u.validateAll(kClass = u::class)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}