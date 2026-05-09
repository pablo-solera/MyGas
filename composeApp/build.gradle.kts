import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.buildkonfig)
}

val secretsProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use(::load)
    }
}

fun mapApiKeyFor(platform: String): String {
    val platformKey = "MAPS_API_KEY_${platform.uppercase()}"
    return providers.gradleProperty(platformKey).orNull
        ?: providers.environmentVariable(platformKey).orNull
        ?: secretsProperties.getProperty(platformKey)
        ?: providers.gradleProperty("MAPS_API_KEY").orNull
        ?: providers.environmentVariable("MAPS_API_KEY").orNull
        ?: secretsProperties.getProperty("MAPS_API_KEY")
        ?: error("$platformKey no encontrada. Añádela a secrets.properties, Gradle properties o variable de entorno.")
}

val androidMapsApiKey = mapApiKeyFor("android")
val desktopMapsApiKey = mapApiKeyFor("desktop")
val iosMapsApiKey = mapApiKeyFor("ios")

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Google Maps
            implementation("com.google.maps.android:maps-compose:8.3.0")
            implementation("com.google.maps.android:maps-compose-utils:8.3.0")
            //implementation("com.google.android.gms:play-services-maps:18.2.0")

            // Ktor
            implementation(libs.ktor.client.okhttp)

            // Koin
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)

            // kmp-maps
            implementation("com.swmansion.kmpmaps:core:0.9.1")

            // Icons
            implementation(libs.material.icons.extended)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Room
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            // KCEF
            implementation("dev.datlag:kcef:2024.04.20.4")

            // Ktor
            implementation(libs.ktor.client.okhttp)
        }
    }
}

android {
    namespace = "org.pablo.mygas"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.pablo.mygas"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["MAPS_API_KEY"] = androidMapsApiKey
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // Le decimos que use nuestro archivo de reglas
            )
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}

buildkonfig {
    packageName = "org.pablo.mygas"

    defaultConfigs {
        buildConfigField(STRING, "MAPS_API_KEY", "")
    }

    targetConfigs {
        create("android") {
            buildConfigField(STRING, "MAPS_API_KEY", androidMapsApiKey)
        }
        create("desktop") {
            buildConfigField(STRING, "MAPS_API_KEY", desktopMapsApiKey)
        }
        create("iosX64") {
            buildConfigField(STRING, "MAPS_API_KEY", iosMapsApiKey)
        }
        create("iosArm64") {
            buildConfigField(STRING, "MAPS_API_KEY", iosMapsApiKey)
        }
        create("iosSimulatorArm64") {
            buildConfigField(STRING, "MAPS_API_KEY", iosMapsApiKey)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    setOf(
        "kspAndroid", "kspDesktop", "kspIosX64", "kspIosArm64", "kspIosSimulatorArm64"
    ).forEach {
        add(it, libs.room.compiler)
    }
}
room {
    schemaDirectory("$projectDir/schemas")
}
compose.desktop {
    application {
        mainClass = "org.pablo.mygas.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.pablo.mygas"
            packageVersion = "1.0.0"
        }
    }
}
