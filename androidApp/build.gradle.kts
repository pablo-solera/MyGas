import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.baselineProfile)
}

val secretsProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use(::load)
    }
}

val mapsApiKey: String = providers.gradleProperty("MAPS_API_KEY").orNull
    ?: providers.environmentVariable("MAPS_API_KEY").orNull
    ?: secretsProperties.getProperty("MAPS_API_KEY")
    ?: ""

fun secret(name: String): String? =
    providers.gradleProperty(name).orNull
        ?: providers.environmentVariable(name).orNull
        ?: secretsProperties.getProperty(name)

val keystorePath = secret("ANDROID_KEYSTORE_PATH")
val keystorePassword = secret("ANDROID_KEYSTORE_PASSWORD")
val releaseKeyAlias = secret("ANDROID_KEY_ALIAS")
val releaseKeyPassword = secret("ANDROID_KEY_PASSWORD")
val canSignRelease = listOf(keystorePath, keystorePassword, releaseKeyAlias, releaseKeyPassword).all { !it.isNullOrBlank() }

gradle.taskGraph.whenReady {
    val releaseRequested = allTasks.any {
        it.project == project && (it.name.startsWith("bundle") || it.name.startsWith("assemble")) &&
            it.name.endsWith("Release")
    }
    if (releaseRequested && mapsApiKey.isBlank()) {
        throw GradleException(
            "MAPS_API_KEY is required for release builds. " +
                "Set it in secrets.properties, an environment variable or a Gradle property."
        )
    }
}

android {
    namespace = "org.pablo.mygas"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.pablo.mygas"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = secret("APP_VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = secret("APP_VERSION_NAME") ?: "1.0"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }
    packaging {
        resources {
            excludes.add("/META-INF/AL2.0")
            excludes.add("/META-INF/LGPL2.1")
        }
    }
    signingConfigs {
        if (canSignRelease) {
            create("release") {
                storeFile = rootProject.file(keystorePath!!)
                storePassword = keystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }
    buildTypes {
        getByName("release") {
            if (canSignRelease) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.koin.android)
    baselineProfile(projects.baselineprofile)
}
