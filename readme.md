### Json Config

#### A tiny library which allows to manage configuration files in json.

#### Used especially in minecraft mod dev

#### If you want to understand how this library works, clone the project and have a look at the unit test

#### To import the library in your project, here is what your build.gradle should look like

```
plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0" // Use for annotation like @kotlinx.serialization.Serializable
}

repositories {
    maven("https://repo.repsy.io/mvn/amibeskyfy16/repo")
}

dependencies {
    implementation("ch.skyfy.jsonconfiglib:json-config-lib:3.0.12")!!
}
```

#### If you want to include this no-mod library in your final jar file, take a look at this: https://github.com/AmibeSkyfy16/MariaDBServerFabricMC/blob/2.0/build.gradle.kts

