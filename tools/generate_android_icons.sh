#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="$ROOT/assets/app-icon-source.png"
for spec in "mdpi:48" "hdpi:72" "xhdpi:96" "xxhdpi:144" "xxxhdpi:192"; do
  density="${spec%%:*}"
  size="${spec##*:}"
  out="$ROOT/android/app/src/main/res/mipmap-$density"
  mkdir -p "$out"
  ffmpeg -hide_banner -loglevel error -y -i "$SRC" -vf "scale=${size}:${size}:flags=lanczos" "$out/ic_launcher.png"
done
cp "$ROOT/assets/app-icon-source.png" "$ROOT/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.png"
