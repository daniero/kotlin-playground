package net.daniero.keymap

import kotlinx.serialization.Contextual
import kotlin.enums.enumEntries

typealias KeyMap<E> =
        @Contextual
        Map<Key<E>, String>

inline fun <reified E> makeKeyMap(stringMap: Map<String, String>): KeyMap<E> where E : Enum<E>, E : Key<E> {
    return stringMap.mapKeys { (key) ->
        enumEntries<E>().find { it.name == key } ?: Unknown(key)
    }
}
