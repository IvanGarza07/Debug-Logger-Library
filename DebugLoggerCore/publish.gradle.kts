import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.SigningExtension

afterEvaluate {

    extensions.configure<PublishingExtension>("publishing") {
        publications {
            create<MavenPublication>("release") {

                groupId = "io.github.ivangarza07"
                artifactId = "debuglogger"
                version = "1.0.0"

                from(components["release"])

                pom {
                    name.set("DebugLogger")
                    description.set("Android debug logging library")
                    inceptionYear.set("2025")
                    url.set("https://github.com/IvanGarza07/Debug-Logger-Library")

                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
        }

        repositories {
            // 📦 REMOTE REPOSITORY
            maven {
                name = "mavenRemote"
                //url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                url = uri("https://central.sonatype.com/api/v1/publisher/upload")
                //url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                //url = uri("https://central.sonatype.com/publish/repositories/io.github.ivangarza07/maven2/")
                credentials {
                    username = project.findProperty("SONATYPE_USERNAME")?.toString()
                    password = project.findProperty("SONATYPE_PASSWORD")?.toString()
                }
            }

            // 📦 LOCAL REPOSITORY
            mavenLocal()
        }
    }

    extensions.configure<SigningExtension>("signing") {
        sign(
            extensions
                .getByType<PublishingExtension>()
                .publications["release"]
        )
    }
}
