package ch.skyfy.jsonconfig.core

fun interface Defaultable<DATA> {
    fun getDefault(): DATA
}