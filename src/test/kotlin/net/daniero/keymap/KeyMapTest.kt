package net.daniero.keymap

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test


class KeyMapTest {

    enum class Example : EnumKey<Example> {
        FOO, BAR, BAZ
    }

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
}
