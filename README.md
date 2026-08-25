# Liquid Glass for Android

[![](https://jitpack.io/v/zhaopuchu/LiquidGlass-Fork-IOS27.svg)](https://jitpack.io/#zhaopuchu/LiquidGlass-Fork-IOS27)

纯 Android、Jetpack Compose 的 Liquid Glass 库。当前版本：**v1.1.0**

- minSdk 23
- API 31+：RenderEffect、Blur、ColorFilter
- API 33+：RuntimeShader、Lens、色散与动态高光

## 接入 JitPack

### 1. 添加仓库

在根目录 `settings.gradle.kts` 的 `repositories` 中加入 JitPack：

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Groovy 的 `settings.gradle` 这样写：

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. 添加依赖

在模块的 `build.gradle.kts` 中：

```kotlin
dependencies {
    implementation("com.github.zhaopuchu:LiquidGlass-Fork-IOS27:v1.1.0")
}
```

Groovy：

```groovy
dependencies {
    implementation 'com.github.zhaopuchu:LiquidGlass-Fork-IOS27:v1.1.0'
}
```

版本号与 GitHub tag 一致，发布页见 [JitPack](https://jitpack.io/#zhaopuchu/LiquidGlass-Fork-IOS27)。

## 许可证

Apache License 2.0，详见 [LICENSE](LICENSE)。
