package dev.vladleesi.braindanceapp.koin

import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.api.clients.GamerPowerClient
import dev.vladleesi.braindanceapp.data.api.clients.IgdbClient
import dev.vladleesi.braindanceapp.data.api.clients.TwitchClient
import dev.vladleesi.braindanceapp.data.api.remote.AuthRemote
import dev.vladleesi.braindanceapp.data.api.remote.GamerPowerRemote
import dev.vladleesi.braindanceapp.data.api.remote.GamesRemote
import dev.vladleesi.braindanceapp.data.repository.GameDetailsRepo
import dev.vladleesi.braindanceapp.data.repository.GamerPowerRepo
import dev.vladleesi.braindanceapp.data.repository.HomeRepo
import dev.vladleesi.braindanceapp.data.token.TokenStorage
import dev.vladleesi.braindanceapp.ui.viewmodels.GameDetailsViewModel
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(androidConfig: KoinAppDeclaration? = null) {
    startKoin {
        androidConfig?.invoke(this)
        koinModules()
    }
}

private fun KoinApplication.koinModules() =
    modules(remoteModule, repositoryModule, viewModelModule, tokenStorageModule, ktorClientManagerModule)

private val ktorClientManagerModule =
    module {
        single(named(KtorClientManager.IGDB_HTTP_CLIENT)) {
            IgdbClient.build(authRemote = get(), tokenStorage = get())
        }
        single(named(KtorClientManager.TWITCH_HTTP_CLIENT)) { TwitchClient.build() }
        single(named(KtorClientManager.GAMER_POWER_HTTP_CLIENT)) { GamerPowerClient.build() }
        singleOf(::KtorClientManager)
    }

private val remoteModule =
    module {
        singleOf(::AuthRemote)
        singleOf(::GamesRemote)
        singleOf(::GamerPowerRemote)
    }

private val repositoryModule =
    module {
        singleOf(::HomeRepo)
        singleOf(::GamerPowerRepo)
        singleOf(::GameDetailsRepo)
    }

private val viewModelModule =
    module {
        viewModelOf(::HomeViewModel)
        viewModelOf(::GameDetailsViewModel)
    }

private val tokenStorageModule =
    module {
        singleOf(::TokenStorage)
    }
