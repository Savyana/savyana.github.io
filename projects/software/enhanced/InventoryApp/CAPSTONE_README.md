# CS 499 Software Design and Engineering Enhancement

This Android Studio project is an enhanced version of the student's CS 360 InventoryApp prototype. The original starter project is included in the submission archive under `original/`.

## Enhancements
- SQLite persistence with per-user access to inventory records
- Registration and login using salted PBKDF2 password hashing
- Add, edit, delete and display inventory records
- Search by item name using selection arguments, with LIKE wildcard escaping
- Reusable InventoryRules business logic and JVM unit tests
- Versioned SQLite schema migration with an owner/name index (no table drops)
- Optional SMS notifications for low-stock threshold crossings

## Local verification
Open the `enhanced/InventoryApp` directory in Android Studio, wait for Gradle sync, run `testDebugUnitTest`, then launch an emulator. Register a user, add/edit/delete records, test search with text and %/_, restart the app, and test SMS permission approved and denied.

The project has not been built or emulator-tested in the authoring environment. Gradle settings may need alignment with the Android SDK installed on the testing computer. SMS delivery on emulators varies; test permission and no-crash behavior independently of carrier delivery.

## Security note
The local login is intended for a course prototype, not a production identity provider. Do not reuse personal passwords. Only request SMS permission when using optional messaging.
