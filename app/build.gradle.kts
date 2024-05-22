plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bintianqi.nip"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bintianqi.nip"
        minSdk = 23
        targetSdk = 34
        versionName = "1.0"
        versionCode = 1
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions"
        )
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    compileOnly(libs.xposed.api)
    implementation(libs.yukihook.api)
    ksp(libs.yukihook.ksp.xposed)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
}