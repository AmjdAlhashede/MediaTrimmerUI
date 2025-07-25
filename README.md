# MediaTrimmer Compose UI

A lightweight and modern UI component for trimming audio and video in Android apps, built entirely with Jetpack Compose.

This library focuses purely on the **UI layer**, giving you full control over the media source and processing logic. It is especially designed to be **flexible**, **customizable**, and easy to integrate into your existing media pipeline.

---

## ✨ Features (Alpha)

> 🚧 This project is in **early alpha** — APIs and behavior are subject to change.

- Minimal and flexible trimming UI
- Jetpack Compose-native and fully themable
- Integrates with your own media playback system (e.g., ExoPlayer)
- Fully customizable handles, layout, and visual content
- **Media-agnostic**: visualize your content however you like (waveform, thumbnails, etc.)


## Installation

To start using MediaTrimmer Compose UI, simply add the following dependency to your app-level build.gradle.kts file:  
```kotlin
implementation("io.github.amjdalhashede:mediatrimmer-compose-ui:1.0.0-alpha2")
``` 

Or in Gradle Groovy DSL `build.gradle`:

```groovy
implementation 'io.github.amjdalhashede:mediatrimmer-compose-ui:1.0.0-alpha2'
```


## License: Apache License 2.0 
[See LICENSE file for details](./LICENSE)
