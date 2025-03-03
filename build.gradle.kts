plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.kitandasmart"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5") // Check for latest version
	implementation("com.microsoft.graph:microsoft-graph:5.0.0")
	implementation("com.azure:azure-identity:1.6.0")
	implementation("net.sourceforge.tess4j:tess4j:4.5.4")
	implementation("org.slf4j:slf4j-api:2.0.9") // Example version

	implementation("org.openpnp:opencv:4.9.0-0")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")  // Provides all the necessary implementation classes
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
