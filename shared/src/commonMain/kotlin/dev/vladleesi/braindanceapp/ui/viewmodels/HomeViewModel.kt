package dev.vladleesi.braindanceapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.api.remote.CompilationRemote
import dev.vladleesi.braindanceapp.data.repository.HomeRepo
import dev.vladleesi.braindanceapp.ui.components.MiniGameCard
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

    private val _popular = MutableStateFlow<List<MiniGameCard>>(emptyList())
    val popular: StateFlow<List<MiniGameCard>> = _popular.asStateFlow()

    // TODO: Move to global coroutine config
    private val handler =
        CoroutineExceptionHandler { _, exception ->
            Napier.e(message = exception.message.orEmpty(), throwable = exception)
        }

    fun load() {
        viewModelScope.launch(handler) {
            val result = homeRepo.popular(pageSize = PAGE_SIZE)
            _popular.emit(
                result.results.orEmpty().map {
                    MiniGameCard(
                        title = it.name.orEmpty(),
                        backgroundImage = it.backgroundImage.orEmpty(),
                    )
                },
            )
        }
    }

    private companion object {
        private const val PAGE_SIZE = 5
    }
}
