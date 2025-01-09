package dev.vladleesi.braindanceapp.utils

import dev.vladleesi.braindanceapp.data.models.games.Platform
import dev.vladleesi.braindanceapp.resources.Res
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_macos
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_playstation
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_switch
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_windows
import dev.vladleesi.braindanceapp.resources.ic_platform_logo_xbox
import org.jetbrains.compose.resources.DrawableResource

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

enum class PlatformTypeShort(val iconRes: DrawableResource?) {
    PlayStation(iconRes = Res.drawable.ic_platform_logo_playstation),
    Xbox(iconRes = Res.drawable.ic_platform_logo_xbox),
    PC(iconRes = Res.drawable.ic_platform_logo_windows),
    MacOS(iconRes = Res.drawable.ic_platform_logo_macos),
    Switch(iconRes = Res.drawable.ic_platform_logo_switch),
    Other(iconRes = null),
}
