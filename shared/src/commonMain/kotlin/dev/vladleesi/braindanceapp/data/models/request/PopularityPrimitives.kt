package dev.vladleesi.braindanceapp.data.models.request

@Suppress("MagicNumber")
enum class PopularityPrimitives(
    val type: Int,
) {
    VISITS(1),
    WANT_TO_PLAY(2),
    PLAYING(3),
    PLAYED(4),
}
