# iosApp

This iOS app module links the Kotlin/Native frameworks from `ui` and `shared` using the official `embedAndSignAppleFrameworkForXcode` flow.

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

`./gradlew :ui:embedAndSignAppleFrameworkForXcode`

`./gradlew :shared:embedAndSignAppleFrameworkForXcode`

Xcode then links `ui` and `shared` from:

`ui/build/xcode-frameworks/<CONFIGURATION>/<SDK_NAME>`

`shared/build/xcode-frameworks/<CONFIGURATION>/<SDK_NAME>`

## Android Studio Run

Use an **Xcode Application** run configuration in Android Studio and select `iosApp.xcodeproj`.
