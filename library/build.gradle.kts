@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

val versionName = rootProject.extra["versionName"] as String

android {
    namespace = "com.bobbyesp.library"
    compileSdk = 34

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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.core.ktx)
    implementation(libs.bundles.coroutines)
    implementation(project(":common"))

    implementation(libs.okhttp)

    implementation(libs.commons.io)

    //Serialization
    implementation(libs.kotlin.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

afterEvaluate{
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "com.github.bobbyesp"
                artifactId = "library"
                version = versionName
            }
        }
    }
}