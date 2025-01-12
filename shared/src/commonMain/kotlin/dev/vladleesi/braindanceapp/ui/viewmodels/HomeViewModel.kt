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

    private val _popularThisYear = MutableStateFlow<List<MiniGameCardModel>>(emptyList())
    val popularThisYear: StateFlow<List<MiniGameCardModel>> = _popularThisYear.asStateFlow()

    private val _popularLastYear = MutableStateFlow<List<MiniGameCardModel>>(emptyList())
    val popularLastYear: StateFlow<List<MiniGameCardModel>> = _popularLastYear.asStateFlow()

    private val _allTimeTop = MutableStateFlow<List<MiniGameCardModel>>(emptyList())
    val allTimeTop: StateFlow<List<MiniGameCardModel>> = _allTimeTop.asStateFlow()

    private val _thisWeekReleases = MutableStateFlow<List<MiniGameCardModel>>(emptyList())
    val thisWeekReleases: StateFlow<List<MiniGameCardModel>> = _thisWeekReleases.asStateFlow()

    // TODO: Move to global coroutine config
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
            val result = homeRepo.bestOfTheYear(pageSize = pageSize, year = currentYear)
            _popularThisYear.emit(
                result.results.orEmpty().mapperMiniGameCardModel(),
            )
        }
    }

    private fun loadPopularLastYear(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            val result = homeRepo.bestOfTheYear(pageSize = pageSize, year = lastYear)
            _popularLastYear.emit(
                result.results.orEmpty().mapperMiniGameCardModel(),
            )
        }
    }

    private fun loadPopularAllTime(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            val result = homeRepo.popular(pageSize = pageSize)
            _allTimeTop.emit(
                result.results.orEmpty().mapperMiniGameCardModel(),
            )
        }
    }

    private fun loadThisWeekReleases(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            val result = homeRepo.thisWeekReleases(pageSize = pageSize)
            _thisWeekReleases.emit(
                result.results.orEmpty().mapperMiniGameCardModel(),
            )
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
