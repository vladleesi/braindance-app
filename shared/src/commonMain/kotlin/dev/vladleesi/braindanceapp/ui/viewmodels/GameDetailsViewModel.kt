package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.repository.GameDetailsRepo
import dev.vladleesi.braindanceapp.utils.CoverSize
import dev.vladleesi.braindanceapp.utils.ParentPlatformType
import dev.vladleesi.braindanceapp.utils.StoreTypeModel
import dev.vladleesi.braindanceapp.utils.formatDate
import dev.vladleesi.braindanceapp.utils.orZero
import dev.vladleesi.braindanceapp.utils.parentPlatformTypes
import dev.vladleesi.braindanceapp.utils.toCoverUrl
import dev.vladleesi.braindanceapp.utils.toStoreTypes
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailsViewModel(
    private val gameDetailsRepo: GameDetailsRepo,
) : ViewModel() {
    private val _gameDetailsState = MutableStateFlow<GameDetailsState>(GameDetailsState.Loading)
    val gameDetailsState: StateFlow<GameDetailsState> = _gameDetailsState.asStateFlow()

    private var hasLoaded = false

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            Napier.e(message = exception.message.orEmpty(), throwable = exception)
            _gameDetailsState.value = GameDetailsState.Error(exception.message.orEmpty())
        }

    fun loadGameDetails(
        gameId: Int?,
        reload: Boolean,
    ) {
        if (hasLoaded && reload.not()) return
        hasLoaded = true
        viewModelScope.launch(Dispatchers.IO + handler) {
            _gameDetailsState.emit(GameDetailsState.Loading)
            val game = gameDetailsRepo.gameDetails(gameId.orZero())?.firstOrNull()
            val gameDetails =
                GameDetails(
                    id = game?.id.orZero(),
                    name = game?.name.orEmpty(),
                    summary = game?.summary.orEmpty(),
                    storyline = game?.storyline.orEmpty(),
                    coverImageUrl =
                        game
                            ?.cover
                            ?.url
                            ?.toCoverUrl(CoverSize.ORIGINAL)
                            .orEmpty(),
                    releaseDate = game?.releaseDates?.firstOrNull()?.human ?: game?.firstReleaseDate?.formatDate(),
                    platforms = game?.platforms.orEmpty().parentPlatformTypes(),
                    stores = game?.websites.orEmpty().toStoreTypes(),
                    genres =
                        game?.genres.orEmpty().map { genre ->
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

    data class Success(
        val gameDetails: GameDetails,
    ) : GameDetailsState()

    data class Error(
        val message: String,
    ) : GameDetailsState()
}

data class GameDetails(
    val id: Int,
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
