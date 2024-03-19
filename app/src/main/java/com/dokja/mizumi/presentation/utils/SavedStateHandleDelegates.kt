package com.dokja.mizumi.presentation.utils

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import kotlin.reflect.KProperty

class StateExtraString(private val state: SavedStateHandle) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        state.get<String>(property.name)!!

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) =
        state.set(property.name, value)
}

class StateExtraBoolean(private val state: SavedStateHandle) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        state.get<Boolean>(property.name)!!

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) =
        state.set(property.name, value)
}

class ExtraString {
    operator fun getValue(thisRef: Intent, property: KProperty<*>) =
        thisRef.extras!!.getString(property.name)!!

    operator fun setValue(thisRef: Intent, property: KProperty<*>, value: String) =
        thisRef.putExtra(property.name, value)
}

class ExtraBoolean {
    operator fun getValue(thisRef: Intent, property: KProperty<*>) =
        thisRef.extras!!.getBoolean(property.name)

    operator fun setValue(thisRef: Intent, property: KProperty<*>, value: Boolean) =
        thisRef.putExtra(property.name, value)
}