package org.pablo.mygas.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.pablo.mygas.data.remote.GasolinerasRepository
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.domain.GetAllComunidadesAutonomasUseCase
import org.pablo.mygas.domain.GetAllGasolinerasByMunicipioUseCase
import org.pablo.mygas.domain.GetAllMunicipiosByProvinciaUseCase
import org.pablo.mygas.ui.viewmodels.GasolineraDetailViewModel
import org.pablo.mygas.ui.viewmodels.GasolineraFavoriteViewModel
import org.pablo.mygas.ui.viewmodels.HomeViewModel

val appModule = module {
    // Api key
    // Database


}
val dataModule = module {
    singleOf(::MunicipioService)
    singleOf(::GasolinerasRepository)


    single {
        HttpClient {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

}

val viewModelsModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::GasolineraFavoriteViewModel)
    viewModelOf(::GasolineraDetailViewModel)
}

val useCasesModule = module {
    factoryOf(::GetAllGasolinerasByMunicipioUseCase)
    factoryOf(::GetAllMunicipiosByProvinciaUseCase)
    factoryOf(::GetAllComunidadesAutonomasUseCase)

}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule, dataModule, viewModelsModule, useCasesModule)
    }
}