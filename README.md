# Liquid Glass for Android

纯 Android、Jetpack Compose 版本的 Liquid Glass 外部库。库代码由
[Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass)
2.0 KMP 源码和 [Kyant0/Shapes](https://github.com/Kyant0/Shapes) 迁移而来。

## 工程结构

- `:liquid-glass`：自包含 Android AAR，包含 Backdrop 2.0 与 Shapes。
- `:sample`：Android 示例 App；`LiquidButton`、`LiquidToggle`、
  `LiquidSlider`、`LiquidBottomTabs` 仅属于示例，不是库 API。

项目不使用 Kotlin Multiplatform、Compose Multiplatform 或 Skiko。

## Android 版本能力

- minSdk 23：AndroidX Compose 1.11.1 的最低版本；不支持完整玻璃效果。
- API 31+：支持 RenderEffect、Blur 和 ColorFilter。
- API 33+：支持 RuntimeShader、Lens、色散与动态高光。

API 23–30 沿用上游的静默降级行为，不包含软件模拟。

## iOS 27 风格暗边

Apple 在 WWDC26 将新版 Liquid Glass 描述为 `darkened edge` 与更亮的镜面高光组合。
本库把暗边定义为 Highlight 轮廓上的窄暗线，不再将折射区域整体压暗：

```kotlin
highlight = {
    Highlight.IOS27.copy(
        darkEdge = DarkEdge(
            width = 0.75.dp,
            alpha = 0.18f
        )
    )
}
```

- `DarkEdge` 可配置 `width`、`color` 和 `alpha`，默认宽度 `0.75dp`、强度 `0.18f`。
- 暗线贴合形状轮廓，并与内部白色高光相邻，不受 `refractionHeight` 影响。
- `Highlight.IOS27` 使用上下方向的窄镜面高光；方向性 shader 在 API 33+ 生效，暗线本身不依赖 RuntimeShader。
- 这是依据 iOS 27 `darkened edge` 视觉特征实现的近似效果，不是 Apple 私有着色器的复刻。

示例 App 的 `Glass playground` 页面提供 `Dark edge` 滑块，可实时调试强度。

## JitPack

在 `settings.gradle.kts` 中加入仓库：

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

当前 tag 为 `v1.0.0`：

```kotlin
dependencies {
    implementation("com.github.zhaopuchu:LiquidGlass-Fork-IOS27:v1.0.0")
}
```

推送 GitHub tag 后，到 [JitPack](https://jitpack.io/#zhaopuchu/LiquidGlass-Fork-IOS27) 执行 Look up 即可构建该版本。仓库需公开，JitPack 才能免费构建。

## 源码与许可证

AndroidLiquidGlass 基线：
`b18eb0ff12c616546a68c72e7d0097f1ab286c87`

Shapes 基线：
`acf86616d1f3e911e95e2e624cd86438c1426a17`

本项目仅进行 Android 工程结构、包名、资源 API 和 `expect/actual`
扁平化迁移；核心算法与 AGSL 未做优化或重写。详细来源见
[`NOTICE`](NOTICE)，许可证全文见 [`LICENSE`](LICENSE)。
