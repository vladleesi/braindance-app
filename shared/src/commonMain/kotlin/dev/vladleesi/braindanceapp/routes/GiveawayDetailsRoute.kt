package dev.vladleesi.braindanceapp.routes

import androidx.compose.runtime.Composable
import androidx.core.bundle.Bundle
import androidx.navigation.NavHostController
import dev.vladleesi.braindanceapp.ui.screens.GiveawayDetailsScreen

data object GiveawayDetailsRoute : Route() {
    override val name
        get() = "${super.name}/{${Params.GIVEAWAY_ID}}"

    @Composable
    override fun renderContent(
        bundle: Bundle?,
        navHostController: NavHostController?,
    ) {
        GiveawayDetailsScreen(bundle, navHostController)
    }

    object Params {
        const val GIVEAWAY_ID = "giveawayId"
    }
}
