package dev.vladleesi.braindanceapp.koin

import dev.vladleesi.braindanceapp.data.api.KtorClientManager
import dev.vladleesi.braindanceapp.data.api.clients.GamerPowerClient
import dev.vladleesi.braindanceapp.data.api.clients.IgdbClient
import dev.vladleesi.braindanceapp.data.api.remote.GamerPowerRemote
import dev.vladleesi.braindanceapp.data.api.remote.GamesRemote
import dev.vladleesi.braindanceapp.data.api.remote.PopularityPrimitivesRemote
import dev.vladleesi.braindanceapp.data.repository.GameDetailsRepo
import dev.vladleesi.braindanceapp.data.repository.GamerPowerRepo
import dev.vladleesi.braindanceapp.data.repository.HomeRepo
import dev.vladleesi.braindanceapp.data.repository.PopularityPrimitivesRepo
import dev.vladleesi.braindanceapp.ui.viewmodels.GameDetailsViewModel
import dev.vladleesi.braindanceapp.ui.viewmodels.GiveawayDetailsViewModel
import dev.vladleesi.braindanceapp.ui.viewmodels.HomeViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    androidConfig: KoinAppDeclaration? = null,
    debugHttpLogging: Boolean = false,
) {
    startKoin {
        androidConfig?.invoke(this)
        koinModules(debugHttpLogging)
    }
}

private fun KoinApplication.koinModules(debugHttpLogging: Boolean) =
    modules(remoteModule, repositoryModule, viewModelModule, ktorClientManagerModule(debugHttpLogging))

private fun ktorClientManagerModule(debugHttpLogging: Boolean) =
    module {
        single(named(KtorClientManager.IGDB_HTTP_CLIENT)) { IgdbClient.build(debugHttpLogging) }
        single(named(KtorClientManager.GAMER_POWER_HTTP_CLIENT)) { GamerPowerClient.build(debugHttpLogging) }
        singleOf(::KtorClientManager)
    }

private val remoteModule =
    module {
        singleOf(::GamesRemote)
        singleOf(::PopularityPrimitivesRemote)
        singleOf(::GamerPowerRemote)
    }

private val repositoryModule =
    module {
        singleOf(::HomeRepo)
        singleOf(::GamerPowerRepo)
        singleOf(::GameDetailsRepo)
        singleOf(::PopularityPrimitivesRepo)
    }

private val viewModelModule =
    module {
        viewModelOf(::HomeViewModel)
        viewModel { (id: Int) -> GameDetailsViewModel(gameId = id, gameDetailsRepo = get()) }
        viewModel { (id: Int) -> GiveawayDetailsViewModel(giveawayId = id, gamerPowerRepo = get()) }
    }
