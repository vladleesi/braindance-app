package dev.vladleesi.braindanceapp.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vladleesi.braindanceapp.data.api.remote.SearchRemote
import dev.vladleesi.braindanceapp.data.models.search.SearchResult
import dev.vladleesi.braindanceapp.data.repository.SearchRepo
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    @SuppressWarnings("ForbiddenComment")
    // TODO: Move to DI
    private val searchRepo = SearchRepo(SearchRemote())

    private val _searchResult = MutableLiveData<SearchResult>()
    val searchResult: LiveData<SearchResult> = _searchResult

    fun search(query: CharSequence) {
        viewModelScope.launch {
            val result = searchRepo.search(query.toString())
            _searchResult.postValue(result)
        }
    }
}
