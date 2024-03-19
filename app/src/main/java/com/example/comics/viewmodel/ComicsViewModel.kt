package com.example.comics.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.comics.model.ItemModel
import com.example.comics.model.Repository
import com.example.comics.util.Result
import com.example.comics.util.safeRunDispatcher
import com.example.comics.view.ItemVO

class ComicsViewModel(application: Application, private val repository: Repository) : AndroidViewModel(application) {

    private val _comics = MutableLiveData<List<ItemVO>>()
    val comics: LiveData<List<ItemVO>> = _comics

    private val _isViewLoading = MutableLiveData<Boolean>().apply { value = false }
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    suspend fun loadComics() {
        when (val result = safeRunDispatcher {
            repository.getComics()
        }) {
            is Result.Success -> result.data.body()?.apply {
                setupList(this)
                _isViewLoading.postValue(false)
            }

            is Result.Failure -> {
                _isViewLoading.postValue(false)
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
        _isViewLoading.postValue(false)
    }

    fun setViewLoading(isViewLoading: Boolean) {
        _isViewLoading.postValue(isViewLoading)
    }
}