package dev.vladleesi.braindanceapp.backend

import java.util.concurrent.TimeUnit

internal class StartupTiming {
    private val started = System.nanoTime()

    fun <T> measure(
        phase: String,
        block: () -> T,
    ): T {
        val phaseStarted = System.nanoTime()
        var succeeded = false
        try {
            return block().also { succeeded = true }
        } finally {
            write(phase, phaseStarted, succeeded)
        }
    }

    fun checkpoint(phase: String) {
        write(phase, null, true)
    }

    private fun write(
        phase: String,
        phaseStarted: Long?,
        succeeded: Boolean,
    ) {
        val now = System.nanoTime()
        val duration = phaseStarted?.let { ",\"duration_ms\":${TimeUnit.NANOSECONDS.toMillis(now - it)}" }.orEmpty()
        val elapsed = TimeUnit.NANOSECONDS.toMillis(now - started)
        println(
            "{\"event\":\"backend_startup\",\"phase\":\"$phase\"$duration," +
                "\"elapsed_ms\":$elapsed,\"succeeded\":$succeeded}",
        )
    }
}
