package conventions.enforcer.rules

import conventions.FakeVisitorManager
import conventions.enforcer.api.DependencyRule
import conventions.enforcer.api.Visitor
import conventions.enforcer.impl.rules.BannedDependencyRule
import conventions.enforcer.impl.violations.DependencyViolation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BannedDependencyRuleTest {

    private lateinit var rule: DependencyRule
    private lateinit var fakeVisitorManager: FakeVisitorManager
    private val fakeVisitor = object : Visitor(emptyList(), FakeVisitorManager()) {}

    private val buildFile = Path.of("example/build.gradle")

    @BeforeEach
    fun setup() {
        fakeVisitorManager = FakeVisitorManager()
        rule = BannedDependencyRule(
            banned = setOf("com.squareup.retrofit2:retrofit", "org.jetbrains.kotlin:kotlin-reflect"),
            visitorManager = fakeVisitorManager
        )
    }

    @Test
    fun `no violations when no banned dependencies are present`() {
        fakeVisitorManager.add(fakeVisitor.javaClass, "androidx.core:core-ktx", 2)
        fakeVisitorManager.add(fakeVisitor.javaClass, "junit:junit", 5)

        val violations = rule.enforce(buildFile, fakeVisitor)

        assertTrue(violations.isEmpty(), "Expected no violations for safe dependencies")
    }

    @Test
    fun `violation is reported for a banned dependency`() {
        fakeVisitorManager.add(fakeVisitor.javaClass, "com.squareup.retrofit2:retrofit", 4)

        val violations = rule.enforce(buildFile, fakeVisitor)

        assertEquals(1, violations.size)
        val violation = violations.first() as DependencyViolation
        assertTrue(violation.message.contains("retrofit"))
        assertTrue(violation.message.contains("disallowed"))
        assertEquals(buildFile, violation.buildFile)
    }

    @Test
    fun `multiple violations reported for different banned dependencies`() {
        fakeVisitorManager.add(fakeVisitor.javaClass, "com.squareup.retrofit2:retrofit", 3)
        fakeVisitorManager.add(fakeVisitor.javaClass, "org.jetbrains.kotlin:kotlin-reflect", 8)

        val violations = rule.enforce(buildFile, fakeVisitor)

        assertEquals(2, violations.size)
        assertTrue(violations.any { it.message.contains("retrofit") })
        assertTrue(violations.any { it.message.contains("kotlin-reflect") })
    }
}
