package com.example.comics.viewmodel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.comics.MockApplication
import com.example.comics.model.DataModel
import com.example.comics.model.ItemModel
import com.example.comics.model.Repository
import com.example.comics.util.Result
import com.example.comics.view.ItemVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import retrofit2.Response

@ExperimentalCoroutinesApi
class ComicsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var repository: Repository

    private lateinit var viewModel: ComicsViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ComicsViewModel(MockApplication(), repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test loadComics success`() = runBlocking {

        val itemModel = ItemModel(data = DataModel(results = listOf())) // Supondo que DataModel tenha um construtor que aceita uma lista de resultados
        val response = Response.success(itemModel)
        Mockito.`when`(repository.getComics()).thenReturn(response)

        viewModel.loadComics()

        Assert.assertEquals(false, viewModel.isViewLoading.value)
        Assert.assertNull(viewModel.error.value)
        Assert.assertEquals(emptyList<ItemVO>(), viewModel.comics.value)
    }

    @Test
    fun `test loadComics failure`() = runBlocking {

        val errorResponse: Response<ItemModel> = Response.error(404, ResponseBody.create(MediaType.parse("application/json"), "Not Found"))

        Mockito.`when`(repository.getComics()).thenReturn(errorResponse)

        viewModel.loadComics()

        Assert.assertEquals(false, viewModel.isViewLoading.value)
        Assert.assertNull(viewModel.comics.value)
    }


    @Test
    fun `test setViewLoading`() {
        viewModel.setViewLoading(true)

        Assert.assertEquals(true, viewModel.isViewLoading.value)

        viewModel.setViewLoading(false)

        Assert.assertEquals(false, viewModel.isViewLoading.value)
    }
}
