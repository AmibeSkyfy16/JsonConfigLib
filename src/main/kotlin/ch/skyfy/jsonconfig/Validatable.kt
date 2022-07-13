@file:Suppress("unused")

package ch.skyfy.jsonconfig

import ch.skyfy.jsonconfig.JsonConfig.LOGGER

interface Validatable {

    /**
     * A typo or a mistake can happen quickly.
     * If this is what happened when the user was setting up the files it could have bad repercussions on the game
     *
     * This should not happen at all
     */
    fun validate(errors: MutableList<String>){}

    /**
     * Check that the primitive type values are correct.
     * Ex: "port (for the mariadb server connection" can't be less than 0 or greater than 65535
     */
    fun validatePrimitivesType(errors: MutableList<String>?){}

    /**
     * Performs a thorough check of all fields in a class to make sure nothing is null
     * This method must be called at the very beginning of validate
     */
    fun validateNonNull(errors: MutableList<String>) {
        try {
            ValidateUtils.traverse(this, errors)
        } catch (e: NullPointerException) {
            errors.forEach(LOGGER::fatal)
//            LOGGER.fatal("FATAL ERROR")
//            throw java.lang.RuntimeException(e)
        }
    }

    /**
     * must be called at the end.
     * If there are some errors, they will be print and a Runtime Exception will be thrown
     */
    fun confirmValidate(errors: MutableList<String>){
        if(errors.size != 0){
            LOGGER.fatal("Some json file are not valid")
            errors.forEach(LOGGER::fatal)
            throw RuntimeException()
        }
    }

}