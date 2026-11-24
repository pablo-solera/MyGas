package org.pablo.mygas.core.di

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.pablo.mygas.data.local.UserPreferencesRepositoryImpl
import org.pablo.mygas.data.local.database.AppDatabase
import org.pablo.mygas.data.local.database.GasStationDao
import org.pablo.mygas.data.local.database.RoomGasStationAdapter
import org.pablo.mygas.data.local.database.buildDatabase
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.adapters.PrecioilGasStationAdapter
import org.pablo.mygas.data.remote.datasources.PrecioilGasStationDataSource
import org.pablo.mygas.data.repositories.GasStationRepositoryImpl
import org.pablo.mygas.domain.repositories.GasStationRepository
import org.pablo.mygas.domain.repositories.UserPreferencesRepository
import org.pablo.mygas.domain.usecases.GetMunicipalitiesUseCase
import org.pablo.mygas.domain.usecases.GetStationUseCase
import org.pablo.mygas.domain.usecases.GetStationsByMunicipalityUseCase
import org.pablo.mygas.domain.usecases.ObserveFavoriteIdsUseCase
import org.pablo.mygas.domain.usecases.SetFavoriteUseCase
import org.pablo.mygas.data.repositories.LocationRepositoryImpl
import org.pablo.mygas.domain.repositories.FavoritesRepository
import org.pablo.mygas.domain.repositories.LocationRepository
import org.pablo.mygas.domain.usecases.GetStationsInAreaUseCase
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel
import org.pablo.mygas.ui.viewmodels.HomeViewModel
import org.pablo.mygas.ui.viewmodels.MapViewModel
import org.pablo.mygas.ui.viewmodels.SettingsViewModel
import org.pablo.mygas.ui.viewmodels.ThemeViewModel

val appModule = module {
    single<AppDatabase> { buildDatabase(get()) }
    single<GasStationDao> { get<AppDatabase>().gasStationDao() }
}

val dataModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true })
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
            }
        }
    }
    single { MunicipioService(get()) }
    single<PrecioilGasStationDataSource> { PrecioilGasStationAdapter(get()) }
    single<FavoritesRepository> { RoomGasStationAdapter(get()) }
    single<GasStationRepository> { GasStationRepositoryImpl(get(), get()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    single<Settings> { Settings() }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }
}

val viewModelsModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModelOf(::GasolineraFavoriteViewModel)
    viewModelOf(::GasolineraDetailViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ThemeViewModel)
}

val useCasesModule = module {
    factoryOf(::GetMunicipalitiesUseCase)
    factoryOf(::GetStationsByMunicipalityUseCase)
    factoryOf(::GetStationUseCase)
    factoryOf(::GetStationsInAreaUseCase)
    factoryOf(::ObserveFavoriteIdsUseCase)
    factoryOf(::SetFavoriteUseCase)
}

val allModules = listOf(platformModule, appModule, dataModule, viewModelsModule, useCasesModule)

/** Safe to call more than once: the platform entry points may be re-created. */
fun initKoin(config: KoinAppDeclaration? = null) {
    if (GlobalContext.getOrNull() != null) return
    startKoin {
        config?.invoke(this)
        modules(allModules)
    }
}
