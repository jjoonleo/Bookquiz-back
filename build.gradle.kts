import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("org.flywaydb.flyway") version "9.22.3"
}

group = "kr.co.bookquiz"
version = "0.0.1-SNAPSHOT"
var dockerImageName = "jjoonleo/bookquiz-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.hibernate.orm:hibernate-core")
    // JWT dependencies
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    
    // Configure Mockito to use static agent loading instead of dynamic loading
    systemProperty("mockito.mock.maker", "org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker")
    
    // Configure Mockito as a Java agent to avoid dynamic loading
    doFirst {
        val mockitoCoreJar = configurations.testRuntimeClasspath.get().files
            .find { it.name.contains("mockito-core") }
            ?.absolutePath
        
        if (mockitoCoreJar != null) {
            jvmArgs("-javaagent:$mockitoCoreJar")
        }
    }
}
val installLocalGitHook = tasks.register<Copy>("installLocalGitHook") {
    from("${rootProject.rootDir}/src/main/resources/git-hooks/pre-commit")
    into(File("${rootProject.rootDir}/.git/hooks"))
}

tasks.build {
    dependsOn(installLocalGitHook)
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set(dockerImageName)
    builder.set("dashaun/builder:tiny")
}

flyway {
    url = "jdbc:postgresql://localhost:5432/bookquiz_db"
    user = "postgres"
    password = "postgres"
}