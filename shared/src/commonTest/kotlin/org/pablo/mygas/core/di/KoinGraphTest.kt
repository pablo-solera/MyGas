package org.pablo.mygas.core.di

import kotlin.test.Test
import kotlin.test.assertIs
import org.koin.dsl.koinApplication
import org.pablo.mygas.data.remote.datasources.GovernmentGasStationDataSource
import org.pablo.mygas.data.remote.repository.GasolinerasRepository

class KoinGraphTest {
    @Test
    fun `government data source resolves to the repository binding`() {
        val application = koinApplication {
            modules(appModule, dataModule, viewModelsModule, useCasesModule)
        }

        try {
            assertIs<GasolinerasRepository>(
                application.koin.get<GovernmentGasStationDataSource>()
            )
        } finally {
            application.close()
        }
    }
}
