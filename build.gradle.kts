import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
    id("java-library")
    `maven-publish`
    idea
}

base {
    archivesName.set(properties["archives_name"].toString())
    group = property("maven_group")!!
    version = property("version")!!
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.slf4j:slf4j-api:2.0.12")

    testImplementation("ch.qos.logback:logback-classic:1.5.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.23")
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

    named<Wrapper>("wrapper") {
        gradleVersion = "8.7"
        distributionType = Wrapper.DistributionType.BIN
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
            artifactId = project.base.archivesName.get()
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set(project.base.archivesName.get())
                description.set("A tiny json config library used for Minecraft modding development")

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
                properties.load(file("Z:\\#2 - Profiles\\KotSeek\\Resources\\Repsy\\repsy.properties").inputStream())
                username = "${properties["USERNAME"]}"
                password = "${properties["PASSWORD"]}"
            }
        }
    }
}