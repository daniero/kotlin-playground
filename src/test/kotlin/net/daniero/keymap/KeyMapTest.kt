package net.daniero.keymap

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.daniero.keymap.Example.BAZ
import net.daniero.keymap.Example.FOO
import org.junit.jupiter.api.Test

@Serializable
data class Foo(
    val map: KeyMap<Example>
)

@Serializable
enum class Example {
    FOO, BAR, BAZ
}

val json = Json {
    allowStructuredMapKeys = true
    serializersModule = SerializersModule {
        polymorphic(Key::class) {
            fun <E> registerKeySerializer(kSerializer: KSerializer<E>) {
                subclass(EnumKey.serializer(kSerializer))
                subclass(Unknown.serializer(kSerializer))
            }

            polymorphicDefaultSerializer(Key::class) { value: Key<*> ->
                TODO("To use your enum class in a KeyMap you must register its serializer here")
            }

            registerKeySerializer(Example.serializer())
        }
    }
}

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
                EnumKey(FOO) to "yep",
                EnumKey(BAZ) to "aye",
                Unknown<Example>("what?") to "nope!"
            )
        )
    }

    @Test
    fun `serialize and deserialize`() {
        val original = Foo(
            map = mapOf(
                EnumKey(FOO) to "yep",
                EnumKey(BAZ) to "aye",
                Unknown<Example>("what?") to "nah",
            )
        )

        val string = json.encodeToString(original)
        val fromString = json.decodeFromString<Foo>(string)
        println(string)
        assertThat(fromString).isEqualTo(original)
    }
}
