plugins {
    alias(libs.plugins.android.application)
}

// read the api key from .env, the app still builds without it
val apiKey = rootProject.file(".env").takeIf { it.exists() }?.readLines()
    ?.firstOrNull { it.startsWith("Exchange_Rate_API_KEY=") }
    ?.substringAfter("=")?.trim() ?: ""

android {
    namespace = "com.example.exchangerate"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.exchangerate"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
}
