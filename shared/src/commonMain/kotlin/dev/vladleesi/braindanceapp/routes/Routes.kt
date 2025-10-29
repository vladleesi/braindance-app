package dev.vladleesi.braindanceapp.routes

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.composable
import androidx.savedstate.SavedState
import dev.vladleesi.braindanceapp.routes.RouteType.INNER
import dev.vladleesi.braindanceapp.routes.RouteType.MAIN
import io.github.aakira.napier.Napier

sealed class Route {
    open val name: String =
        this::class.simpleName.orEmpty()

    @Composable
    abstract fun Content(
        savedState: SavedState?,
        navHostController: NavHostController?,
    )
}

inline fun <reified T : Route> NavGraphBuilder.registerMainRoute(
    route: T,
    arguments: List<NamedNavArgument> = emptyList(),
    navHostController: NavHostController? = null,
) {
    registerRoute(route, arguments, navHostController, RouteType.MAIN)
}

inline fun <reified T : Route> NavGraphBuilder.registerInnerRoute(
    route: T,
    arguments: List<NamedNavArgument> = emptyList(),
    navHostController: NavHostController? = null,
) {
    registerRoute(route, arguments, navHostController, RouteType.INNER)
}

inline fun <reified T : Route> NavGraphBuilder.registerRoute(
    route: T,
    arguments: List<NamedNavArgument>,
    navHostController: NavHostController?,
    routeType: RouteType,
) {
    composable(
        route = route.name,
        arguments = arguments,
        enterTransition = {
            when (routeType) {
                RouteType.MAIN -> fadeIn()
                RouteType.INNER ->
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    ) + fadeIn()
            }
        },
        exitTransition = {
            when (routeType) {
                RouteType.MAIN -> fadeOut()
                RouteType.INNER ->
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    ) + fadeOut()
            }
        },
    ) { entry ->
        route.Content(entry.arguments, navHostController)
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
    Napier.i("Navigating to $routeWithArgs")
    runCatching {
        navigate(routeWithArgs, navOptions, navigatorExtras)
    }
}

/**
 * Defines the type of a navigation route.
 *
 * - [MAIN]: Top-level screen in the navigation graph (e.g., bottom bar destinations).
 * - [INNER]: Nested screen within a top-level section.
 */
enum class RouteType {
    MAIN,
    INNER,
}
