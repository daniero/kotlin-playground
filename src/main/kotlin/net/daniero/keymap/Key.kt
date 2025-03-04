package net.daniero.keymap

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

sealed interface Key<E> where E : Key<E>

interface EnumKey<E> : Key<E> where E : Enum<E>, E : Key<E>

@Serializable
data class Unknown<E>(
    val name: String,
) : Key<E> where E : Enum<E>, E : Key<E>

open class KeyMapSerializer<E>(
    private val enumSerializer: KSerializer<E>,
    private val stringSerializer: KSerializer<String>
) : KSerializer<Map<Key<E>, String>> where E : Enum<E>, E : Key<E> {

    override val descriptor: SerialDescriptor = SerialDescriptor("KeyMap", buildClassSerialDescriptor("KeyMap2") {
        element("key", PrimitiveSerialDescriptor("Key", PrimitiveKind.STRING))
        element("value", PrimitiveSerialDescriptor("Value", PrimitiveKind.STRING))
    })

    override fun serialize(encoder: Encoder, value: Map<Key<E>, String>) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("Only JSON supported")
        val jsonObject = buildJsonObject {
            value.forEach { (key, v) ->
                val keyString = when (key) {
                    is EnumKey<*> -> key.toString() // Serialize enum as its name
                    is Unknown<*> -> "unknown:${key.name}" // Prefix for Unknown keys
                }
                put(keyString, JsonPrimitive(v))
            }
        }
        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Map<Key<E>, String> {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Only JSON supported")
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return jsonObject.mapKeys { (keyString, _) ->
            when {
                keyString.startsWith("unknown:") -> Unknown(keyString.removePrefix("unknown:"))
                else -> jsonDecoder.json.decodeFromJsonElement(enumSerializer, JsonPrimitive(keyString))
            }
        }.mapValues { it.value.jsonPrimitive.content }
    }
}
