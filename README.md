# KotlinNative Demo

This repository is a Kotlin/Native demo. It compiles Kotlin/Native code into Android native shared libraries (`.so`) and iOS frameworks that are consumed by app modules.

## What This Demo Shows

- Kotlin/Native shared library builds for Android ABIs.
- Packaging the native libraries into the Android app.
- Building and linking iOS framework artifacts from `shared`.
- A simple Compose UI that calls into native code.

## Project Structure

- `shared/`: Kotlin Multiplatform module that produces `libshared.so`.
- `android/`: Android app module that packages and loads the native libraries.
- `iosApp/`: iOS app module (SwiftUI + XcodeGen) that links `shared` via `embedAndSignAppleFrameworkForXcode`.

## How It Works (High Level)

- The `shared` module builds Kotlin/Native shared libraries for Android targets.
- The `android` module copies those `.so` outputs into `jniLibs`.
- The Android app invokes native functions exposed by `libshared.so`.
- The iOS app invokes Kotlin/Native API from `shared` framework output under `shared/build/xcode-frameworks`.

## Build

Android

```sh
./gradlew :android:assembleRelease
```

Windows

```sh
./gradlew :shared:runDebugExecutableMingwX64
```

iOS (with Xcode + XcodeGen)

```sh
cd iosApp
xcodegen
open iosApp.xcodeproj
```

## Notes

- For native, TLS is not supported because this demo uses Ktor CIO. [YouTrack: TLS sessions are not supported on Native platform](https://youtrack.jetbrains.com/issue/KTOR-7262)

- This is a demo project intended for learning and experimentation.

## Acknowledgements

- This README and parts of the setup were created with help from an AI assistant (OpenAI Codex).
