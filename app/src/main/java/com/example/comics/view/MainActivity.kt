package com.example.comics.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.comics.databinding.ActivityMainBinding
import com.example.comics.viewmodel.ComicsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val comicsViewModel: ComicsViewModel by viewModel()

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupObserver()
        refresh()

        swipeList()
    }

    private fun setupObserver() {
        comicsViewModel.comics.observe(this) {
            viewList(it)
        }

        comicsViewModel.isViewLoading.observe(this) {isLoading ->
            with(binding) {
                this?.swipeRefresh?.isRefreshing = isLoading
            }
        }

        comicsViewModel.error.observe(this) {
            this.error()
        }
    }

    private fun swipeList() = with(binding?.swipeRefresh) {
        this?.setOnRefreshListener {
            refresh()
        }
    }

    private fun refresh() {
        comicsViewModel.setViewLoading(true)
        lifecycle.coroutineScope.launch {
            comicsViewModel.loadComics()
        }
    }

    private fun viewList(list: List<ItemVO>) {
        with(binding) {
            this?.errorTV?.visibility = View.GONE
            this?.listItem?.visibility = View.VISIBLE
            this?.listItem?.adapter = Adapter(list)
            this?.listItem?.layoutManager = LinearLayoutManager(this@MainActivity)
            comicsViewModel.setViewLoading(false)
        }
    }

    private fun error() {
        with(binding) {
            this?.listItem?.visibility = View.GONE
            this?.errorTV?.visibility = View.VISIBLE
            comicsViewModel.setViewLoading(false)
        }
    }



}