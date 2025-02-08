package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.repository.GameDetailsRepo
import dev.vladleesi.braindanceapp.utils.CoverSize
import dev.vladleesi.braindanceapp.utils.ParentPlatformType
import dev.vladleesi.braindanceapp.utils.StoreTypeModel
import dev.vladleesi.braindanceapp.utils.formatDate
import dev.vladleesi.braindanceapp.utils.getMergedStoresType
import dev.vladleesi.braindanceapp.utils.orZero
import dev.vladleesi.braindanceapp.utils.parentPlatformTypes
import dev.vladleesi.braindanceapp.utils.toCoverUrl
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailsViewModel(
    private val gameDetailsRepo: GameDetailsRepo,
) : ViewModel() {
    private val _gameDetailsState = MutableStateFlow<GameDetailsState>(GameDetailsState.Loading)
    val gameDetailsState: StateFlow<GameDetailsState> = _gameDetailsState.asStateFlow()

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            Napier.e(message = exception.message.orEmpty(), throwable = exception)
            _gameDetailsState.value = GameDetailsState.Error(exception.message.orEmpty())
        }

    fun loadGameDetails(gameId: Int?) {
        viewModelScope.launch(handler) {
            _gameDetailsState.emit(GameDetailsState.Loading)
            val gameItem = gameDetailsRepo.gameDetails(gameId.orZero())?.firstOrNull()
            val gameDetails =
                GameDetails(
                    name = gameItem?.name.orEmpty(),
                    summary = gameItem?.summary.orEmpty(),
                    storyline = gameItem?.storyline.orEmpty(),
                    coverImageUrl = gameItem?.cover?.url?.toCoverUrl(CoverSize.ORIGINAL).orEmpty(),
                    releaseDate = gameItem?.firstReleaseDate?.formatDate(),
                    platforms = gameItem?.platforms.orEmpty().parentPlatformTypes(),
                    stores =
                        getMergedStoresType(
                            externalGames = gameItem?.externalGames.orEmpty(),
                            websites = gameItem?.websites.orEmpty(),
                        ),
                    genres =
                        gameItem?.genres.orEmpty().map { genre ->
                            GenreTag(
                                id = genre.id.orZero(),
                                name = genre.name.orEmpty(),
                            )
                        },
                )
            _gameDetailsState.emit(
                GameDetailsState.Success(gameDetails),
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
    val summary: String,
    val storyline: String,
    val coverImageUrl: String,
    val releaseDate: String?,
    val platforms: List<ParentPlatformType>,
    val stores: List<StoreTypeModel>,
    val genres: List<GenreTag>,
)

data class GenreTag(
    val id: Int,
    val name: String,
)
