package net.daniero.keymap

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.serializer
import org.junit.jupiter.api.Test

@Serializable
data class Foo(
    val map: KeyMap<Example>
)

@Serializable
enum class Example : EnumKey<Example> {
    FOO, BAR, BAZ
}

val json = Json {
    prettyPrint = true
    serializersModule = SerializersModule {
        // https://chatgpt.com/share/67c77b8d-9e64-800f-b889-da59477dfa82
        contextual(keyMapSerializer(Example.serializer()))
    }
}

fun <E> keyMapSerializer(enumSerializer: KSerializer<E>): KSerializer<Map<Key<E>, String>>
        where E : Enum<E>, E : Key<E> = KeyMapSerializer(enumSerializer, serializer())


class KeyMapTest {

    @Test
    fun `makeKeyMap maps keys to either the corresponding enum entry or Unknown`() {
        val result: KeyMap<Example> = makeKeyMap(
            mapOf(
                "FOO" to "yep",
                "BAZ" to "aye",
                "what?" to "nope!"
            )
        )

        assertThat(result).isEqualTo(
            mapOf(
                Example.FOO to "yep",
                Example.BAZ to "aye",
                Unknown("what?") to "nope!"
            )
        )
    }

    @Test
    fun `serialize and deserialize`() {
        val original = Foo(
            map = mapOf(
                Example.FOO to "yep",
                Example.BAZ to "aye",
                Unknown<Example>("what?") to "nah",
            )
        )

        val string = json.encodeToString(original)
        val fromString = json.decodeFromString<Foo>(string)
        assertThat(fromString).isEqualTo(original)
    }
}
