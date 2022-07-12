package ch.skyfy.jsonconfig

interface Defaultable<DATA> {
    fun getDefault(): DATA
}