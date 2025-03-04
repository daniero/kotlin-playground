package net.daniero.keymap

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Key<E>

@Serializable
@SerialName("EnumKey")
data class EnumKey<E>(
    val key: E,
) : Key<E> where E : Enum<E>

@Serializable
@SerialName("Unknown")
data class Unknown<E>(
    val name: String,
) : Key<E> where E : Enum<E>

typealias KeyMap<E> = Map<@Polymorphic Key<E>, String>
