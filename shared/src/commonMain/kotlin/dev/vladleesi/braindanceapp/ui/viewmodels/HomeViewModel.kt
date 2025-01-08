package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.api.remote.CompilationRemote
import dev.vladleesi.braindanceapp.data.repository.HomeRepo
import dev.vladleesi.braindanceapp.ui.components.MiniGameCard
import dev.vladleesi.braindanceapp.utils.currentYear
import dev.vladleesi.braindanceapp.utils.lastYear
import dev.vladleesi.braindanceapp.utils.orZero
import dev.vladleesi.braindanceapp.utils.platformTypeShort
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

    private val _bestOfTheYear = MutableStateFlow<List<MiniGameCard>>(emptyList())
    val bestOfTheYear: StateFlow<List<MiniGameCard>> = _bestOfTheYear.asStateFlow()

    private val _popularLastYear = MutableStateFlow<List<MiniGameCard>>(emptyList())
    val popularLastYear: StateFlow<List<MiniGameCard>> = _popularLastYear.asStateFlow()

    private val _allTimeTop = MutableStateFlow<List<MiniGameCard>>(emptyList())
    val allTimeTop: StateFlow<List<MiniGameCard>> = _allTimeTop.asStateFlow()

    // TODO: Move to global coroutine config
    private val handler =
        CoroutineExceptionHandler { _, exception ->
            Napier.e(message = exception.message.orEmpty(), throwable = exception)
        }

    fun load() {
        viewModelScope.launch(handler) {
            val result = homeRepo.bestOfTheYear(pageSize = PAGE_SIZE, year = currentYear)
            _bestOfTheYear.emit(
                result.results.orEmpty().map {
                    MiniGameCard(
                        id = it.id.orZero(),
                        title = it.name.orEmpty(),
                        backgroundImageUrl = it.backgroundImageUrl.orEmpty(),
                        platforms =
                            it.platforms.orEmpty().map { platform -> platform.platformTypeShort().toString() }
                                .distinct(),
                    )
                },
            )
        }

        viewModelScope.launch(handler) {
            val result = homeRepo.bestOfTheYear(pageSize = PAGE_SIZE, year = lastYear)
            _popularLastYear.emit(
                result.results.orEmpty().map {
                    MiniGameCard(
                        id = it.id.orZero(),
                        title = it.name.orEmpty(),
                        backgroundImageUrl = it.backgroundImageUrl.orEmpty(),
                        platforms =
                            it.platforms.orEmpty().map { platform -> platform.platformTypeShort().toString() }
                                .distinct(),
                    )
                },
            )
        }

        viewModelScope.launch(handler) {
            val result = homeRepo.popular(pageSize = PAGE_SIZE)
            _allTimeTop.emit(
                result.results.orEmpty().map {
                    MiniGameCard(
                        id = it.id.orZero(),
                        title = it.name.orEmpty(),
                        backgroundImageUrl = it.backgroundImageUrl.orEmpty(),
                        platforms =
                            it.platforms.orEmpty().map { platform -> platform.platformTypeShort().toString() }
                                .distinct(),
                    )
                },
            )
        }
    }

    private companion object {
        private const val PAGE_SIZE = 10
    }
}
