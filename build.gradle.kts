plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.lab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx{
    version = "21.0.5"
    modules = listOf("javafx.controls" , "javafx.fxml")
}

application{
    mainClass.set("com.lab.Main")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.7")
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}