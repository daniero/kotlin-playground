package net.daniero.keymap

sealed interface Key<E> where E : Key<E>

interface EnumKey<E> : Key<E> where E : Enum<E>, E : Key<E>

data class Unknown<E>(
    val name: String,
) : Key<E> where E : Enum<E>, E : Key<E>
