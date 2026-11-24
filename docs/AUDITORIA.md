# Auditoría de MyGas (noviembre 2026)

Resultado de ejecutar sobre el código las skills instaladas en `.agents/skills/`. Los hallazgos salen de leer el código; solo se ha compilado Android y Desktop. **Nada de iOS está verificado** (no hay Mac).

Rutas relativas a `shared/src/commonMain/kotlin/org/pablo/mygas/` salvo indicación.

## 1. Navegador de Desktop (por qué era tan complejo)

Antes, la pestaña Mapa de Desktop incrustaba un Chromium completo:

- `dev.datlag:kcef` descargaba cientos de MB en el primer uso a `~/.mygas/kcef-bundle`.
- `kmpmaps` (WebView) pintaba Google Maps JS dentro de ese Chromium.
- La clave de Maps entraba por Gradle, `BuildKonfig` y una inicialización tardía.
- Tres capas (kmpmaps, compose-webview, KCEF) con estado global, y cierre manual con `runBlocking` en el hilo de UI.

Fallos que tenía: tras un error de init el mapa se componía roto, salir de la pestaña durante la descarga lo dejaba bloqueado para siempre, no había reintento, había una carrera entre la clave y la carga del WebView, y la clave quedaba en texto plano dentro del instalador.

**Decisión aplicada:** se eliminaron KCEF, kmpmaps, BuildKonfig y la clave de Desktop, y se sustituyó por un mapa de teselas OpenStreetMap dibujado con Compose puro (`TileMap.kt`), sin clave ni descarga de Chromium. MapLibre en Desktop está en Alpha y exige Java 25, por eso se descartó.

## 2. Errores confirmados (prioridad alta)

> **Estado:** corregidos M1-M8, N1-N4, U1-U6, C1-C3 en `feat(map)`; con tests en `MappersTest`, `MunicipioServiceTest`, `PrecioilGasStationAdapterTest`, `HomeViewModelTest`, `SettingsViewModelTest` y `KoinGraphTest`. C3 (iOS) sin verificar.

| Id | Dónde | Problema |
|---|---|---|
| M1 | `data/Mappers.kt:43-46` | Biodiésel usa la media como precio |
| M2 | `data/Mappers.kt:75-102` | `averagePrice = price` y combustibles sin dato como `0.0` en vez de `null` |
| M3 | `data/Mappers.kt:61` | `IDEESS.toInt()` lanza excepción si no es numérico y tumba toda la lista |
| M4 | `data/Mappers.kt:108-154` | `GasStationEntity.toDomain()` devuelve una estación vacía |
| M5 | `HomeViewModel.kt:132-134` | `sortedBy` con `Double?` pone las estaciones sin precio arriba |
| M6 | `HomeViewModel.kt:142,170` | Si falla la primera carga, "Reintentar" no hace nada (`municipioId` no se guarda) |
| M7 | `HomeViewModel.kt:120-127` | Al cambiar de municipio se ven las estaciones del anterior |
| M8 | `MapViewModel.kt:29` | Al limpiar la selección el mapa conserva los marcadores viejos |
| N1 | `AppModule.kt:61-63` | Solo hay `requestTimeout` de 5 s. En Android OkHttp mantiene 10 s de socket aunque se suba el otro |
| N2 | `MunicipioService.kt:58-67` | `UnresolvedAddressException` es de JVM; `NO_INTERNET` casi nunca se produce |
| N3 | `PrecioilGasStationAdapter.kt:42` | `error(...)` pierde el `NetworkError` tipado: la UI siempre ve `UNKNOWN` |
| N4 | DTOs de `data/remote` | Casi todos los campos son obligatorios: un campo ausente tumba la lista entera |
| U1 | `androidApp/.../MapOpener.android.kt` | Con targetSdk 36 falta `<queries>` y `resolveActivity` devuelve `null`: el botón de abrir mapa puede no hacer nada |
| U2 | `ui/screens/SettingsScreen.kt` | El teclado tapa el campo y el botón Guardar (falta `adjustResize` e `imePadding`) |
| U3 | `core/navigation/Navigation.kt:89-92` | Insets duplicados por `Scaffold` anidados (hueco doble arriba) |
| U4 | `Navigation.kt:125` | El `NavigationRail` navega sin `popUpTo/launchSingleTop/restoreState` |
| U5 | `Navigation.kt:45` | El detalle abierto desde Favoritos marca Inicio como pestaña activa |
| U6 | Varias pantallas | Precios con `Double.toString()` (`1.5190000000000001`, `71.39999999999999 €`) |
| C1 | `GasolineraDetailViewModel.kt:53-66` | Cada llamada a `checkFavorite` lanza un colector nuevo sin cancelar el anterior |
| C2 | `UserPreferencesRepositoryImpl.kt:19,25` | Actualización no atómica; usar `_preferences.update { }` |
| C3 | `iosMain/.../MainViewController.kt` | `initKoin()` en cada creación del controlador puede lanzar `KoinApplicationAlreadyStartedException` (sin verificar) |

