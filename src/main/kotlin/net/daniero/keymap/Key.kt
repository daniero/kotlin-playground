package net.daniero.keymap

interface Key<E> where E : Enum<E>, E : Key<E>

data class Unknown<E>(
    val name: String,
) : Key<E> where E : Enum<E>, E : Key<E>
