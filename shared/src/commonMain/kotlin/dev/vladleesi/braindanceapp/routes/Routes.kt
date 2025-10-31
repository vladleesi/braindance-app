package dev.vladleesi.braindanceapp.routes

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.vladleesi.braindanceapp.routes.RouteType.INNER
import dev.vladleesi.braindanceapp.routes.RouteType.MAIN

/**
 * Every Route subclass must be annotated with @Serializable.
 */
interface Route {
    @Composable
    fun Content(navHostController: NavHostController?)
}

inline fun <reified T : Route> NavGraphBuilder.registerMainRoute(navHostController: NavHostController? = null) {
    registerRoute<T>(navHostController, RouteType.MAIN)
}

inline fun <reified T : Route> NavGraphBuilder.registerInnerRoute(navHostController: NavHostController? = null) {
    registerRoute<T>(navHostController, RouteType.INNER)
}

inline fun <reified T : Route> NavGraphBuilder.registerRoute(
    navHostController: NavHostController?,
    routeType: RouteType,
) {
    composable<T>(
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
        val route = entry.toRoute<T>()
        route.Content(navHostController)
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
