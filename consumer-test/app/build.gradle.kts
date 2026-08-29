import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application") version "9.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10"
}

android {
    namespace = "com.linrjk.liquid.consumer"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.linrjk.liquid.consumer"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation("com.github.zhaopuchu:LiquidGlass-Fork-IOS27:v1.3.0")
}
