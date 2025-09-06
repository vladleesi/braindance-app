package dev.vladleesi.braindanceapp.data.models.games

import kotlinx.serialization.Serializable

@Serializable
data class AgeRating(
    val id: Int,
    val rating: Rating,
)

@Serializable
enum class Rating(
    val value: Int,
) {
    THREE(1),
    SEVEN(2),
    TWELVE(3),
    SIXTEEN(4),
    EIGHTEEN(5),
    RP(6),
    EC(7),
    E(8),
    E10(9),
    T(10),
    M(11),
    AO(12),
    CERO_A(13),
    CERO_B(14),
    CERO_C(15),
    CERO_D(16),
    CERO_Z(17),
    USK_0(18),
    USK_6(19),
    USK_12(20),
    USK_16(21),
    USK_18(22),
    GRAC_ALL(23),
    GRAC_TWELVE(24),
    GRAC_FIFTEEN(25),
    GRAC_EIGHTEEN(26),
    GRAC_TESTING(27),
    CLASS_IND_L(28),
    CLASS_IND_TEN(29),
    CLASS_IND_TWELVE(30),
    CLASS_IND_FOURTEEN(31),
    CLASS_IND_SIXTEEN(32),
    CLASS_IND_EIGHTEEN(33),
    ACB_G(34),
    ACB_PG(35),
    ACB_M(36),
    ACB_MA15(37),
    ACB_R18(38),
    ACB_RC(39),
    ;

    companion object {
        fun fromInt(value: Int): Rating? = entries.find { it.value == value }
    }
}
