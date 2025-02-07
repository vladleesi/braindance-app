package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.api.remote.GamerPowerRemote
import dev.vladleesi.braindanceapp.data.api.remote.GamesRemote
import dev.vladleesi.braindanceapp.data.models.games.GameItem
import dev.vladleesi.braindanceapp.data.models.games.Platform
import dev.vladleesi.braindanceapp.data.models.giveaways.GiveawayResponse
import dev.vladleesi.braindanceapp.data.repository.GamerPowerRepo
import dev.vladleesi.braindanceapp.data.repository.HomeRepo
import dev.vladleesi.braindanceapp.ui.components.MiniGameCardModel
import dev.vladleesi.braindanceapp.ui.components.giveaways.GiveawayItemModel
import dev.vladleesi.braindanceapp.utils.CoverSize
import dev.vladleesi.braindanceapp.utils.ParentPlatformType
import dev.vladleesi.braindanceapp.utils.nowUnix
import dev.vladleesi.braindanceapp.utils.orZero
import dev.vladleesi.braindanceapp.utils.parentPlatformTypes
import dev.vladleesi.braindanceapp.utils.toCoverUrl
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    @Suppress("ForbiddenComment")
    // TODO: Move to DI
    private val homeRepo = HomeRepo(GamesRemote())
    private val gamerPowerRepo = GamerPowerRepo(GamerPowerRemote())

    private val _mostAnticipated = MutableStateFlow<HomeState>(HomeState.Loading)
    val mostAnticipated: StateFlow<HomeState> = _mostAnticipated.asStateFlow()

    private val _giveaways = MutableStateFlow<HomeState>(HomeState.Loading)
    val giveaways: StateFlow<HomeState> = _giveaways.asStateFlow()

    private val _popularRightNow = MutableStateFlow<HomeState>(HomeState.Loading)
    val popularRightNow: StateFlow<HomeState> = _popularRightNow.asStateFlow()

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            Napier.e(message = exception.message.orEmpty(), throwable = exception)
        }

    fun loadHome() {
        loadMostAnticipated()
        loadGiveaways()
        loadPopularRightNow()
    }

    private fun loadMostAnticipated(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            runCatching {
                val gameItems = homeRepo.mostAnticipated(pageSize = pageSize, currentTimestamp = nowUnix)
                gameItems.orEmpty().mapperMiniGameCardModel()
            }
                .onSuccess { result ->
                    _mostAnticipated.emit(HomeState.Success(result))
                }
                .onFailure { throwable ->
                    _mostAnticipated.emit(HomeState.Error(throwable.message.orEmpty()))
                }
        }
    }

    private fun loadGiveaways(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            runCatching {
                val giveaways = gamerPowerRepo.giveaways(pageSize = pageSize)
                giveaways.orEmpty().mapperGiveawayModel()
            }
                .onSuccess { result ->
                    _giveaways.emit(HomeState.Success(result))
                }
                .onFailure { throwable ->
                    _giveaways.emit(HomeState.Error(throwable.message.orEmpty()))
                }
        }
    }

    private fun loadPopularRightNow(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            runCatching {
                val gameItems = homeRepo.popularRightNow(pageSize = pageSize)
                gameItems.orEmpty().mapperMiniGameCardModel()
            }
                .onSuccess { result ->
                    _popularRightNow.emit(HomeState.Success(result))
                }
                .onFailure { throwable ->
                    _popularRightNow.emit(HomeState.Error(throwable.message.orEmpty()))
                }
        }
    }

    private fun List<GameItem>.mapperMiniGameCardModel() =
        map { item ->
            MiniGameCardModel(
                id = item.id.orZero(),
                title = item.name.orEmpty(),
                backgroundImageUrl = item.cover?.url?.toCoverUrl(CoverSize.P_1080).orEmpty(),
                platforms = item.platforms.orEmpty().parentPlatformTypes(),
            )
        }

    private fun List<GiveawayResponse>.mapperGiveawayModel() =
        map { item ->
            GiveawayItemModel(
                id = item.id.orZero(),
                title = item.title.orEmpty(),
                image = item.image.orEmpty(),
                worth = item.worth.takeIf { it != "N/A" }.orEmpty(),
                // TODO: Convert
                endDate = item.endDate.orEmpty(),
                platforms = item.platforms?.giveawayPlatformMapper().orEmpty(),
            )
        }

    private fun String.giveawayPlatformMapper(): List<ParentPlatformType> =
        split(", ").map { Platform(null, it) }.parentPlatformTypes()

    private companion object {
        private const val PAGE_SIZE = 15
    }
}

sealed interface HomeState {
    data object Loading : HomeState

    data class Success<T : HomeStateEntity>(val entities: List<T>) : HomeState

    data class Error(val message: String) : HomeState
}

interface HomeStateEntity
