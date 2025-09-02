package conventions.enforcer

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * Extension for declaring convention enforcement rules.
 */
open class RulesExtension @Inject constructor(objects: ObjectFactory) {
    /**
     * List of plugin IDs that are explicitly allowed in the project.
     *
     * This list acts as a safelist. Any plugin not in this list may be treated as a violation,
     * depending on enforcement rules.
     *
     * Example: ["java", "kotlin", "com.android.application"]
     */
    val allowedPlugins: ListProperty<String> = objects.listProperty(String::class.java)

    /**
     * List of closure groups that are allowed in the project.
     *
     * This list acts as a safelist. Any closure block not in this list may be treated as a violation,
     * depending on enforcement rules.
     *
     * Example: ["myCustomClosure { }, ktlint{ }, etc."]
     */
    val allowedClosures: ListProperty<String> = objects.listProperty(String::class.java)

    /**
     * List of dependencies (as group:artifact) that are banned from usage.
     *
     * Example: ["com.squareup.retrofit2:retrofit", "org.jetbrains.kotlin:kotlin-reflect"]
     */
    val bannedDependencies: ListProperty<String> = objects.listProperty(String::class.java)
}