package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.core.bundle.Bundle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.composable
import dev.vladleesi.braindanceapp.ui.screens.CalendarScreen
import dev.vladleesi.braindanceapp.ui.screens.CollectionsScreen
import dev.vladleesi.braindanceapp.ui.screens.GameDetailsScreen
import dev.vladleesi.braindanceapp.ui.screens.HomeScreen
import dev.vladleesi.braindanceapp.ui.screens.NewsScreen
import dev.vladleesi.braindanceapp.ui.screens.ProfileScreen
import io.github.aakira.napier.Napier

inline fun <reified T : Route> NavGraphBuilder.registerRoute(
    route: T,
    arguments: List<NamedNavArgument> = emptyList(),
) {
    composable(route = route.name, arguments = arguments) { entry ->
        route.screen.invoke(entry.arguments)
    }
}

fun NavHostController.navigate(
    route: Route,
    arguments: Map<String, String> = emptyMap(),
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
) {
    if (arguments.isEmpty()) {
        navigate(route.name, navOptions, navigatorExtras)
        return
    }
    val routeWithArgs =
        arguments.entries.fold(route.name) { acc, (key, value) ->
            acc.replace("{$key}", value)
        }
    Napier.d("Navigating to $routeWithArgs")
    runCatching {
        navigate(routeWithArgs, navOptions, navigatorExtras)
    }
}

sealed class Route(val screen: @Composable (Bundle?) -> Unit) {
    open val name = this::class.simpleName.orEmpty()

    data object HomeRoute : Route({ HomeScreen() })

    data object NewsRoute : Route({ NewsScreen() })

    data object CalendarRoute : Route({ CalendarScreen() })

    data object CollectionsRoute : Route({ CollectionsScreen() })

    data object ProfileRoute : Route({ ProfileScreen() })

    data object GameDetailsRoute : Route({ bundle -> GameDetailsScreen(bundle) }) {
        override val name = "${super.name}/{${Params.GAME_ID}}"

        object Params {
            const val GAME_ID = "gameId"
        }
    }
}
