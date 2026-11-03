plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.lab06.lesson4"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.lab06.lesson4"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
}
