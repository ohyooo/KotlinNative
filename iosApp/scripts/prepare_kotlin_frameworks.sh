#!/bin/bash

set -euo pipefail

script_dir="$(cd "$(dirname "$0")" && pwd)"
project_root="$(cd "$script_dir/../.." && pwd)"

configuration="${CONFIGURATION:-}"
sdk_name="${SDK_NAME:-}"
archs="${ARCHS:-}"

if [[ -z "$configuration" || -z "$sdk_name" || -z "$archs" ]]; then
  echo "CONFIGURATION, SDK_NAME and ARCHS must be set by Xcode."
  exit 1
fi

case "$configuration" in
  Debug)
    build_type_dir="debugFramework"
    task_build_type="Debug"
    kotlin_framework_build_type="debug"
    ;;
  Release)
    build_type_dir="releaseFramework"
    task_build_type="Release"
    kotlin_framework_build_type="release"
    ;;
  *)
    echo "Unsupported CONFIGURATION=$configuration"
    exit 1
    ;;
esac

case "$sdk_name" in
  iphoneos*)
    kotlin_target="iosArm64"
    task_target_suffix="IosArm64"
    ;;
  iphonesimulator*)
    if [[ " $archs " != *" arm64 "* ]]; then
      echo "Unsupported simulator ARCHS=$archs. Only arm64 simulator builds are configured."
      exit 1
    fi
    kotlin_target="iosSimulatorArm64"
    task_target_suffix="IosSimulatorArm64"
    ;;
  *)
    echo "Unsupported SDK_NAME=$sdk_name"
    exit 1
    ;;
esac

cd "$project_root"

KOTLIN_FRAMEWORK_BUILD_TYPE="$kotlin_framework_build_type" ./gradlew \
  --no-configure-on-demand \
  --console=plain \
  ":shared:link${task_build_type}Framework${task_target_suffix}" \
  ":ui:link${task_build_type}Framework${task_target_suffix}"

for module in shared ui; do
  source_dir="$project_root/$module/build/bin/$kotlin_target/$build_type_dir"
  destination_dir="$project_root/$module/build/xcode-frameworks/$configuration/$sdk_name"

  mkdir -p "$destination_dir"
  rsync -a --delete "$source_dir/" "$destination_dir/"
done
