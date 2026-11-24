package org.pablo.mygas.resources

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Every translation must define the same keys, with the same format placeholders, as the default strings. */
class StringsParityTest {
    private val resourcesDir = File("src/commonMain/composeResources")
    private val entry = Regex("""<string name="([^"]+)">(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)
    private val placeholder = Regex("""%\d+\$[sd]""")

    private fun load(folder: String): Map<String, String> =
        entry.findAll(File(resourcesDir, "$folder/strings.xml").readText())
            .associate { it.groupValues[1] to it.groupValues[2] }

    @Test
    fun `every translation has the same keys and placeholders as the default`() {
        val default = load("values")
        val translations = resourcesDir.listFiles { file -> file.isDirectory && file.name.startsWith("values-") }.orEmpty()
        assertTrue(translations.isNotEmpty())

        translations.forEach { folder ->
            val translated = load(folder.name)
            assertEquals(default.keys, translated.keys, "keys in ${folder.name}")
            default.forEach { (key, value) ->
                assertEquals(
                    placeholder.findAll(value).map { it.value }.sorted().toList(),
                    placeholder.findAll(translated.getValue(key)).map { it.value }.sorted().toList(),
                    "placeholders of $key in ${folder.name}",
                )
            }
        }
    }
}
