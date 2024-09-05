import org.gradle.api.publish.maven.MavenPom

object MavenConfiguration {
    fun MavenPom.commonPomConfiguration() {
        name.set("spotdl-android")
        description.set("spotdl for Android")
        url.set("https://github.com/BobbyESP/spotdl-android")
        inceptionYear.set("2022")
        licenses {
            license {
                name.set("MIT license")
                url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
            }
        }
        developers {
            developer {
                id.set("BobbyESP")
                name.set("Gabriel Fontán")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/BobbyESP/spotdl-android")
            url.set("https://github.com/BobbyESP/spotdl-android")
        }
    }
}