package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.composable
import androidx.savedstate.SavedState
import io.github.aakira.napier.Napier

sealed class Route {
    open val name: String =
        this::class.simpleName.orEmpty()

    @Composable
    abstract fun renderContent(
        savedState: SavedState?,
        navHostController: NavHostController?,
    )
}

inline fun <reified T : Route> NavGraphBuilder.registerRoute(
    route: T,
    arguments: List<NamedNavArgument> = emptyList(),
    navHostController: NavHostController? = null,
) {
    composable(route = route.name, arguments = arguments) { entry ->
        route.renderContent(entry.arguments, navHostController)
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
