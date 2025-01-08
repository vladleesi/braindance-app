package dev.vladleesi.braindanceapp.utils

import dev.vladleesi.braindanceapp.data.models.games.Platform

fun List<Platform>.distinctPlatformTypes(): List<PlatformTypeShort> {
    return this.map { it.platformTypeShort() }.distinct()
}

fun Platform.platformTypeShort(): PlatformTypeShort {
    val platformName = this.details?.name ?: return PlatformTypeShort.Other
    return when {
        platformName.contains("PlayStation", ignoreCase = true) -> PlatformTypeShort.PlayStation
        platformName.contains("Xbox", ignoreCase = true) -> PlatformTypeShort.Xbox
        platformName.contains("PC", ignoreCase = true) -> PlatformTypeShort.PC
        platformName.contains("macOS", ignoreCase = true) -> PlatformTypeShort.MacOS
        platformName.contains("Nintendo", ignoreCase = true) -> PlatformTypeShort.Switch
        else -> PlatformTypeShort.Other
    }
}

enum class PlatformTypeShort {
    PlayStation,
    Xbox,
    PC,
    MacOS,
    Switch,
    Other,
}
