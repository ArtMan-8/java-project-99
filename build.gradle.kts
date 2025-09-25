group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application {
    mainClass = "hexlet.code.AppApplication"
}

repositories {
    mavenCentral()
}

plugins {
    application
    checkstyle
    jacoco
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "8.14.2"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("org.sonarqube") version "6.2.0.5505"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.postgresql:postgresql")
    implementation("net.datafaker:datafaker:2.5.1")
	implementation("org.instancio:instancio-junit:5.5.1")

    runtimeOnly("com.h2database:h2")

    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:4.1.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

sonar {
  properties {
    property("sonar.projectKey", "ArtMan-8_java-project-99")
    property("sonar.organization", "artman-8")
  }
}