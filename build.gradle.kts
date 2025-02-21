plugins {
    kotlin("jvm") version "2.0.20"
}

group = "net.daniero"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")}

tasks.test {
    useJUnitPlatform()
}
