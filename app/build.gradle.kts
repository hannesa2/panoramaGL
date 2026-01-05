import info.git.versionHelper.getGitCommitCount
import info.git.versionHelper.getLatestGitHash
import info.git.versionHelper.getVersionText

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.panoramagl.sample"
    defaultConfig {
        applicationId = "com.panoramagl.sample"
        minSdk = 21
        compileSdk = 36
        targetSdkVersion(36)
        versionCode = getGitCommitCount()
        versionName = "${getVersionText()}.$versionCode-${getLatestGitHash()}"

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments.putAll(
            mapOf(
                "useTestStorageService" to "true",
            ),
        )
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation(project(":library"))
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.2.20")
    implementation("com.google.android.material:material:1.13.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.3.0")
    androidTestUtil("androidx.test.services:test-services:1.6.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test:rules:1.7.0") // GrantPermissionRule
}
