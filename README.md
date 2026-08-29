# Liquid Glass for Android

[![](https://jitpack.io/v/zhaopuchu/LiquidGlass-Fork-IOS27.svg)](https://jitpack.io/#zhaopuchu/LiquidGlass-Fork-IOS27)

纯 Android、Jetpack Compose 的 Liquid Glass 库。当前版本：**v1.2.0**

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
    implementation("com.github.zhaopuchu:LiquidGlass-Fork-IOS27:v1.2.0")
}
```

Groovy：

```groovy
dependencies {
    implementation 'com.github.zhaopuchu:LiquidGlass-Fork-IOS27:v1.2.0'
}
```

版本号与 GitHub tag 一致，发布页见 [JitPack](https://jitpack.io/#zhaopuchu/LiquidGlass-Fork-IOS27)。

## 参数说明

### 通用玻璃参数

| 参数 | 范围/单位 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `Corner radius` | `0..1` 比例 | `0.5` | 控制玻璃轮廓的圆角比例。`0` 接近直角，`1` 使用组件允许的最大圆角。Circle Button 固定为完整圆形，不提供此参数。 |
| `Blur radius` | `0..32dp` | `0dp` | 控制玻璃内部背景的模糊半径。数值越大，背景细节越模糊；`0` 表示不增加模糊。 |
| `Refraction height` | `0..1` 比例 | `0.2` | 控制边缘折射区域向玻璃内部延伸的距离。Sample 会根据组件短边将比例换算为实际像素。 |
| `Refraction amount` | `0..1` 比例 | `0.2` | 控制背景内容被玻璃边缘偏移、弯折的强度。数值越大，折射位移越明显。 |
| `Chromatic aberration` | `0..1` | `0` | 控制是否启用边缘色散。底层 API 是布尔开关，因此 Sample 中大于 `0` 即启用，具体数值不表示色散强度。 |
| `Dark edge` | `0..1` 透明度 | `0.18` | 控制 iOS 27 风格暗边的整体透明度。`0` 表示关闭，数值越大，玻璃与背景之间的轮廓分离越明显。 |

### Dialog 外观参数

| 参数 | 范围 | 默认值（亮色/暗色） | 作用 |
| --- | --- | --- | --- |
| `Surface alpha` | `0..1` | `0.6 / 0.4` | 控制 Dialog 表面覆盖色的透明度。数值越大，表面底色越明显、玻璃越不通透；`0` 表示不绘制覆盖色。 |
| `Background dim` | `0..1` | `0.23 / 0.56` | 控制 Dialog 后方整个背景的压暗程度。`0` 表示不压暗背景。 |
| `Brightness` | `-1..1` | `0.2 / 0` | 控制玻璃采样内容的亮度。`0` 保持原亮度，正值提亮，负值压暗。 |
| `Saturation` | `0..2` | `1.5` | 控制玻璃采样内容的色彩饱和度。`1` 保持原色，`0` 接近灰度，大于 `1` 增强色彩。 |

### Lens 参数

| 参数 | 类型/单位 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `refractionHeight` | `Float`，像素 | 必填 | 边缘折射区域的实际高度。必须大于 `0` 才会生成 Lens 效果。 |
| `refractionAmount` | `Float`，像素 | 必填 | 折射采样坐标的最大偏移量。数值越大，透过玻璃看到的背景弯曲越明显。 |
| `depthEffect` | `Boolean` | `false` | 是否根据玻璃边缘的深度变化调整折射曲线，使边缘更接近具有厚度的实体玻璃。 |
| `chromaticAberration` | `Boolean` | `false` | 是否使用带色散的折射 Shader。启用后会分别偏移颜色通道，但会增加渲染成本。 |

### DarkEdge 参数

```kotlin
DarkEdge(
    width = 0.5f.dp,
    color = Color.Black,
    alpha = 0.18f,
    spread = 2f.dp,
    blurRadius = 1.25f.dp,
    spreadAlpha = 0.45f
)
```

| 参数 | 类型/单位 | 默认值 | 作用 |
| --- | --- | --- | --- |
| `width` | `Dp` | `0.5dp` | 核心暗边的宽度，负责提供清晰且较细的轮廓。赋值 DarkEdge 后，高光描边也使用该宽度，并去掉高光模糊，保证暗边与高光粗细一致。 |
| `color` | `Color` | `Color.Black` | 暗边颜色。颜色自身的透明度会与 `alpha` 相乘。 |
| `alpha` | `0..1` | `0.18` | 整套暗边效果的基础透明度，同时影响核心暗边和柔化延展区域。 |
| `spread` | `Dp` | `2dp` | 暗边从核心轮廓向玻璃内部延展的范围。数值越大，边缘分离越柔和。 |
| `blurRadius` | `Dp` | `1.25dp` | 柔化延展区域的模糊半径。数值越大，暗边过渡越平滑。 |
| `spreadAlpha` | `0..1` | `0.45` | 柔化延展区域相对于核心暗边的透明度比例。`0` 表示只保留核心暗边。 |

Sample 中的调试配置会按页面分别保存。带有自动保存提示的页面会在参数停止变化约 `300ms` 后写入本地配置，并在下次打开页面时恢复。

## 许可证

Apache License 2.0，详见 [LICENSE](LICENSE)。
