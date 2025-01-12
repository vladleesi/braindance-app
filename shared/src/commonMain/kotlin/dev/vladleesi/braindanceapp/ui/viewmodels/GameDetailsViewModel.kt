package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.api.remote.GameDetailsRemote
import dev.vladleesi.braindanceapp.data.api.remote.StoresRemote
import dev.vladleesi.braindanceapp.data.repository.GameDetailsRepo
import dev.vladleesi.braindanceapp.data.repository.StoresRepo
import dev.vladleesi.braindanceapp.utils.ParentPlatformType
import dev.vladleesi.braindanceapp.utils.StoreType
import dev.vladleesi.braindanceapp.utils.formatDate
import dev.vladleesi.braindanceapp.utils.parentPlatformTypes
import dev.vladleesi.braindanceapp.utils.storeTypes
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailsViewModel : ViewModel() {
    @Suppress("ForbiddenComment")
    // TODO: Move to DI
    private val gameDetailsRepo = GameDetailsRepo(GameDetailsRemote())
    private val storesRepo = StoresRepo(StoresRemote())

    private val _gameDetailsState = MutableStateFlow<GameDetailsState>(GameDetailsState.Loading)
    val gameDetailsState: StateFlow<GameDetailsState> = _gameDetailsState.asStateFlow()

    // TODO: Move to global coroutine config
    private val handler =
        CoroutineExceptionHandler { _, exception ->
            Napier.e(message = exception.message.orEmpty(), throwable = exception)
            // TODO: Handle error in background thread
            _gameDetailsState.value = GameDetailsState.Error(exception.message.orEmpty())
        }

    fun loadGameDetails(gameId: String?) {
        viewModelScope.launch(handler) {
            // TODO: Add dispatcher
            val result = gameDetailsRepo.gameDetails(gameId.orEmpty())
            // TODO: Make the second request async
            val stores = storesRepo.stores(gameId.orEmpty())
            _gameDetailsState.emit(
                GameDetailsState.Success(
                    GameDetails(
                        name = result.name.orEmpty(),
                        descriptionRaw = result.descriptionRaw.orEmpty(),
                        backgroundImage = result.backgroundImage.orEmpty(),
                        releaseDate = result.released.orEmpty().formatDate(),
                        platforms = result.parentPlatforms.orEmpty().parentPlatformTypes(),
                        stores = stores.results.orEmpty().storeTypes(),
                    ),
                ),
            )
        }
    }
}

sealed class GameDetailsState {
    data object Loading : GameDetailsState()

    data class Success(val gameDetails: GameDetails) : GameDetailsState()

    data class Error(val message: String) : GameDetailsState()
}

data class GameDetails(
    val name: String,
    val descriptionRaw: String,
    val backgroundImage: String,
    val releaseDate: String?,
    val platforms: List<ParentPlatformType>,
    val stores: List<Pair<StoreType, String>>,
)
