package org.pablo.mygas.ui.viewmodels

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.pablo.mygas.core.utils.NetworkError
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.local.UserPreferencesRepositoryImpl
import org.pablo.mygas.domain.usecases.GetStationUseCase
import org.pablo.mygas.domain.usecases.ObserveFavoriteIdsUseCase
import org.pablo.mygas.domain.usecases.SetFavoriteUseCase
import org.pablo.mygas.testing.FakeFavorites
import org.pablo.mygas.testing.FakeStationRepository
import org.pablo.mygas.testing.station
import org.pablo.mygas.ui.state.Loadable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@kotlinx.coroutines.ExperimentalCoroutinesApi
class FavoritesAndDetailViewModelTest {

    private fun viewModelTest(block: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun detail(stations: FakeStationRepository, favorites: FakeFavorites) = GasolineraDetailViewModel(
        GetStationUseCase(stations),
        ObserveFavoriteIdsUseCase(favorites),
        SetFavoriteUseCase(favorites),
        UserPreferencesRepositoryImpl(MapSettings()),
    )

    private fun favoriteList(stations: FakeStationRepository, favorites: FakeFavorites) = GasolineraFavoriteViewModel(
        ObserveFavoriteIdsUseCase(favorites),
        GetStationUseCase(stations),
        UserPreferencesRepositoryImpl(MapSettings()),
    )

    @Test
    fun `detail loads the station and exposes the typed error on failure`() = viewModelTest {
        val stations = FakeStationRepository()
        stations.stationResults[1] = Result.Success(station(1))
        val viewModel = detail(stations, FakeFavorites())

        viewModel.getGasStationById(1)
        advanceUntilIdle()
        assertIs<Loadable.Content<*>>(viewModel.state.value.loadState)

        viewModel.getGasStationById(99)
        advanceUntilIdle()
        assertEquals(Loadable.Failed(NetworkError.NOT_FOUND), viewModel.state.value.loadState)
    }

    @Test
    fun `detail tracks the favourite flag as it is toggled`() = viewModelTest {
        val favorites = FakeFavorites()
        val viewModel = detail(FakeStationRepository(), favorites)
        viewModel.checkFavorite(5)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isFavorite)

        viewModel.insertGasStationId(5)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isFavorite)

        viewModel.removeGasStationId(5)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isFavorite)
    }

    @Test
    fun `calling checkFavorite twice does not keep two collectors alive`() = viewModelTest {
        val favorites = FakeFavorites()
        val viewModel = detail(FakeStationRepository(), favorites)

        viewModel.checkFavorite(5)
        viewModel.checkFavorite(5)
        favorites.addFavorite(5)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isFavorite)
    }

    @Test
    fun `favourites list shows the stations that loaded and hides the failed ones`() = viewModelTest {
        val stations = FakeStationRepository()
        stations.stationResults[1] = Result.Success(station(1))
        val favorites = FakeFavorites().also { it.addFavorite(1); it.addFavorite(2) }

        val viewModel = favoriteList(stations, favorites)
        advanceUntilIdle()

        val loaded = assertIs<Loadable.Content<List<org.pablo.mygas.domain.models.GasStation>>>(viewModel.state.value.loadState)
        assertEquals(listOf(1), loaded.value.map { it.id })
    }

    @Test
    fun `favourites list fails only when nothing could be loaded`() = viewModelTest {
        val favorites = FakeFavorites().also { it.addFavorite(2) }

        val viewModel = favoriteList(FakeStationRepository(), favorites)
        advanceUntilIdle()

        assertEquals(Loadable.Failed(NetworkError.NOT_FOUND), viewModel.state.value.loadState)
    }

    @Test
    fun `favourites list is empty without favourites`() = viewModelTest {
        val viewModel = favoriteList(FakeStationRepository(), FakeFavorites())
        advanceUntilIdle()

        val loaded = assertIs<Loadable.Content<List<org.pablo.mygas.domain.models.GasStation>>>(viewModel.state.value.loadState)
        assertTrue(loaded.value.isEmpty())
    }
}
