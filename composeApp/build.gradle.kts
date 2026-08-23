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
    alias(libs.plugins.baselineProfile)
}

val secretsProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use(::load)
    }
}

val androidMapsApiKey = providers.gradleProperty("MAPS_API_KEY_ANDROID").orNull
    ?: providers.environmentVariable("MAPS_API_KEY_ANDROID").orNull
    ?: providers.gradleProperty("MAPS_API_KEY").orNull
    ?: providers.environmentVariable("MAPS_API_KEY").orNull
    ?: secretsProperties.getProperty("MAPS_API_KEY_ANDROID")
    ?: secretsProperties.getProperty("MAPS_API_KEY")
    ?: error("MAPS_API_KEY_ANDROID no encontrada")

val desktopMapsApiKey = providers.gradleProperty("MAPS_API_KEY_DESKTOP").orNull
    ?: providers.environmentVariable("MAPS_API_KEY_DESKTOP").orNull
    ?: providers.gradleProperty("MAPS_API_KEY").orNull
    ?: providers.environmentVariable("MAPS_API_KEY").orNull
    ?: secretsProperties.getProperty("MAPS_API_KEY_DESKTOP")
    ?: secretsProperties.getProperty("MAPS_API_KEY")
    ?: error("MAPS_API_KEY_DESKTOP no encontrada")

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
            implementation(libs.androidx.profileinstaller)

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

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)

            // Icons
            implementation(libs.material.icons.extended)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.multiplatform.settings.no.arg)

            // Room
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.multiplatform.settings.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            // KCEF
            implementation("dev.datlag:kcef:2024.04.20.4")

            // Ktor
            implementation(libs.ktor.client.cio)
            implementation("com.swmansion.kmpmaps:core:0.9.1")
        }
    }
}

composeCompiler {
    metricsDestination.set(layout.buildDirectory.dir("compose_compiler"))
    reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
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
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // Le decimos que use nuestro archivo de reglas
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

baselineProfile {
    automaticGenerationDuringBuild = false
}

buildkonfig {
    packageName = "org.pablo.mygas"

    defaultConfigs {
        buildConfigField(STRING, "MAPS_API_KEY", "")
    }

    targetConfigs {
        create("desktop") {
            buildConfigField(STRING, "MAPS_API_KEY", desktopMapsApiKey)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    baselineProfile(project(":baselineprofile"))
    setOf(
        "kspCommonMainMetadata",
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
