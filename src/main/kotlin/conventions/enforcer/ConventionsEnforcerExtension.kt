package conventions.enforcer

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Gradle DSL extension for configuring the Conventions Enforcer plugin.
 */
open class ConventionsEnforcerExtension @Inject constructor(
    private val objects: ObjectFactory
) {
    /**
     * List of build files to be excluded from enforcement of rules.
     */
    val excludedBuildFiles: ListProperty<String> = objects.listProperty(String::class.java)

    /**
     * If the build should not fail if violations are reported, then this flag is enabled.
     */
    val shouldFailOnViolation: Property<Boolean> = objects.property(Boolean::class.java)
}