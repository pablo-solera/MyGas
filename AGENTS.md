# AGENTS.md — MyGas

App Compose Multiplatform (Android, iOS, Desktop JVM) para consultar precios de carburante en España por provincia y municipio, con favoritos y mapa. Package base: `org.pablo.mygas`. Idioma de la UI y de la conversación: español.

## Entorno

- Desarrollo en **Windows**: no se puede compilar ni probar iOS localmente. Todo lo de iOS se valida solo por revisión de código o en CI con runner macOS. No afirmes que algo funciona en iOS sin haberlo compilado.
- Cuando cambies código de `commonMain`, comprueba que no usa APIs de JVM/Android (debe compilar también en Kotlin/Native).

## Estructura

| Módulo | Contenido |
|---|---|
| `:shared` | Biblioteca KMP (plugin `com.android.kotlin.multiplatform.library`). Todo el código compartido y de plataforma. Targets: `android`, `iosArm64`, `iosSimulatorArm64` (framework estático `ComposeApp`), `jvm("desktop")` |
| `:androidApp` | App Android: `MainActivity`, `MyGasApplication`, manifest, recursos, firma, R8 (`proguard-rules.pro`), versionCode/versionName |
| `:desktopApp` | Solo `main.kt` y `compose.desktop` (empaquetado Dmg/Msi/Deb) |
| `:baselineprofile` | Generador de baseline profile (UiAutomator). Apunta a `:androidApp`. `baseline-prof.txt` está versionado pero está obsoleto (ver "Pendiente") |
| `iosApp/` | Proyecto Xcode (SwiftUI mínimo que envuelve `MainViewController`). Framework directo, sin CocoaPods ni SPM. Compila con `:shared:embedAndSignAppleFrameworkForXcode` |

Source sets en `shared/src`: `commonMain`, `androidMain`, `iosMain`, `desktopMain`, `commonTest`.

Paquetes en `org.pablo.mygas`: `core` (di, navigation, state, utils), `data` (local, remote), `domain` (models, repositories, usecases), `ui` (components, screens, state, theme, viewmodels). Los recursos de Compose se importan de `org.pablo.mygas.resources`.

`expect/actual` actuales: `mapAvailable`, `mapNavigationAvailable`, `MapOpener`, `provideDatabaseBuilder()`, `GasStationDatabaseConstructor`, composable `Map(...)`.

Mapa por plataforma: Android usa `maps-compose`; iOS es un stub (`mapAvailable = false`); **Desktop no tiene mapa** (se eliminó KCEF/Chromium embebido): la pestaña se oculta y el botón del detalle abre Google Maps en el navegador del sistema.

## Comandos

```bash
./gradlew :androidApp:assembleDebug         # Android debug
./gradlew :desktopApp:run                   # Desktop
./gradlew :shared:desktopTest :shared:testAndroidHostTest   # tests ejecutables en Windows
./gradlew :androidApp:lintDebug             # lint
MAPS_API_KEY=... ./gradlew :androidApp:assembleRelease      # release con R8 (exige clave)
./gradlew :desktopApp:packageMsi            # instalador Windows
```

En Windows usa `gradlew.bat` si `./gradlew` falla. No ejecutes tareas `ios*` ni `link*Ios*` aquí.

Hace falta `local.properties` con `sdk.dir=...` (está ignorado por git) y la plataforma Android 37 (compileSdk 37; la instala AGP al aceptar licencias).

## Clave de Google Maps

Solo la usa Android. Se busca en este orden: propiedad Gradle, variable de entorno, `secrets.properties` (raíz, ignorado por git):

```properties
MAPS_API_KEY=...
```

Es **opcional para debug y tests** (el mapa sale gris sin clave) y **obligatoria para `assembleRelease`/`bundleRelease`**: el build falla con un mensaje claro si falta. Nunca escribas claves reales en el repositorio, en logs ni en commits.

## Versiones (estado actual)

