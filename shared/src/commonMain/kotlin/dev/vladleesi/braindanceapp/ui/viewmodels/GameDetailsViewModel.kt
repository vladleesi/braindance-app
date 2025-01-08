package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.api.remote.GameDetailsRemote
import dev.vladleesi.braindanceapp.data.repository.GameDetailsRepo
import dev.vladleesi.braindanceapp.utils.PlatformTypeShort
import dev.vladleesi.braindanceapp.utils.distinctPlatformTypes
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
            val result = gameDetailsRepo.gameDetails(gameId.orEmpty())
            _gameDetailsState.emit(
                GameDetailsState.Success(
                    GameDetails(
                        name = result.name.orEmpty(),
                        descriptionRaw = result.descriptionRaw.orEmpty(),
                        backgroundImage = result.backgroundImage.orEmpty(),
                        // TODO: Return icons?
                        platforms = result.platforms.orEmpty().distinctPlatformTypes(),
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
    val platforms: List<PlatformTypeShort>,
)
