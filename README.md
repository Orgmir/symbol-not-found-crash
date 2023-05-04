# CrashyCrash

This repo is the reproducible sample for this [kotlin issue](https://youtrack.jetbrains.com/issue/KT-58461).

Steps to reproduce:
1. Open Xcode project and archive the app
2. Distribute for Development, with App Thinning for "All compatible device variants"
3. Install on device, by drag and drop in Finder for example
4. Open the app

The crash will be visible in the "Device logs", in "Devices" window in Xcode.