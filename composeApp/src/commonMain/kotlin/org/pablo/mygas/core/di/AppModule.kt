package org.pablo.mygas.core.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.pablo.mygas.data.local.database.AppDatabase
import org.pablo.mygas.data.local.database.GasStationDao
import org.pablo.mygas.data.local.database.RoomGasStationAdapter
import org.pablo.mygas.data.local.UserPreferencesRepositoryImpl
import org.pablo.mygas.data.local.database.getRoomDatabase
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.data.remote.adapters.PrecioilGasStationAdapter
import org.pablo.mygas.data.remote.datasources.GovernmentGasStationDataSource
import org.pablo.mygas.data.remote.repository.GasolinerasRepository
import org.pablo.mygas.domain.usecases.GetAllComunidadesAutonomasUseCase
import org.pablo.mygas.domain.usecases.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.usecases.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.domain.usecases.GetGasolineraByIdUseCase
import org.pablo.mygas.domain.repositories.UserPreferencesRepository
import com.russhwolf.settings.Settings
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel
import org.pablo.mygas.ui.viewmodels.HomeViewModel
import org.pablo.mygas.ui.viewmodels.MapViewModel
import org.pablo.mygas.ui.viewmodels.SettingsViewModel

val appModule = module {
    // Api key
    // Database
    single<GasStationDao> { get<AppDatabase>().gasStationDao() }
    single<AppDatabase>{ getRoomDatabase() }

}
val dataModule = module {
    singleOf(::MunicipioService)
    singleOf(::GasolinerasRepository)
    single<GovernmentGasStationDataSource> { get<GasolinerasRepository>() }
    singleOf(::PrecioilGasStationAdapter)
    singleOf(::RoomGasStationAdapter)
    single<Settings> { Settings() }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }



    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true ; isLenient = true})
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5_000
            }
        }
    }

}

val viewModelsModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModelOf(::GasolineraFavoriteViewModel)
    viewModelOf(::GasolineraDetailViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::SettingsViewModel)

}

val useCasesModule = module {
    factoryOf(::GetAllGasolinerasByMunicipioUseCase)
    factoryOf(::GetAllMunicipiosByProvinciaUseCase)
    factoryOf(::GetAllComunidadesAutonomasUseCase)
    factoryOf(::GetGasolineraByIdUseCase)

}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule, dataModule, viewModelsModule, useCasesModule)
    }
}
