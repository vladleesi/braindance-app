package dev.vladleesi.braindanceapp.utils

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import okio.FileSystem

object ImageLoaderInitializer {
    @Composable
    fun initialize() {
        setSingletonImageLoaderFactory(::createImageLoader)
    }
}

// Cache configuration constants
private const val MEMORY_CACHE_PERCENTAGE = 0.3
private const val DISK_CACHE_MAX_SIZE_BYTES = 512L * 1024 * 1024 // 512MB

// Builds the ImageLoader with enabled caching policies
private fun createImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache { configureMemoryCache(context) }
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCache { configureDiskCache() }
        .crossfade(true)
        .logger(DebugLogger())
        .build()

// Configures the memory cache with a specific percentage of available memory
private fun configureMemoryCache(context: PlatformContext) =
    MemoryCache.Builder()
        .maxSizePercent(context, MEMORY_CACHE_PERCENTAGE)
        .strongReferencesEnabled(true)
        .build()

// Configures the disk cache with a specified size and directory
private fun configureDiskCache() =
    DiskCache.Builder()
        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        .maxSizeBytes(DISK_CACHE_MAX_SIZE_BYTES)
        .build()
