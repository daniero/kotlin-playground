package net.daniero.keymap

import kotlin.enums.enumEntries

inline fun <reified E> makeKeyMap(stringMap: Map<String, String>): KeyMap<E> where E : Enum<E> {
    return stringMap.mapKeys { (key) ->
        enumEntries<E>().find { it.name == key }
            ?.let { entry -> EnumKey(entry) }
            ?: Unknown(key)
    }
}
