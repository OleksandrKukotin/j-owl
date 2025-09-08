plugins {
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

group = "com.github.oleksandrkukotin"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val logForJavaVer = "2.24.3"
val jSoupVer = "1.18.3"

val springBootStarterVer = "3.4.2"
val apacheLuceneVer = "10.1.0"
val apacheLuceneAnalyzersVer = "8.11.4"
dependencies {
    implementation("org.apache.logging.log4j:log4j-core:$logForJavaVer")
    implementation("org.apache.logging.log4j:log4j-api:$logForJavaVer")

    implementation("org.apache.lucene:lucene-core:$apacheLuceneVer")
    implementation("org.apache.lucene:lucene-queryparser:$apacheLuceneVer")
    implementation("org.apache.lucene:lucene-highlighter:$apacheLuceneVer")
    implementation("org.apache.lucene:lucene-memory:$apacheLuceneVer")
    implementation("org.apache.lucene:lucene-analyzers-common:$apacheLuceneAnalyzersVer")

    implementation("org.jsoup:jsoup:$jSoupVer")

    implementation("org.springframework.boot:spring-boot-starter:$springBootStarterVer")
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootStarterVer")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootStarterVer")
}

tasks.test {
    useJUnitPlatform()
}
