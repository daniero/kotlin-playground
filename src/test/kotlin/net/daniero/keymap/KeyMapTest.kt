package net.daniero.keymap

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

@Serializable
data class Foo(
    @Serializable(with = ES::class)
    val map: KeyMap<Example>
)

@Serializable
enum class Example : EnumKey<Example> {
    FOO, BAR, BAZ
}

class ES : KeyMapSerializer<Example>(Example.serializer(), serializer())

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

        val string = Json.encodeToString(original)
        val fromString = Json.decodeFromString<Foo>(string)

        assertThat(fromString).isEqualTo(original)
    }
}
