package org.pablo.mygas.data.repositories

import kotlinx.coroutines.test.runTest
import org.pablo.mygas.core.utils.Result
import org.pablo.mygas.data.remote.MunicipioService
import org.pablo.mygas.domain.models.Municipality
import org.pablo.mygas.testing.jsonResponse
import org.pablo.mygas.testing.testClient
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationRepositoryImplTest {
    @Test
    fun `municipalities are mapped and entries with a bad id are skipped`() = runTest {
        val client = testClient {
            jsonResponse(
                """[{"IDMunicipio":"4354","Municipio":"Madrid","IDProvincia":"28"},
                    {"IDMunicipio":"x","Municipio":"Broken","IDProvincia":"28"}]"""
            )
        }

        val result = LocationRepositoryImpl(MunicipioService(client)).getMunicipalities("28")

        assertEquals(Result.Success(listOf(Municipality(4354, "Madrid", "28"))), result)
        client.close()
    }
}
