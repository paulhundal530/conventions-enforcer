import com.gradle.publish.PluginBundleExtension

plugins {
    kotlin("jvm") version "2.2.0"
    id("java-gradle-plugin")
    id("maven-publish")
    id("groovy")
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "io.github.phundal"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.codehaus.groovy:groovy-all:2.4.21")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

java {
    sourceSets["main"].java.srcDirs("src/main/kotlin")
}

tasks.test {
    useJUnitPlatform()
}

/** Plugin definition **/
gradlePlugin {
    plugins {
        register("conventionsEnforcer") {
            id = "conventions.enforcer"
            implementationClass = "conventions.enforcer.ConventionsEnforcerPlugin"
            displayName = "Conventions Enforcer"
            description = "A settings plugin to enforce plugin and dependency conventions."
            tags.set(listOf("gradle", "plugin", "conventions", "enforcer", "settings"))
        }
    }
}

/** Gradle Plugin Portal metadata **/
//pluginBundle {
//    website = "https://github.com/paulhundal530/conventions-enforcer"
//    vcsUrl = "https://github.com/paulhundal530/conventions-enforcer"
//    tags = listOf("gradle", "plugin", "conventions", "enforcer", "settings")
//
//    plugins {
//        create("conventionsEnforcer") {
//            id = "conventions.enforcer"
//            displayName = "Conventions Enforcer"
//            description = "A Gradle plugin to enforce plugin and dependency conventions."
//        }
//    }
//}

/** Publishing step **/
publishing {
    publications {
        withType<MavenPublication>().configureEach {
            pom {
                name.set("Conventions Enforcer")
                description.set("A Gradle plugin to enforce plugin and dependency conventions.")
                url.set("https://github.com/paulhundal530/conventions-enforcer")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/paulhundal530/conventions-enforcer.git")
                    developerConnection.set("scm:git:ssh://github.com:paulhundal530/conventions-enforcer.git")
                    url.set("https://github.com/paulhundal530/conventions-enforcer")
                }
            }
        }
    }
}