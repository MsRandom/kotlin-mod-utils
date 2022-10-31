package net.msrandom.kotlinutils

import kotlin.reflect.KProperty

class DelegateProvider<D>(private val provide: (name: String) -> D) {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>) = provide(property.name)
}
