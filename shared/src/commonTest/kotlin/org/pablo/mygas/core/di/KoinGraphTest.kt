package org.pablo.mygas.core.di

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.pablo.mygas.domain.repositories.FavoritesRepository
import org.pablo.mygas.domain.repositories.GasStationRepository
import org.pablo.mygas.domain.repositories.LocationRepository
import org.pablo.mygas.domain.usecases.GetMunicipalitiesUseCase
import org.pablo.mygas.domain.usecases.GetStationUseCase
import org.pablo.mygas.domain.usecases.GetStationsByMunicipalityUseCase
import org.pablo.mygas.domain.usecases.GetStationsInAreaUseCase
import org.pablo.mygas.domain.usecases.ObserveFavoriteIdsUseCase
import org.pablo.mygas.domain.usecases.SetFavoriteUseCase
import org.pablo.mygas.testing.FakeFavorites
import org.pablo.mygas.testing.FakeLocationRepository
import org.pablo.mygas.testing.FakeStationRepository
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel
import org.pablo.mygas.ui.viewmodels.HomeViewModel
import org.pablo.mygas.ui.viewmodels.MapViewModel
import org.pablo.mygas.ui.viewmodels.SettingsViewModel
import kotlin.test.Test
import kotlin.test.assertNotNull

@kotlinx.coroutines.ExperimentalCoroutinesApi
class KoinGraphTest {
    @Test
    fun `every view model and use case can be resolved`() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val fakes = module {
            single<Settings> { MapSettings() }
            single<FavoritesRepository> { FakeFavorites() }
            single<LocationRepository> { FakeLocationRepository() }
            single<GasStationRepository> { FakeStationRepository() }
        }
        val application = koinApplication { modules(allModules + fakes) }

        try {
            val koin = application.koin
            assertNotNull(koin.get<HomeViewModel>())
            assertNotNull(koin.get<GasolineraFavoriteViewModel>())
            assertNotNull(koin.get<GasolineraDetailViewModel>())
            assertNotNull(koin.get<MapViewModel>())
            assertNotNull(koin.get<SettingsViewModel>())
            assertNotNull(koin.get<GetMunicipalitiesUseCase>())
            assertNotNull(koin.get<GetStationsByMunicipalityUseCase>())
            assertNotNull(koin.get<GetStationUseCase>())
            assertNotNull(koin.get<GetStationsInAreaUseCase>())
            assertNotNull(koin.get<ObserveFavoriteIdsUseCase>())
            assertNotNull(koin.get<SetFavoriteUseCase>())
        } finally {
            application.close()
            Dispatchers.resetMain()
        }
    }
}
