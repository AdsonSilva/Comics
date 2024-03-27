package com.example.comics.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.comics.model.ItemModel
import com.example.comics.model.Repository
import com.example.comics.util.Result
import com.example.comics.util.safeRunDispatcher
import com.example.comics.view.ItemVO
import kotlinx.coroutines.launch

class ComicsViewModel(application: Application, private val repository: Repository) : AndroidViewModel(application) {

    private val _comics = MutableLiveData<List<ItemVO>>()
    val comics: LiveData<List<ItemVO>> = _comics

    var isRefreshing by mutableStateOf(false)
        private set

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    fun getComics() = viewModelScope.launch {
        isRefreshing = true
        when (val result = safeRunDispatcher {
            repository.getComics()
        }) {
            is Result.Success -> result.data.body()?.apply {
                setupList(this)
                isRefreshing = false

            }

            is Result.Failure -> {
                isRefreshing = false
                _error.postValue(result.error)
            }
        }
    }

    private fun setupList(list: ItemModel) {
           val comicsList = list.data.results.map {
                ItemVO(
                    image = "${it.thumbnail.path}.${it.thumbnail.extension}",
                    title = it.title,
                    subtitle = it.description ?: "Sem descricao"
                )
            }

        _comics.postValue(comicsList)
        isRefreshing = false
    }
}