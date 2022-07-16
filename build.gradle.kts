import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
    id("java-library")
    `maven-publish`
    idea
}

group = "ch.skyfy.jsonconfig"
version = "2.1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha16")
    implementation("org.slf4j:slf4j-api:1.7.36")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
}

tasks {

    val javaVersion = JavaVersion.VERSION_17

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    withType<JavaCompile> {
        options.release.set(javaVersion.toString().toInt())
        options.encoding = "UTF-8"
    }

    java {
        withSourcesJar()
        withJavadocJar()

        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
            vendor.set(JvmVendorSpec.BELLSOFT)
        }
    }

    javadoc {
        options.encoding = "UTF-8"
        options.source = javaVersion.toString()
    }

    jar {
        manifest {
            attributes(
                mapOf(
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version
                )
            )
        }
    }

    test {
        useJUnitPlatform()

        testLogging {
            outputs.upToDateWhen { false } // When the build task is executed, stderr-stdout of test classes will be show
            showStandardStreams = true
        }
    }
}


publishing {

    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "json-config"
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set("json-config")
                description.set("a tiny json config library used for minecraft mod dev")

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

            }
        }
    }

    repositories {
        maven {
            url = uri("https://repo.repsy.io/mvn/amibeskyfy16/repo")
            credentials {
                val properties = Properties()
                properties.load(file("C:\\Users\\Skyfy16\\.gradle\\repsy.properties").inputStream())
                username = "${properties["USERNAME"]}"
                password = "${properties["PASSWORD"]}"
            }
        }
    }
}