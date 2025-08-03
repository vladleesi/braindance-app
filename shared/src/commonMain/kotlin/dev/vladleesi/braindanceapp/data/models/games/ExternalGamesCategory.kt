package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ExternalGames(
    val id: Int?,
    val url: String?,
    val category: ExternalGamesCategory?,
)

@Suppress("MagicNumber")
@Serializable(with = ExternalGamesSerializer::class)
enum class ExternalGamesCategory(
    val id: Int,
) {
    STEAM(1),
    GOG(5),
    YOUTUBE(10),
    MICROSOFT(11),
    APPLE(13),
    TWITCH(14),
    ANDROID(15),
    AMAZON_ASIN(20),
    AMAZON_LUNA(22),
    AMAZON_ADG(23),
    EPIC_GAME_STORE(26),
    OCULUS(28),
    UTOMIK(29),
    ITCH_IO(30),
    XBOX_MARKETPLACE(31),
    KARTRIDGE(32),
    PLAYSTATION_STORE_US(36),
    FOCUS_ENTERTAINMENT(37),
    XBOX_GAME_PASS_ULTIMATE_CLOUD(54),
    GAMEJOLT(55),
    UNKNOWN(-1),
    ;

    companion object {
        private val idMap = entries.associateBy(ExternalGamesCategory::id)

        fun fromId(id: Int): ExternalGamesCategory? = idMap[id]
    }
}

object ExternalGamesSerializer : KSerializer<ExternalGamesCategory> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ExternalGamesCategory", PrimitiveKind.INT)

    override fun serialize(
        encoder: Encoder,
        value: ExternalGamesCategory,
    ) {
        encoder.encodeInt(value.id)
    }

    override fun deserialize(decoder: Decoder): ExternalGamesCategory {
        val id = decoder.decodeInt()
        return ExternalGamesCategory.fromId(id) ?: ExternalGamesCategory.UNKNOWN
    }
}
