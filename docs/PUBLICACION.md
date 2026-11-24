# Guía de publicación

Estado de lo que ya está preparado en el repositorio y de lo que solo puedes hacer tú (cuentas, claves, formularios).

## Android (Google Play)

### Ya hecho en el código
- Firma de release leyendo secretos (`androidApp/build.gradle.kts`). Sin secretos, `assembleRelease` genera un APK sin firmar.
- `versionCode` y `versionName` configurables con `APP_VERSION_CODE` y `APP_VERSION_NAME`.
- R8 y reducción de recursos activos en release.
- Reglas de copia de seguridad explícitas (`res/xml/data_extraction_rules.xml`, `backup_rules.xml`): se respaldan favoritos y preferencias.
- `targetSdk` 36, `compileSdk` 37.
- Consultas de visibilidad de paquetes para abrir mapas.
- Si falta `MAPS_API_KEY`, `assembleRelease` y `bundleRelease` fallan con un mensaje claro.

### Tienes que hacer tú
1. **Crear el keystore de subida** y guardarlo fuera del repositorio, con copia de seguridad:
   ```bash
   keytool -genkeypair -v -keystore upload.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
   ```
2. **Activar Play App Signing** al crear la app en Play Console.
3. **Secretos** (en `secrets.properties`, variables de entorno o secretos de GitHub): `ANDROID_KEYSTORE_PATH`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`, `MAPS_API_KEY`.
4. **Restringir la clave de Google Maps** en Google Cloud: aplicaciones Android, paquete `org.pablo.mygas`, y los SHA-1 de depuración, de subida **y de firma de Play App Signing**. Limitar a "Maps SDK for Android" y poner alertas de cuota. Sin el SHA-1 de Play el mapa sale gris en producción.
5. **Generar el AAB**: `./gradlew :androidApp:bundleRelease` (con los secretos anteriores).
6. **Probar un release real en un dispositivo** antes de subir: las reglas de R8 conservan paquetes enteros (`core.navigation`, `data.remote.responses`) y nunca se han ejecutado en un dispositivo. Recorre Inicio, detalle, favoritos, mapa y ajustes.
7. **Ficha de la tienda**: icono 512 px, gráfico de 1024x500, capturas, descripción en español e inglés.
8. **Formularios de Play Console**: política de privacidad (URL), seguridad de los datos, clasificación de contenido, público objetivo y declaración de anuncios (no hay).
9. Las cuentas personales nuevas necesitan una fase de pruebas cerradas con testers antes de producción: comprueba el requisito vigente en Play Console.

### Seguridad de los datos (respuestas sugeridas)
- No se recopilan datos personales ni se comparten con terceros desde la app.
- No hay cuentas, anuncios, compras ni analítica.
- Favoritos y preferencias se guardan solo en el dispositivo.
- Google Maps SDK puede tratar datos del dispositivo según la política de Google: revisa su guía de declaración.
- No se pide ni se usa la ubicación del usuario (aún no existe esa función). Si se añade, hay que actualizar este formulario.

## iOS (App Store)

### Ya hecho en el código (sin compilar: no hay macOS en este equipo)
- `PrivacyInfo.xcprivacy` con UserDefaults, FileTimestamp, SystemBootTime y DiskSpace, sin rastreo y sin datos recopilados. **Revísalo con el informe de privacidad de Xcode** (Product > Archive > Generate Privacy Report) porque dependencias de Kotlin/Native pueden requerir más categorías.
- `UIRequiredDeviceCapabilities` en `arm64`, `ITSAppUsesNonExemptEncryption` en `false`.
- Versión y build tomadas de `MARKETING_VERSION` y `CURRENT_PROJECT_VERSION` en `iosApp/Configuration/Config.xcconfig`.
- Fondo de lanzamiento claro/oscuro (`LaunchBackground`) para evitar el parpadeo antes de Compose.
- El identificador de bundle ya no se concatena con el `TEAM_ID`.

### Tienes que hacer tú
1. **Decidir el bundle ID definitivo.** Ahora es `org.pablo.mygas` (igual que Android). No se puede cambiar una vez creada la app en App Store Connect.
2. **Rellenar `TEAM_ID`** en `iosApp/Configuration/Config.xcconfig`.
3. **Certificados y perfiles de distribución**, o una clave de API de App Store Connect si firmas desde CI.
4. **Compilar en macOS** al menos una vez: el mapa de MapKit y el host de Xcode nunca se han compilado. El workflow `ci-ios.yml` lo hace en cada cambio.
5. **Ficha**: capturas de iPhone 6,9" e iPad 13" (o limitar a iPhone con `TARGETED_DEVICE_FAMILY = 1`), descripción, categoría, clasificación por edad, URL de soporte y de privacidad.
6. **Etiquetas de privacidad de la app**: "Datos no recopilados" mientras no exista ubicación.
7. Subir a TestFlight interno antes de enviar a revisión.

## Desktop
- Se distribuye con `./gradlew :desktopApp:packageMsi`, `packageDmg` o `packageDeb` (cada uno solo en su sistema operativo). Los workflows de CI generan los instaladores.
- El mapa usa teselas públicas de OpenStreetMap: pon un contacto o URL en el `User-Agent` de `TileRepository.kt` antes de distribuirlo y respeta su política de uso. Para mucho tráfico, usa un proveedor de teselas propio.
- Los instaladores no están firmados ni notarizados.

## Datos y riesgos de terceros
- **MINETUR** (precios oficiales): cita la fuente en la ficha. Reutilización de información pública.
- **api.precioil.es**: servicio no oficial, solo para el detalle de una estación. Puede cambiar o cerrar sin aviso. Plan B: mostrar solo los datos del Ministerio.

## Pendiente que no se puede hacer sin dispositivo
- Regenerar `baselineprofile/src/main/baseline-prof.txt` (necesita un emulador o dispositivo: `./gradlew :androidApp:generateReleaseBaselineProfile`).
- Probar el release con R8 y acotar sus reglas de conservación.
