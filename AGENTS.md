# AGENTS.md — MyGas

App Compose Multiplatform (Android, iOS, Desktop JVM) para consultar precios de carburante en España por provincia y municipio, con favoritos y mapa. Package base: `org.pablo.mygas`. Idioma de la UI y de la conversación: español.

## Entorno

- Desarrollo en **Windows**: no se puede compilar ni probar iOS localmente. Todo lo de iOS se valida solo por revisión de código o en CI con runner macOS. No afirmes que algo funciona en iOS sin haberlo compilado.
- Cuando cambies código de `commonMain`, comprueba que no usa APIs de JVM/Android (debe compilar también en Kotlin/Native).

## Estructura

| Módulo | Contenido |
|---|---|
| `:composeApp` | KMP + app Android (`com.android.application`). Targets: `androidTarget`, `iosX64/Arm64/SimulatorArm64` (framework estático `ComposeApp`), `jvm("desktop")` |
| `:baselineprofile` | Generador de baseline profile (UiAutomator). `baseline-prof.txt` está versionado |
| `iosApp/` | Proyecto Xcode (SwiftUI mínimo que envuelve `MainViewController`). Integración por framework directo, sin CocoaPods ni SPM |

Source sets en `composeApp/src`: `commonMain`, `androidMain`, `iosMain`, `desktopMain`, `commonTest`.

Paquetes en `org.pablo.mygas`: `core` (di, navigation, state, utils), `data` (local, remote), `domain` (models, repositories, usecases), `ui` (components, screens, state, theme, viewmodels).

`expect/actual` actuales: `mapAvailable`, `mapNavigationAvailable`, `MapOpener`, `provideDatabaseBuilder()`, `GasStationDatabaseConstructor`, composable `Map(...)`.

## Comandos

```bash
./gradlew :composeApp:assembleDebug            # Android debug
./gradlew :composeApp:run                      # Desktop (pendiente verificar nombre de tarea)
./gradlew :composeApp:allTests                 # tests de todos los targets disponibles
./gradlew :composeApp:testDebugUnitTest        # tests en JVM/Android (los que se pueden ejecutar en Windows)
./gradlew :composeApp:desktopTest              # solo si existe el source set
./gradlew :composeApp:assembleRelease          # release con R8 (minify + shrink activos)
```

En Windows usa `gradlew.bat` si `./gradlew` falla. No ejecutes tareas `ios*` ni `link*Ios*` aquí.

## Clave de Google Maps (obligatoria para compilar)

El build **falla a propósito** si no hay clave. Se busca en este orden: propiedad Gradle, variable de entorno, `secrets.properties` (raíz, ignorado por git):

```properties
MAPS_API_KEY_ANDROID=...
MAPS_API_KEY_DESKTOP=...
# o una sola: MAPS_API_KEY=...
```

Nunca escribas claves reales en el repositorio, en logs ni en commits. Para tareas que no necesitan el mapa, usa una clave ficticia (`MAPS_API_KEY=dummy`) vía variable de entorno.

## Versiones (estado actual)

Kotlin 2.2.20 · Compose Multiplatform 1.9.0 · AGP 8.9.3 · Gradle 8.11.1 · KSP 2.2.20-2.0.4 · Koin 4.1.1 · Ktor 3.3.0 · Room 2.8.1 (BundledSQLiteDriver) · navigation-compose (JetBrains) 2.9.0 · kotlinx.serialization 1.9.0 · compileSdk/targetSdk 36 · minSdk 24.

Las dependencias van en `gradle/libs.versions.toml`. No añadas versiones directamente en `build.gradle.kts` (hay algunas heredadas: maps-compose, kcef, kmpmaps, que conviene migrar al catálogo).

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

1. **Migrar a AGP 9**: separar la app Android de `composeApp` (`shared` + `androidApp` + `desktopApp`), plugin `com.android.kotlin.multiplatform.library`, subir Gradle y Compose Multiplatform. Usa `kotlin-tooling-agp9-migration`, `kmp-migration` y `kmp-compose-upgrade`. Una fase por commit; verificar Android y Desktop tras cada una.
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

## Reglas para el agente

- Cambios pequeños, un tema por commit. Mensajes en formato Conventional Commits.
- Antes de refactorizar, comprueba qué tests existen y ejecútalos; después, vuelve a ejecutarlos.
- No toques `composeApp/schemas/` a mano (lo genera Room). El esquema `GasStationDatabase/1.json` está obsoleto; confirma con el usuario antes de borrarlo.
- No subas `secrets.properties`, `local.properties`, keystores ni claves.
- No actualices versiones en bloque; sigue `kmp-compose-upgrade` y comprueba la compatibilidad.
