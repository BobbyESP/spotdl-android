import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
}

val splitApks = !project.hasProperty("noSplits")

val localProperties = Properties().apply {
    load(project.rootDir.resolve("local.properties").inputStream())
}

android {
    namespace = "com.bobbyesp.spotdl_android"

    compileSdk = 34
    defaultConfig {
        applicationId = "com.bobbyesp.spotdl_android"
        minSdk = 24
        targetSdk = 34
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
        if (!splitApks) ndk {
            (properties["ABI_FILTERS"] as String).split(';').forEach {
                abiFilters.add(it)
            }
        }
    }

    if (splitApks) splits {
        abi {
            isEnable = !project.hasProperty("noSplits")
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
    }

    buildTypes {
        all {
            buildConfigField(
                "String", "CLIENT_ID", "abcad8ba647d4b0ebae797a8f444ac9b"
            )
            buildConfigField(
                "String", "CLIENT_SECRET", "7ac6711e50044f1db20e4610f10f1f98"
            )

//            buildConfigField(
//                "String", "CLIENT_ID", "\"${localProperties.getProperty("CLIENT_ID")}\""
//            )
//            buildConfigField(
//                "String", "CLIENT_SECRET", "\"${localProperties.getProperty("CLIENT_SECRET")}\""
//            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            packaging {
                resources.excludes.add("META-INF/*.kotlin_module")
            }

            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
        debug {
            isMinifyEnabled = false
            packaging {
                resources.excludes.add("META-INF/*.kotlin_module")
            }

            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
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
        compose = true
        buildConfig = true
    }

    lint {
        disable.addAll(listOf("MissingTranslation", "ExtraTranslation"))
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "SpotDL-${defaultConfig.versionName}-${name}.apk"
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/*.kotlin_module"
        }
        jniLibs.useLegacyPackaging = true
    }
}

dependencies {

    //Core libs for the app
    implementation(libs.bundles.core)

    //Material UI, Accompanist...
    api(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist)
    implementation(libs.material)

    //Coil (For Jetpack Compose)
    implementation(libs.compose.coil)

    //Serialization
    implementation(libs.kotlin.serialization.json)

    //DI (Dependency Injection - Hilt)
    implementation(libs.bundles.hilt)
    ksp(libs.bundles.hilt.kapt)

    implementation(libs.mmkv)

    //Spotify downloader
    implementation(project(":library"))
    implementation(project(":ffmpeg"))

    //Compose testing libs
    implementation(libs.compose.tooling.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.test.manifest)

    //Memory leaks detection
    debugImplementation(libs.leakcanary)
}