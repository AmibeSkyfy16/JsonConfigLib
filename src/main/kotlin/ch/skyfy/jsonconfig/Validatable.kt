@file:Suppress("unused")

package ch.skyfy.jsonconfig

import ch.skyfy.jsonconfig.JsonConfig.LOGGER

interface Validatable {

    /**
     * A typo or a mistake can happen quickly.
     * If this is what happened when the user was setting up the files it could have bad repercussions on the game
     *
     * This should not happen at all
     *
     * @return true if its valid, false otherwise
     */
    fun validate(errors: MutableList<String>, shouldCrash: Boolean = true) : Boolean {return true}

    /**
     * must be called at the end.
     * If there are some errors, they will be printed and a Runtime Exception will be thrown
     *
     * @return true if its valid, false otherwise
     */
    fun confirmValidate(errors: MutableList<String>, shouldCrash: Boolean = true) : Boolean {
        return if (errors.size != 0) {
            LOGGER.error("Some json file are not valid")
            errors.forEach(LOGGER::error)
            if (shouldCrash) throw RuntimeException()
            else false
        }else true
    }

}