Gradle 9.6.0 · AGP 9.5.2 · Kotlin 2.4.21 · Compose Multiplatform 1.12.2 · KSP 2.3.14 · Koin 4.1.1 · Ktor 3.3.0 · Room 2.8.1 (BundledSQLiteDriver) · navigation-compose (JetBrains) 2.10.0 · lifecycle (JetBrains) 2.10.0 · kotlinx.serialization 1.9.0 · compileSdk 37 · targetSdk 36 · minSdk 24. JDK 17+ (se usa el instalado, 25).

Las dependencias van en `gradle/libs.versions.toml`; no añadas versiones directamente en los `build.gradle.kts`. Ya no existe `iosX64` (lo retiró Compose Multiplatform).

## Arquitectura objetivo: MVVM por capas

Se **mantiene MVVM** (no migrar a MVI). Dirección de dependencias: `ui → domain ← data`.

- **domain**: modelos puros de Kotlin, interfaces de repositorio y casos de uso. Sin DTOs, sin Ktor, sin Room, sin `Context`.
- **data**: DTOs (`*Response`), entidades Room, servicios Ktor, mappers DTO/entidad → dominio, implementaciones de las interfaces de `domain`.
- **ui**: ViewModels + `XState` expuesto como `StateFlow`. Los ViewModels dependen solo de casos de uso o interfaces de `domain`, nunca de adapters concretos de `data`.
- Estados de carga: usa `ui/state/Loadable` (`Idle/Loading/Content/Failed`). No mezcles con flags `isLoading` paralelos.
- Errores: `core/utils/Result<D, E>` con `NetworkError`. Relanza siempre `CancellationException`.
- En Compose usa `collectAsStateWithLifecycle()`; las pantallas no deben hacer `koinInject` de repositorios, deben pedir los datos a su ViewModel.

### Deuda conocida (ordenar capas)

Al tocar estas zonas, aprovecha para corregirlas, pero en cambios pequeños y verificables:

1. Casos de uso que devuelven DTOs (`GasolinerasResponse`, `PrecioilGasStationResponse`, `MunicipioItem` llegan a la UI): crear modelos de dominio y mapear en `data`.
2. `domain/repositories/GasStationRepository` y `LocationRepository` son interfaces sin implementación.
3. `GasolineraDetailViewModel` y `GasolineraFavoriteViewModel` inyectan adapters concretos; deben usar casos de uso/interfaces.
4. `GetAllComunidadesAutonomasUseCase` depende de `GasolinerasRepository` concreto, no de `GovernmentGasStationDataSource`.
5. Nombres invertidos: `GasolinerasRepository` es en realidad un data source.
6. Android obtiene `Context` con `GlobalContext.get()` (`MapOpener.android.kt`, `AppDatabase.android.kt`); inyéctalo por Koin.
7. `SelectedMunicipalityStore` es estado global mutable compartido entre Home y Map.
8. Bugs en `data/Mappers.kt`: biodiésel usa la media en vez del precio, se copia el precio a `averagePrice`, `GasStationEntity.toDomain()` devuelve una estación vacía, `IDEESS.toInt()` puede lanzar excepción.
9. Textos de UI escritos directamente en composables aunque existe `composeResources/values/strings.xml`; precios sin formatear/redondear.
10. Mezcla de nombres español/inglés (`Gasolinera*`, `Municipio*`, `Provincia` vs `GasStation*`). Para código nuevo usa inglés en tipos y funciones; el texto visible va en recursos.

## Convenciones

- Koin con DSL (`singleOf`, `factoryOf`, `viewModelOf`) en `core/di/AppModule.kt`.
- Navegación con rutas `@Serializable` en `core/navigation/Screens.kt`. Mantén igual el comportamiento de `NavigationBar` y `NavigationRail` (`popUpTo`, `launchSingleTop`, `restoreState`).
- Inyecta dispatchers para poder testear; evita `Dispatchers.IO/Default` fijos.
- No dejes código comentado ni imports sin usar.
- Código de plataforma: preferir interfaz en `commonMain` + implementación inyectada; usar `expect/actual` solo cuando sea lo más simple.

## Tests

