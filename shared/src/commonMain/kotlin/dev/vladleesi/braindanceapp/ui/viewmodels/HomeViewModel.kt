package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.api.remote.CompilationRemote
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

    // TODO: Move to global coroutine config
    private val handler =
        CoroutineExceptionHandler { _, exception ->
            Napier.e(message = exception.message.orEmpty(), throwable = exception)
        }

    fun loadPopularThisYear(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            val result = homeRepo.bestOfTheYear(pageSize = pageSize, year = currentYear)
            _popularThisYear.emit(
                result.results.orEmpty().map {
                    MiniGameCardModel(
                        id = it.id.orZero(),
                        title = it.name.orEmpty(),
                        backgroundImageUrl = it.backgroundImageUrl.orEmpty(),
                        platforms = it.parentPlatforms.orEmpty().parentPlatformTypes(),
                    )
                },
            )
        }
    }

    fun loadPopularLastYear(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            val result = homeRepo.bestOfTheYear(pageSize = pageSize, year = lastYear)
            _popularLastYear.emit(
                result.results.orEmpty().map {
                    MiniGameCardModel(
                        id = it.id.orZero(),
                        title = it.name.orEmpty(),
                        backgroundImageUrl = it.backgroundImageUrl.orEmpty(),
                        platforms = it.parentPlatforms.orEmpty().parentPlatformTypes(),
                    )
                },
            )
        }
    }

    fun loadPopularAllTime(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch(handler) {
            val result = homeRepo.popular(pageSize = pageSize)
            _allTimeTop.emit(
                result.results.orEmpty().map {
                    MiniGameCardModel(
                        id = it.id.orZero(),
                        title = it.name.orEmpty(),
                        backgroundImageUrl = it.backgroundImageUrl.orEmpty(),
                        platforms = it.parentPlatforms.orEmpty().parentPlatformTypes(),
                    )
                },
            )
        }
    }

    private companion object {
        private const val PAGE_SIZE = 10
    }
}
