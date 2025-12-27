# Contributing to DroneCraft

Thank you for your interest in contributing to **DroneCraft**!
We welcome contributions from players, addon developers, and maintainers.

This document explains **how to contribute effectively**, without accidentally breaking APIs or documentation
guarantees.

---

## 🧭 Ways to Contribute

- 🐛 Bug reports
- ✨ Feature requests
- 🧱 Code contributions
- 📚 Documentation improvements
- 🔌 Addon / integration examples

---

## 📚 Documentation Structure (IMPORTANT)

We maintain **two distinct documentation areas**:

| Location          | Purpose                                 | Public |
|-------------------|-----------------------------------------|--------|
| `/docs/`          | Player wiki, API docs, contributor docs | ✅ Yes  |
| `/docs-internal/` | Unreleased docs                         | ❌ No   |

⚠️ **Do not** document unfinished or speculative features in `/docs/` include them in `/docs-internal/` and we will port
them on feature release.

---

## 🧑‍💻 Code Contribution Guidelines

### Branching

| Prefix     | Purpose                   | Merge        | Write        |
|------------|---------------------------|--------------|--------------|
| `main`     | Main development branch   | Maintainers  | Never        |
| `v`        | Version release branches  | Maintainers  | Never        |
| `feature/` | Specific feature branches | Contributors | Contributors |
| `hotfix/`  | Hotfixes for old releases | Contributors | Contributors |

### Style & Formatting

General

- Follow existing code style
- Prefer clarity over cleverness
- Keep commits focused

Minecraft-specific rules

- Server is authoritative
- Never trust client input
- Avoid per-tick allocations
- Avoid per-tick networking

---

#### 🧹 Code Quality & Static Analysis

This project uses a set of automated tools to enforce **consisten style**, **catch bugs early**, and **maintain
long-term code quality**. All checks run locally before commits and are enforced again in CI.
---

##### Checkstyle

**Purpose**: Enforces coding style and formatting rules.

Checkstyle ensures that all Java code follows a consistent style guide (based on Google Java Style with project-specific
adjustments). This helps keep the codebase readable and predicatable across contributors.

- Catches issues like unused imports, missing braces, excessive method length, and indentation problems
- Configured via `config/checkstyle/checkstyle.xml`
- Runs automatically during pre-commit and CI

---

##### PMD

**Purpose**: Detects common coding mistakes and design issues.

PMD performs static analysis to find problems that may not be caught by the compiler, such as overly complex methods,
poor design choices, or inefficient constructs.

- Flags potential bugs, performance issues, and maintainability concerns
- Rules defined in `config/pmd/pmd.xml`
- Complements Checkstyle by focusing on *code quality*, not just formatting

---

##### SpotBugs

**Purpose**: Finds potential runtime bugs using bytecode analysis.

SpotBugs analyses compiled bytecode to identify issues such as null pointer risks, incorrect equals/hashCode
implementations, and threading problems.

- Focuses on correctness and reliability
- Runs on compiled classes rather than source code
- Particularly useful for catching subtle bugs early

---

##### Spotless

**Purpose**: Automatically formats source code

Spotless applies consistent formatting rules to the codebase and can automatically fix many issues. This removes
bikeshedding around formatting and keeps diffs clean.

- Uses `google-java-format`
- Can auto-fix formatting issues with

```bash
  ./gradlew spotlessApply
```

- Formatting is verified during pre-commit and CI

---

##### Gradle

**Purpose**: Build automation and task orchestration.

Gradle is used to configure and run all code quality tools through a single, consistent interface.

- A custom `codeQuality` task runs all checks together
- Ensures local development and CI use the exact same rules
- Acts as the single source of truth for quality enforcement

---

##### pre-commit

**Purpose**: Version-controlled Git hooks

The `pre-commit` tool ensures code quality checks run automatically before every commit, preventing broken or
non-compliant code from being commited.

- Hooks are defined in `.pre-commit-config.yml`
- Aims to prevent **"it works on my machine"** issues
- Keeps Git hooks consistent across all contributions
  To enable hooks locally:

```bash
  pre-commit install
```

---

##### Continuous Integration (CI)

**Purpose**: Enforces quality checks on every push and pull request.

All code quality checks are re-run in CI to ensure nothing bypasses local hooks.

- Prevents accidental or intentional bypass of local checks
- Ensures the `main` and version branches always meets quality standards

---

#### Summary

Together, these tools provide:

- ✅ Consistent code style
- ✅ Early detection of bugs
- ✅ Automated formatting
- ✅ Fast local feedback
- ✅ Strict CI enforcement
  Contributors are encouraged to run:

```bash
  ./gradlew codeQuality
```

---

## 🔌 API Contributions (STRICT RULES)

The public API lives under:
`src/main/java/com/dyingday/dronecraft/api`

### API Rules

- **All public API must be documented**
- API changes must follow semantic versioning
- Breaking changes require an API version bump
- Experimental APIs must be clearly marked

🚨 **Pull requests that change the API without documentation will be rejected.**

---

## 📚 Documentation Contributions

See: [`docs/docs_contributing.md`](docs/docs_contributing.md)

---

## 🧪 Building & Testing

```bash
  ./gradlew build
```

If your change affects:

- API -> `./gradlew apiJavadoc`
- Networking -> test on dedicated server
- World data -> test save/load compatibility

---

## 🤝 Code of Conduct

Be respectful, constructive, and patient. We are all learning, have different experiences and program differently.

Harassment or hostility will not be tolerated.

---
Thank you for helping make DroneCraft better ❤️
