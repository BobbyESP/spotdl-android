sealed class Version(
    open val versionMajor: Int,
    val versionMinor: Int,
    val versionPatch: Int,
    val versionBuild: Int = 0,
    val commitId: String = ""
) {
    abstract fun toVersionName(): String
    class Stable(versionMajor: Int, versionMinor: Int, versionPatch: Int) :
        Version(versionMajor, versionMinor, versionPatch) {
        override fun toVersionName(): String = "${versionMajor}.${versionMinor}.${versionPatch}"
    }

    class ReleaseCandidate(
        versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int
    ) : Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override fun toVersionName(): String =
            "${versionMajor}.${versionMinor}.${versionPatch}-rc.$versionBuild"
    }

    class Beta(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int) :
        Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override fun toVersionName(): String =
            "${versionMajor}.${versionMinor}.${versionPatch}-beta.$versionBuild"
    }

    class Alpha(
        versionMajor: Int, versionMinor: Int, versionPatch: Int, commitId: String
    ) : Version(versionMajor, versionMinor, versionPatch, commitId = commitId) {
        override fun toVersionName(): String =
            "${versionMajor}.${versionMinor}.${versionPatch}-alpha.$commitId"
    }
}

val commitSignature = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
}.standardOutput.asText.get().substringBefore("\n")

val currentVersion: Version = Version.Alpha(
    versionMajor = 0,
    versionMinor = 3,
    versionPatch = 0,
    commitId = commitSignature
)

val versionName by extra(currentVersion.toVersionName())
val versionCode by extra(
    currentVersion.run {
        versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionBuild
    }
)

buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

allprojects {
    group = "com.github.BobbyESP"
    version = versionCode
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}