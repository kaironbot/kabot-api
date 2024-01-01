package org.wagham.kabotapi.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object TimestampDateSerializer: KSerializer<Date> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TimestampDate", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.time)
    }

    override fun deserialize(decoder: Decoder): Date {
        val ts = decoder.decodeLong()
        return Date(ts)
    }
}