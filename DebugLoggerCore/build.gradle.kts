import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.ivangarza07",
        artifactId = "debuglogger",
        version = "1.0.0"
    )

    pom {
        name.set("DebugLogger")
        description.set("Android debug logging library")
        url.set("https://github.com/IvanGarza07/Debug-Logger-Library")
        inceptionYear.set("2025")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }

        developers {
            developer {
                id.set("IvanGarza07")
                name.set("Ivan Garza")
                email.set("ivan.darkness.07@gmail.com")
                url.set("https://github.com/IvanGarza07/")
            }
        }

        scm {
            url.set("https://github.com/IvanGarza07/Debug-Logger-Library")
            connection.set("scm:git:git://github.com/IvanGarza07/Debug-Logger-Library.git")
            developerConnection.set("scm:git:ssh://git@github.com/IvanGarza07/Debug-Logger-Library.git")
        }
    }
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    /*******************************************************
    GOOGLE
     ******************************************************/
    implementation(libs.google.material)

    /*******************************************************
    ROOM
     ******************************************************/
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    /*******************************************************
    KOTLIN
     ******************************************************/
    implementation(libs.kotlin.coroutines.android)

    /*******************************************************
    TEST
     ******************************************************/
    testImplementation(libs.androidx.arch.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)

    /*******************************************************
    ANDROID TEST
     ******************************************************/
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
