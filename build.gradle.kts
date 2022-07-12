import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("java-library")
}

group = "ch.skyfy.jsonconfig"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("org.apache.logging.log4j:log4j-core:2.18.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.10")
}

tasks{

    val javaVersion = JavaVersion.VERSION_17

    withType<KotlinCompile>{
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    withType<JavaCompile>{
        options.release.set(javaVersion.toString().toInt())
        options.encoding = "UTF-8"
    }

    test{
        useJUnitPlatform()

        testLogging {
            outputs.upToDateWhen { false } // When the build task is executed, stderr-stdout of test classes will be show
            showStandardStreams = true
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

