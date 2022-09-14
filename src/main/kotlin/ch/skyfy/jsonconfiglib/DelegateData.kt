package ch.skyfy.jsonconfiglib

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.getExtensionDelegate

abstract class DelegateData<DELEGATE>(var data: DELEGATE){

    init {
        val  p = this::class.declaredMemberProperties.first().getExtensionDelegate()
    }

}