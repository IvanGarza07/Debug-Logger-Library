import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.igarza.debuglogger"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    /*******************************************************
    ANDROID
     ******************************************************/
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    /*******************************************************
    GOOGLE
     ******************************************************/
    implementation(libs.google.material)
    implementation(libs.google.hilt.android)

    /*******************************************************
    KOTLIN
     ******************************************************/
    implementation(libs.kotlin.coroutines.android)

    /*******************************************************
    THIRD PART LIBS
     ******************************************************/
    implementation(libs.timber)

    /*******************************************************
    KAPT
     ******************************************************/
    ksp(libs.androidx.room.compiler)
    ksp(libs.google.hilt.compiler)
    ksp(libs.google.hilt.android.compiler)

    /*******************************************************
    TEST
     ******************************************************/
    testImplementation(libs.google.hilt.android.testing)
    testImplementation(libs.junit)

    /*******************************************************
    ANDROID TEST
     ******************************************************/
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /*******************************************************
    KSP TEST
     ******************************************************/
    kspTest(libs.google.hilt.android.testing)
    kspAndroidTest(libs.google.hilt.android.testing)
}