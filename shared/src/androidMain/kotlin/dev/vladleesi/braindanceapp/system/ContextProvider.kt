package dev.vladleesi.braindanceapp.system

import android.content.Context

object ContextProvider {
    // TODO: Move to DI
    private var contextProvider: () -> Context = {
        throw IllegalArgumentException(
            "You need to implement the 'contextProvider' to provide the required Context. " +
                "Just make sure to set a valid context using " +
                "the 'setContextProvider()' method.",
        )
    }

    fun setContextProvider(provider: () -> Context) {
        contextProvider = provider
    }

    fun androidContext(): Context = contextProvider.invoke()
}
