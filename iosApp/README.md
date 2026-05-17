# iosApp

This iOS app module links the Kotlin/Native framework from `ui` through a pre-build script that compiles the correct Apple target and copies the framework into Xcode search paths. The `ui` framework exports `shared`, so the app links a single Kotlin/Native runtime.

## Prerequisites

- Full Xcode installed (`/Applications/Xcode.app`)
- [XcodeGen](https://github.com/yonaskolb/XcodeGen)

## Generate Project

```sh
cd iosApp
xcodegen
```

Then open `iosApp.xcodeproj` in Xcode and run.

## How Framework Is Linked

A pre-build script in `project.yml` runs:

`iosApp/scripts/prepare_kotlin_frameworks.sh`

Xcode then links `ui` from:

`ui/build/xcode-frameworks/<CONFIGURATION>/<SDK_NAME>`

## Android Studio Run

Use an **Xcode Application** run configuration in Android Studio and select `iosApp.xcodeproj`.
