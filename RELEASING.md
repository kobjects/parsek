# Releasing Parsek

Checklist for preparing and publishing a new release of `org.kobjects.parsek:core`.

## Prerequisites (one-time setup)

Configure Maven Central Portal and GPG signing credentials in `~/.gradle/gradle.properties` (outside the repository):

```properties
mavenCentralUsername=<sonatype-portal-token-username>
mavenCentralPassword=<sonatype-portal-token-password>

signing.keyId=<gpg-key-id>
signing.password=<gpg-key-passphrase>
signing.secretKeyRingFile=/path/to/secring.gpg
```

## Release Steps

1. **Clean up previous deprecations**
   - Remove `@Deprecated` APIs kept for one release cycle in the previous version.
   - Ensure any newly replaced APIs are marked `@Deprecated` (with `ReplaceWith` where helpful).

2. **Bump version & update docs**
   - Update `org.kobjects.parsek.version` in `gradle.properties`.
   - Add an entry under `## Version History` in `README.md`.
   - Update `core/module.md` and `README.md` if packages, examples, or links changed.

3. **Verify build & tests across all targets**
   ```bash
   ./gradlew check :core:publishToMavenLocal --warning-mode all
   ```
   - Confirm zero unexpected compiler/Gradle warnings and inspect `~/.m2/repository/org/kobjects/parsek/`.

4. **Commit, tag, and push**
   ```bash
   git add -A
   git commit -m "Release X.Y.Z"
   git tag vX.Y.Z
   git push && git push --tags
   ```

5. **Publish to Maven Central**
   ```bash
   ./gradlew :core:publishToMavenCentral
   ```
   - `automaticRelease = true` is configured in `core/build.gradle.kts`, so Central Portal will validate and publish automatically.

6. **Update hosted Dokka documentation**
   ```bash
   ./gradlew :core:dokkaGenerate
   ```
   - Upload the generated `core/build/dokka/` directory to `https://kobjects.org/parsek/dokka/`.
   - Spot-check the links in `README.md` (both Dokka links and the Maven Central badge).