## 3. Capas y DI (deuda de arquitectura)

- Domain depende de data: los 4 casos de uso devuelven DTOs (`GasolinerasResponse`, `PrecioilGasStationResponse`, `MunicipioItem`) y uno depende de una clase concreta.
- Los ViewModels de Detalle y Favoritos inyectan adapters concretos, no interfaces ni casos de uso.
- `GasStationRepository` y `LocationRepository` (domain) no tienen implementación ni consumidores.
- `GasolinerasRepository` es en realidad un data source; `MunicipioService` mezcla dos APIs (MINETUR y precioil) con URLs fijas.
- Koin registra clases concretas, no interfaces; Android usa `GlobalContext.get()` para el `Context`; `KoinGraphTest` solo verifica un binding.
- Estado: flags `isLoading` en paralelo a `Loadable`, estado global `SelectedMunicipalityStore`, `Dispatchers.IO` fijo en `SettingsViewModel`, scope del adapter sin dueño.
- Tipos de UI dentro de `domain/models/map`; textos de UI en `FuelTypeLabels` (domain).

### Layout objetivo

```
domain/  models/ · repositories/ (interfaces) · usecases/ (devuelven Result<Modelo, NetworkError>)
data/    remote/ (servicios + DTOs + mappers) · local/ (Room) · repositories/ (implementaciones)
ui/      viewmodels/ (solo casos de uso o interfaces de domain) · state/ · screens/ · components/
```

### Plan por fases (un commit por paso, tests antes y después)

1. **P0, bugs sin cambiar arquitectura:** M1-M8, C2, U1, U4, ambos timeouts, y `MappersTest` primero.
2. **P1, ordenar capas:** modelos de dominio y mappers en `data`; casos de uso sin DTOs; VMs a casos de uso; enlazar interfaces en Koin; `MapLauncher` inyectado; `KoinGraphTest` con `verify`.
3. **P2, estado y pulido:** una sola fuente `Loadable`, sustituir `SelectedMunicipalityStore`, mover tipos de mapa a `ui`, limpiar código muerto.

## 4. UI y diseño

> **Estado:** hechos textos a recursos, mensajes de error legibles, formato de precios, paleta completa clara/oscura, tema nativo Android día/noche, barras transparentes, `Card(onClick)`, encabezados semánticos, estados de carga e inicial en Inicio y marcadores accesibles en Desktop. Pendiente: color dinámico, selector de tema, `WindowSizeClass` con lista+detalle, previews, pull-to-refresh y traducciones.

- **Textos:** casi todo escrito a mano aunque `strings.xml` ya tiene la mayoría de claves; falta `values-en/`. `ErrorState` enseña `unknown` al usuario.
- **Precios:** una función común de formato (3 decimales por litro, 2 por total, coma decimal por locale) con tests.
- **Insets y teclado:** `consumeWindowInsets`, `adjustResize`, `imePadding`, `isNavigationBarContrastEnforced = false`.
- **Tema:** `ColorScheme` incompleto (los roles sin definir caen a la paleta púrpura por defecto de M3), tema Android claro fijo con flash blanco en modo oscuro, `MyGasSpacing` sin usar, formas y dimensiones sueltas. Valorar color dinámico y selector Claro/Oscuro/Sistema.
- **Layout adaptable:** `600.dp` fijo; usar `WindowSizeClass` y lista+detalle en pantallas anchas.
- **Accesibilidad:** `contentDescription` duplicado (TalkBack lee dos veces), títulos sin `heading()`, tarjeta con `Column(clickable)` en vez de `Card(onClick)`.
- **Home:** sin estado inicial ni de carga de municipios; cabecera y filtros no desplazables en apaisado.
- **Previews:** las dos existentes llaman a `App()`, que necesita Koin. Separar contenido y wiring y añadir previews de componentes.
- **Ideas P2:** ordenar por el combustible favorito, etiqueta "más barata", pull-to-refresh, deshacer al quitar favorito, atajos en Desktop.

