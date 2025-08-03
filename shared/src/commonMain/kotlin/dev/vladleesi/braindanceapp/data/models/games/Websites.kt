package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Websites(
    val id: Int?,
    val url: String?,
    val category: WebsiteCategory?,
)

@Suppress("MagicNumber")
@Serializable(WebsiteCategorySerializer::class)
enum class WebsiteCategory(
    val id: Int,
) {
    OFFICIAL(1),
    WIKIA(2),
    WIKIPEDIA(3),
    FACEBOOK(4),
    TWITTER(5),
    TWITCH(6),
    INSTAGRAM(8),
    YOUTUBE(9),
    IPHONE(10),
    IPAD(11),
    ANDROID(12),
    STEAM(13),
    REDDIT(14),
    ITCH(15),
    EPIC_GAMES(16),
    GOG(17),
    DISCORD(18),
    BLUESKY(19),
    UNKNOWN(-1),
    ;

    companion object {
        private val idMap = entries.associateBy(WebsiteCategory::id)

        fun fromId(id: Int): WebsiteCategory = idMap[id] ?: UNKNOWN
    }
}

object WebsiteCategorySerializer : KSerializer<WebsiteCategory> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("WebsiteCategory", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): WebsiteCategory {
        val id = decoder.decodeInt()
        return WebsiteCategory.fromId(id)
    }

    override fun serialize(
        encoder: Encoder,
        value: WebsiteCategory,
    ) {
        encoder.encodeInt(value.id)
    }
}
