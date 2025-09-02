# 🛡️ Conventions Enforcer

**A Gradle `SettingsPlugin` to enforce consistent build conventions across your repo.**

---

## 📌 What It Does

This plugin statically analyzes your `build.gradle` files using the Groovy AST to enforce conventions around:

- ✅ **Allowed plugins** (e.g. only use `kotlin`, `java`)
- 🚫 **Banned dependencies** (e.g. block `retrofit`, `kotlinx-coroutines-core`)
- ✅ **Allowed top-level closures** (e.g. allow only `dependencies`, `android`, etc.)

Violations are reported to the console and also written to a clean HTML report.

---

## ⚙️ How It Works

The plugin traverses every project in your build and parses the build scripts using the Groovy AST. Each build file is checked against a set of **rules** defined in your root `settings.gradle.kts`.

It visits:
- `plugins {}` and `apply plugin: ...` blocks
- `dependencies {}` blocks
- Any other top-level closures you configure

Violations are grouped by rule type and reported to the console and a clickable HTML file.

---

## 🚀 How to Use

### 1. Apply the Plugin in `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.phundal.conventions.enforcer") version "<current-version>"
}

conventionsEnforcer {
    rules {
        allowedPlugins = ['java', 'kotlin'] // Optional (default: none)
        allowedClosures = ['dependencies'] // Optional (default: none)
        bannedDependencies = ['com.squareup.retrofit2:retrofit'] // Optional (default: none)
    }

    excludedBuildFiles = [':legacy:build.gradle'] // Optional (default: none)
    shouldFailOnViolation = false // Optional (default: true)
}

rootProject.name = "your-project"
```

### 2. Run the Enforcement Task

```bash
./gradlew enforceConventions
```

•	✅ First 10 violations are shown in the console

•	📄 Full report written to: build/reports/conventions-enforcer/report.html

### Running Tests

```bash
./gradlew test
```

### Running Example

```bash
./gradlew -p example enforceConventions
```