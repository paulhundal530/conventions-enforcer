package conventions.enforcer.rules

import conventions.FakeVisitorManager
import conventions.enforcer.api.ClosureRule
import conventions.enforcer.api.Visitor
import conventions.enforcer.impl.rules.UnsupportedClosureRule
import conventions.enforcer.impl.violations.ClosureViolation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UnsupportedClosureRuleTest {

    private lateinit var rule: ClosureRule
    private lateinit var fakeVisitorManager: FakeVisitorManager
    private val fakeVisitor = object : Visitor(emptyList(), FakeVisitorManager()) {}

    private val buildFile = Path.of("example/build.gradle")

    @BeforeEach
    fun setup() {
        fakeVisitorManager = FakeVisitorManager()
        rule = UnsupportedClosureRule(
            allowlist = setOf("plugins", "dependencies"),
            visitorManager = fakeVisitorManager
        )
    }

    @Test
    fun `no violations when only allowed closures are used`() {
        fakeVisitorManager.add(fakeVisitor.javaClass, "plugins", 1)
        fakeVisitorManager.add(fakeVisitor.javaClass, "dependencies", 2)

        val violations = rule.enforce(buildFile, fakeVisitor)

        assertTrue(violations.isEmpty(), "Expected no violations")
    }

    @Test
    fun `violation is reported for unsupported closure`() {
        fakeVisitorManager.add(fakeVisitor.javaClass, "unsupportedBlock", 5)

        val violations = rule.enforce(buildFile, fakeVisitor)

        assertEquals(1, violations.size)
        val violation = violations.first() as ClosureViolation
        assertTrue(violation.message.contains("unsupportedBlock"))
        assertTrue(violation.message.contains("plugins"))
        assertEquals(buildFile, violation.buildFile)
    }

    @Test
    fun `multiple violations for same unsupported closure at different lines`() {
        fakeVisitorManager.add(fakeVisitor.javaClass, "foo", 3)
        fakeVisitorManager.add(fakeVisitor.javaClass, "foo", 7)

        val violations = rule.enforce(buildFile, fakeVisitor)

        assertEquals(2, violations.size)
        violations.forEach { v ->
            assertTrue(v.message.contains("foo"))
        }
    }
}