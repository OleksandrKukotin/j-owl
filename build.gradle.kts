plugins {
    id("java")
}

group = "com.github.oleksandrkukotin"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val logForJavaVer = "2.24.3"
val springBootStarterVer = "3.4.2"
dependencies {
    implementation("org.apache.logging.log4j:log4j-core:$logForJavaVer")
    implementation("org.apache.logging.log4j:log4j-api:$logForJavaVer")

    implementation("org.apache.lucene:lucene-core:10.1.0")
    implementation("org.jsoup:jsoup:1.18.3")

    implementation("org.springframework.boot:spring-boot-starter:$springBootStarterVer")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootStarterVer")
}

tasks.test {
    useJUnitPlatform()
}
