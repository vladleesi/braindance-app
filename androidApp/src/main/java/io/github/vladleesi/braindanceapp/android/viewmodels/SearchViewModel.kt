package io.github.vladleesi.braindanceapp.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vladleesi.braindanceapp.api.remote.SearchRemote
import io.github.vladleesi.braindanceapp.models.games.Game
import io.github.vladleesi.braindanceapp.repository.SearchRepo
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    @SuppressWarnings("ForbiddenComment")
    // TODO: Move to DI
    private val searchRepo = SearchRepo(SearchRemote())

    private val _searchResult = MutableLiveData<List<Game>>()
    val searchResult: LiveData<List<Game>> = _searchResult

    fun search(query: CharSequence) {
        viewModelScope.launch {
            val result = searchRepo.search(query.toString())
            _searchResult.postValue(result)
        }
    }
}
