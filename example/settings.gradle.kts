//pluginManagement {
//    repositories {
//        mavenLocal()
//        gradlePluginPortal()
//    }
//}
//
//plugins {
//    id("conventions.enforcer") version "1.0-SNAPSHOT"
//}
//
//val conventions = the<conventions.enforcer.ConventionsEnforcerExtension>()
//
//conventions.rules {
//    allowedPlugins.set(listOf("java", "kotlin"))
//    bannedDependencies.set(listOf("com.squareup.retrofit2:retrofit"))
//}
//conventions.excludedBuildFiles.set(listOf(":legacy:build.gradle"))
//
//rootProject.name = "example"