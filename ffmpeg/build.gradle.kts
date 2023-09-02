plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.bobbyesp.ffmpeg"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

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
//    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation(libs.core.ktx)
    implementation(project(":library"))
    implementation(project(":commonUtilities"))
    implementation(libs.commons.io)
}

//afterEvaluate{
//    publishing {
//        publications {
//            maven(MavenPublication) {
//                from components.release
//
//                groupId 'com.github.bobbyesp'
//                artifactId 'spotdl-android-ffmpeg'
//                version '0.1.5'
//            }
//        }
//    }
//}