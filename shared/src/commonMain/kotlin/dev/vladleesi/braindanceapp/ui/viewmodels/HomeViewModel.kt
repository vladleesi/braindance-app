package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.api.remote.CompilationRemote
import dev.vladleesi.braindanceapp.data.models.games.Game
import dev.vladleesi.braindanceapp.data.repository.HomeRepo
import dev.vladleesi.braindanceapp.ui.components.MiniGameCardModel
import dev.vladleesi.braindanceapp.utils.currentYear
import dev.vladleesi.braindanceapp.utils.lastYear
import dev.vladleesi.braindanceapp.utils.orZero
import dev.vladleesi.braindanceapp.utils.parentPlatformTypes
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    @Suppress("ForbiddenComment")
    // TODO: Move to DI
    private val homeRepo = HomeRepo(CompilationRemote())

    private val _popularThisYear = MutableStateFlow<HomeState>(HomeState.Loading)
    val popularThisYear: StateFlow<HomeState> = _popularThisYear.asStateFlow()

    private val _popularLastYear = MutableStateFlow<HomeState>(HomeState.Loading)
    val popularLastYear: StateFlow<HomeState> = _popularLastYear.asStateFlow()

    private val _allTimeTop = MutableStateFlow<HomeState>(HomeState.Loading)
    val allTimeTop: StateFlow<HomeState> = _allTimeTop.asStateFlow()

    private val _thisWeekReleases = MutableStateFlow<HomeState>(HomeState.Loading)
    val thisWeekReleases: StateFlow<HomeState> = _thisWeekReleases.asStateFlow()

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            Napier.e(message = exception.message.orEmpty(), throwable = exception)
        }

    fun loadHome() {
        loadPopularThisYear()
        loadPopularLastYear()
        loadPopularAllTime()
        loadThisWeekReleases()
    }

    private fun loadPopularThisYear(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            runCatching {
                homeRepo.bestOfTheYear(pageSize = pageSize, year = currentYear)
            }
                .onSuccess { result ->
                    _popularThisYear.emit(
                        HomeState.Success(result.results.orEmpty().mapperMiniGameCardModel()),
                    )
                }
                .onFailure { throwable ->
                    _popularThisYear.emit(HomeState.Error(throwable.message.orEmpty()))
                }
        }
    }

    private fun loadPopularLastYear(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            runCatching {
                homeRepo.bestOfTheYear(pageSize = pageSize, year = lastYear)
            }
                .onSuccess { result ->
                    _popularLastYear.emit(
                        HomeState.Success(result.results.orEmpty().mapperMiniGameCardModel()),
                    )
                }
                .onFailure { throwable ->
                    _popularLastYear.emit(HomeState.Error(throwable.message.orEmpty()))
                }
        }
    }

    private fun loadPopularAllTime(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            runCatching {
                homeRepo.popular(pageSize = pageSize)
            }
                .onSuccess { result ->
                    _allTimeTop.emit(
                        HomeState.Success(result.results.orEmpty().mapperMiniGameCardModel()),
                    )
                }
                .onFailure { throwable ->
                    _allTimeTop.emit(HomeState.Error(throwable.message.orEmpty()))
                }
        }
    }

    private fun loadThisWeekReleases(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            runCatching {
                homeRepo.thisWeekReleases(pageSize = pageSize)
            }
                .onSuccess { result ->
                    _thisWeekReleases.emit(
                        HomeState.Success(result.results.orEmpty().mapperMiniGameCardModel()),
                    )
                }
                .onFailure { throwable ->
                    _thisWeekReleases.emit(HomeState.Error(throwable.message.orEmpty()))
                }
        }
    }

    private fun List<Game>.mapperMiniGameCardModel() = map { game -> game.toMiniGameCardModel() }

    private fun Game.toMiniGameCardModel() =
        MiniGameCardModel(
            id = id.orZero(),
            title = name.orEmpty(),
            backgroundImageUrl = backgroundImageUrl.orEmpty(),
            platforms = parentPlatforms.orEmpty().parentPlatformTypes(),
        )

    private companion object {
        private const val PAGE_SIZE = 10
    }
}

sealed interface HomeState {
    data object Loading : HomeState

    data class Success(val games: List<MiniGameCardModel>) : HomeState

    data class Error(val message: String) : HomeState
}