- `commonTest` con `kotlin.test`, `kotlinx-coroutines-test`, Ktor `MockEngine`, `MapSettings`. Fakes escritos a mano. No hay Turbine ni mocking library.
- Sin tests: `GasolineraDetailViewModel`, `GasolineraFavoriteViewModel`, mappers, DAO y UI. Añade tests al tocar esas piezas (los mappers primero).
- Nombres de test sin comas ni caracteres que rompan Kotlin/Native (ver skill `kmp-testing-strategy`).

## Hoja de ruta (contexto para el agente)

1. ~~Migrar a AGP 9~~ (hecho en `chore/agp9-migration`): estructura `shared` + `androidApp` + `desktopApp`. Pendiente: verificar en dispositivo real un APK release con R8 y compilar iOS en macOS/CI.
2. **Ordenar capas** (sección anterior).
3. **Mapa en iOS** (hoy `Map.ios.kt` es un stub y `mapAvailable = false`): sin Mac no se puede compilar. Prepara el código y la configuración (`Info.plist`, textos de permisos de ubicación) y marca claramente que no está verificado. Considera CI en macOS.
4. **Publicación en Google Play y App Store**: firma, `versionCode`/`CFBundleVersion`, política de Play, ficha de datos, `Info.plist` (hoy lista `armv7`), `TEAM_ID` vacío en `iosApp/Configuration/Config.xcconfig`. Usa `kmp-store-release` y `play-policy-insights`.
5. **Ubicación del usuario** (FAB del mapa sin implementar): permisos en Android e iOS.
6. CI con GitHub Actions (no existe). Debe inyectar la clave de Maps como secreto.

## Skills instaladas (`.agents/skills/`)

Cárgalas según la tarea:

| Tarea | Skills |
|---|---|
| Arquitectura, capas, refactor | `kotlin-project-architecture-review`, `kotlin-kmp-refactor-safety`, `kotlin-project-state-management`, `kotlin-kmp-code-review`, `kotlin-platform-kmp-bridges`, `kmp-app-architecture` |
| Compose / UI | `compose-expert`, `compose-state-and-effects`, `edge-to-edge` |
| Corrutinas y Flow | `kotlin-concurrency-and-flow` |
| Red y APIs (MINETUR, precioil) | `kmp-ktor-networking`, `kmp-api-contracts` |
| Textos y localización | `kmp-strings-localization` |
| Tests | `kmp-testing-strategy` |
| AGP 9 / actualizaciones | `kotlin-tooling-agp9-migration`, `kmp-migration`, `kmp-compose-upgrade` |
| iOS y mapa iOS | `kmp-ios-build`, `mapkit` |
| Publicación y políticas | `kmp-store-release`, `play-policy-insights`, `android-permissions-security`, `r8-analyzer` |

Si dos skills se contradicen, prevalece este archivo. En particular, ignora cualquier recomendación de migrar a MVI o a Navigation 3.

## Pendiente tras la migración a AGP 9

- Regenerar `baselineprofile/src/main/baseline-prof.txt` (referencia el antiguo paquete `mygas.composeapp.generated.resources`).
- iOS no se ha compilado: `baseName` del framework sigue siendo `ComposeApp` a propósito para no tocar los Swift; solo cambió la tarea de Gradle y la ruta en `project.pbxproj`.
- `@Preview` de `App.kt` usa la anotación de JetBrains (deprecada) y falla sin Koin.
- Informe completo de la auditoría: `docs/AUDITORIA.md`.

## Reglas para el agente

- Cambios pequeños, un tema por commit. Mensajes en formato Conventional Commits.
- Antes de refactorizar, comprueba qué tests existen y ejecútalos; después, vuelve a ejecutarlos.
- No toques `shared/schemas/` a mano (lo genera Room). El esquema `GasStationDatabase/1.json` está obsoleto; confirma con el usuario antes de borrarlo.
- No subas `secrets.properties`, `local.properties`, keystores ni claves.
- No actualices versiones en bloque; sigue `kmp-compose-upgrade` y comprueba la compatibilidad.