## 5. Datos, strings y tests

- Room: `GasStationDatabase/1.json` está obsoleto; `VehicleEntity` y `RefuelEntity` no están registradas (si se registran, versión 2 con migración, `vehicles` antes que `refuels`). Decisión pendiente del usuario sobre el esquema antiguo.
- Hay que decidir si existió alguna versión publicada con el esquema antiguo: sin migración la app se cerraría al abrir esa base.
- Tests a escribir, por orden: `MappersTest`; decodificación con JSON real vía `MockEngine`; mapeo de códigos HTTP y excepciones de transporte; adapter (TTL, errores, concurrencia); `KoinGraphTest` real; VMs de Detalle y Favoritos; DAO; paridad de claves entre `values/` y `values-en/`.
- Tests existentes con defectos: `PrecioilGasStationAdapterTest` es circular y usa tiempo real; `SettingsViewModelTest` escribe en `Dispatchers.IO` sin esperar.

## 6. Publicación en las tiendas

### Bloqueantes
- **Android:** no hay `signingConfigs`; el release sale sin firmar. Falta keystore de subida y Play App Signing.
- **Clave de Maps:** restringir por paquete y SHA-1 (debug, subida y firma de Play) y por API; añadir alertas de cuota. Sin el SHA-1 de Play el mapa sale gris en producción.
- **`allowBackup="true"`** sin reglas de extracción: decidir si hay backup.
- **R8:** las reglas `-keep ...navigation.**` y `-keep ...responses.**` son demasiado anchas; probar un release real en dispositivo antes de acotarlas.
- **Política de privacidad pública:** obligatoria en ambas tiendas; no existe.
- **iOS:** `TEAM_ID` vacío y concatenado al bundle ID en `project.pbxproj`; firma de distribución; falta `PrivacyInfo.xcprivacy`; `UIRequiredDeviceCapabilities = armv7` obsoleto; versiones escritas a mano en `Info.plist`; falta `ITSAppUsesNonExemptEncryption`. Escribir el `project.pbxproj` a mano desde Windows es frágil: valorar XcodeGen.
- **Dependencia externa:** `api.precioil.es` no es oficial; plan B con solo MINETUR y cita de la fuente de datos.

### Mapa en iOS (sin Mac)
Enfoque: `MKMapView` dentro de `UIKitView` en `iosMain`. MapKit no necesita clave. Hay que sincronizar marcadores, delegar selección, y declarar `NSLocationWhenInUseUsageDescription` y la privacidad. **Todo se escribe a ciegas:** la única forma de verificarlo es CI en un runner macOS con `compileKotlinIosSimulatorArm64` y `xcodebuild`.

### CI recomendada
- `ubuntu-latest`: tests, lint y `assembleDebug` (sin clave).
- `macos-latest`: compilación de iOS y `xcodebuild` sin firma, solo cuando cambie `shared/` o `iosApp/`.
- Releases manuales, con secretos para keystore y claves de App Store Connect.

## 7. Orden recomendado

1. Bugs P0 con tests (mappers, reintento, timeouts, `<queries>`, insets, formato de precios).
2. Firma y versionado de Android, restricciones de la clave, política de privacidad, pista interna de Play.
3. CI de Android y Desktop; después job de iOS en macOS.
4. Ordenar capas por fases.
5. Host de iOS (bundle ID, `PrivacyInfo`, `Info.plist`) y mapa de iOS verificado solo por CI.
6. Textos a recursos e inglés, y mejoras de UI.
