@file:Suppress("unused")

package ch.skyfy.jsonconfiglib

import ch.skyfy.jsonconfiglib.ConfigManager.LOGGER

interface Validatable : Cloneable{

    /**
     * A typo or a mistake can happen quickly.
     * If this is what happened when the user was setting up the files it could have bad repercussions on the game
     * and this should not happen at all
     *
     * @param errors A [MutableList] object representing a list of errors
     */
    fun validateImpl(errors: MutableList<String>){}

    /**
     * call [validateImpl] fun and will log in the console every error found
     *
     * @param errors A [MutableList] object representing a list of errors
     * @param shouldThrowRuntimeException A [Boolean] object that will throw a [RuntimeException] if it's true and at least one error is found
     * @return true if it's valid, false otherwise
     */
    fun confirmValidate(errors: MutableList<String> = mutableListOf(), shouldThrowRuntimeException: Boolean) : Boolean {
        validateImpl(errors)
        return if (errors.size != 0) {
            LOGGER.error("Some json file are not valid")
            errors.forEach(LOGGER::error)
            if (shouldThrowRuntimeException) throw RuntimeException()
            else false
        }else true
    }

}