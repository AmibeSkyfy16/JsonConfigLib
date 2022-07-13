package ch.skyfy.jsonconfig

fun interface Defaultable<DATA> {
    fun getDefault(): DATA
}