package com.example.comics.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.coroutineScope
import coil.compose.AsyncImage
import com.example.comics.view.ui.theme.ComicsTheme
import com.example.comics.viewmodel.ComicsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class ComicsActivity : ComponentActivity() {

    private val comicsViewModel: ComicsViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComicsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ComicsList(comicsViewModel = comicsViewModel)
                }
            }
        }

        comicsViewModel.getComics()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComicsList(
    comicsViewModel: ComicsViewModel = viewModel(),
) {

    val pullRefreshState = rememberPullRefreshState(
        refreshing = comicsViewModel.isRefreshing,
        onRefresh = { comicsViewModel.getComics() }
    )

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(comicsViewModel.comics.value ?: listOf()) {
                ComicListItem(item = it)
            }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = comicsViewModel.isRefreshing,
            state = pullRefreshState
        )
    }
}

@Composable
fun ComicListItem(item: ItemVO, modifier: Modifier = Modifier) {
    ConstraintLayout (modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()
        .wrapContentHeight()) {
        val (image, title, description) = createRefs()

        AsyncImage(
            model = item.image,
            contentDescription = null,
            modifier = modifier
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .width(120.dp)
                .height(120.dp)
        )

        Text(
            text = item.title,
            modifier = modifier.constrainAs(title) {
                top.linkTo(image.top)
                start.linkTo(image.end, margin = 16.dp)
            }
        )

        Text(
            text = item.subtitle,
            modifier = modifier.constrainAs(description) {
                top.linkTo(title.bottom)
                start.linkTo(title.start)
                bottom.linkTo(image.bottom)
            }
        )
    }
}

@Composable
fun GreetingPreview() {
    ComicsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ComicsList()
        }
    }
}