# Fix Supabase and Material 3 Build Errors

The project currently fails to build due to a renamed Supabase dependency (`gotrue-kt` to `auth-kt`) and subsequent API changes in the Supabase-kt library. There are also deprecated/removed Material 3 APIs being used in `AuthScreen.kt`.

## Proposed Changes

### Supabase Integration

#### [SupabaseClient.kt](file:///D:/Projects/ecocrux_android/app/src/main/java/com/example/ecocrux/data/SupabaseClient.kt)
- Update `GoTrue` to `Auth`.
- Change `install(GoTrue)` to `install(Auth)`.

#### [AuthRepository.kt](file:///D:/Projects/ecocrux_android/app/src/main/java/com/example/ecocrux/data/AuthRepository.kt)
- Update imports from `io.github.jan.supabase.gotrue` to `io.github.jan.supabase.auth`.
- Update `signUpWith` and `signInWith` usages if the signature has changed (checking for `Email` provider).

### UI Components

#### [AuthScreen.kt](file:///D:/Projects/ecocrux_android/app/src/main/java/com/example/ecocrux/ui/screens/AuthScreen.kt)
- Replace `TextFieldDefaults.outlinedTextFieldColors` with `OutlinedTextFieldDefaults.colors()`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure the project compiles.
- Run existing unit tests: `./gradlew :app:testDebugUnitTest`.

### Manual Verification
- Render Compose Previews for `AuthScreen` and `SplashScreen` to ensure UI remains correct.
