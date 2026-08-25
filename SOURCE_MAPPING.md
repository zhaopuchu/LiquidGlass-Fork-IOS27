# 源文件迁移映射

基线：

- AndroidLiquidGlass `b18eb0ff12c616546a68c72e7d0097f1ab286c87`
- Shapes `acf86616d1f3e911e95e2e624cd86438c1426a17`

覆盖统计：commonMain 30/30、androidMain 4/4、Shapes 11/11。

“逐字匹配”由脚本按 UTF-8 全文比较；允许的唯一预处理是已确认的包名/import 替换。
四组平台文件因删除 `expect`/`actual` 并合并声明与 Android 实现，单独标记为平台合并。

## Backdrop commonMain

- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/Backdrop.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/Backdrop.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/BackdropEffectScope.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/BackdropEffectScope.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/backdrops/Backdrop.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/backdrops/Backdrop.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/backdrops/CanvasBackdrop.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/backdrops/CanvasBackdrop.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/backdrops/CombinedBackdrop.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/backdrops/CombinedBackdrop.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/backdrops/EmptyBackdrop.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/backdrops/EmptyBackdrop.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/backdrops/LayerBackdrop.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/backdrops/LayerBackdrop.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/backdrops/LayerBackdropModifier.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/backdrops/LayerBackdropModifier.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/DrawBackdropModifier.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/DrawBackdropModifier.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/effects/Blur.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/effects/Blur.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/effects/ColorFilter.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/effects/ColorFilter.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/effects/Lens.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/effects/Lens.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/effects/RenderEffect.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/effects/RenderEffect.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/highlight/Highlight.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/highlight/Highlight.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/highlight/HighlightModifier.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/highlight/HighlightModifier.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/highlight/HighlightStyle.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/highlight/HighlightStyle.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/internal/InverseLayerScope.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/InverseLayerScope.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/internal/LayerRecorder.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/LayerRecorder.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/internal/Outline.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/Outline.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/internal/Paint.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/Paint.kt`：与 androidMain 合并，移除 expect/actual
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/internal/RenderEffect.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/RenderEffect.kt`：与 androidMain 合并，移除 expect/actual
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/internal/Shaders.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/Shaders.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/internal/ShapeProvider.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/ShapeProvider.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/Platform.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/Platform.kt`：与 androidMain 合并，移除 expect/actual
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/RuntimeShader.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/RuntimeShader.kt`：与 androidMain 合并，移除 expect/actual
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/RuntimeShaderCache.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/RuntimeShaderCache.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/shadow/InnerShadow.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shadow/InnerShadow.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/shadow/InnerShadowModifier.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shadow/InnerShadowModifier.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/shadow/Shadow.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shadow/Shadow.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/commonMain/kotlin/com/kyant/backdrop/shadow/ShadowModifier.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shadow/ShadowModifier.kt`：逐字匹配（仅包名/import 替换）

## Backdrop androidMain

- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/androidMain/kotlin/com/kyant/backdrop/internal/Paint.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/Paint.kt`：与 commonMain 声明合并，保留 Android 实现
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/androidMain/kotlin/com/kyant/backdrop/internal/RenderEffect.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/internal/RenderEffect.kt`：与 commonMain 声明合并，保留 Android 实现
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/androidMain/kotlin/com/kyant/backdrop/Platform.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/Platform.kt`：与 commonMain 声明合并，保留 Android 实现
- `sources/Kyant0-AndroidLiquidGlass/backdrop/src/androidMain/kotlin/com/kyant/backdrop/RuntimeShader.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/RuntimeShader.kt`：与 commonMain 声明合并，保留 Android 实现

## Shapes

- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/Capsule.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/Capsule.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/ContinuousCurvatureRoundedRectangleCornerBuilder.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/ContinuousCurvatureRoundedRectangleCornerBuilder.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/Copy.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/Copy.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/Lerp.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/Lerp.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/Rectangle.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/Rectangle.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/RectangleCornerRadii.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/RectangleCornerRadii.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/RoundedCornerStyle.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/RoundedCornerStyle.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/RoundedRectangle.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/RoundedRectangle.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/RoundedRectangleOutline.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/RoundedRectangleOutline.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/RoundedRectangularShape.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/RoundedRectangularShape.kt`：逐字匹配（仅包名/import 替换）
- `sources/Kyant0-Shapes/shapes/src/commonMain/kotlin/com/kyant/shapes/UnevenRoundedRectangle.kt` → `com.linrjk.liquid/liquid-glass/src/main/kotlin/com/linrjk/liquid/shapes/UnevenRoundedRectangle.kt`：逐字匹配（仅包名/import 替换）

## 平台合并说明

- `Platform.kt`：保留 Android SDK 能力判断与 `ChecksSdkIntAtLeast`。
- `RuntimeShader.kt`：保留 KMP 2.0 自定义接口，接入 `android.graphics.RuntimeShader`。
- `internal/Paint.kt`：保留 `BlurMaskFilter` 与 Android RuntimeShader 桥接。
- `internal/RenderEffect.kt`：保留 Android RenderEffect 链、RuntimeShader 和 ColorFilter 实现。
- 未迁入任何 `skikoMain`、iOS、Desktop、JS 或 Wasm 源码。
