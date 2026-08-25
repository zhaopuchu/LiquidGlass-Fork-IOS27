# 迁移验证记录

验证日期：2026-08-24

## 结论

- 45 个上游库源文件已覆盖：Backdrop commonMain 30/30、androidMain 4/4、
  Shapes 11/11；四组 common/android 文件合并后生成 41 个 Android 源文件。
- 计划中的 commonMain “26 个”是前置统计误差；当前基线目录实际为 30 个，
  本次按磁盘上的完整基线迁移，没有裁剪文件。
- 26 个无需平台合并的 Backdrop 文件和 11 个 Shapes 文件通过全文比较，
  除包名/import 替换外无差异。
- 库与示例均不包含 KMP、Skiko、Compose Multiplatform Resources 或
  `com.kyant.*` 残留。
- Maven Local 坐标 `com.linrjk:liquid-glass:2.0.0-android-SNAPSHOT`
  可被独立 consumer 工程解析和构建。

逐文件记录见 [`SOURCE_MAPPING.md`](SOURCE_MAPPING.md)。

## 自动化测试

- JVM 单元测试：8/8 通过。
  - 圆角半径边界。
  - RTL start/end 映射。
  - 连续曲率 Path Outline 类型。
  - `lerp` 端点与中点。
  - `copy`。
  - Effect scope 状态重置和低版本 no-op。
- Android 仪器测试：
  - API 28：低版本能力判断与 Blur 安全降级通过；API 31/33 用例按条件跳过。
  - API 36：RenderEffect 链、RuntimeShader 缓存、RuntimeShaderEffect 通过；
    低版本用例按条件跳过。

当前机器未安装 API 31 系统镜像，因此没有在 API 31 精确版本重复执行；
RenderEffect 路径已在 API 36 执行，低版本路径已在 API 28 执行。

## 构建与静态检查

- `:liquid-glass:assembleRelease`：通过。
- `:sample:assembleDebug`：通过。
- `:sample:assembleRelease`：通过，R8 与资源压缩已开启并成功完成。
- `:liquid-glass:lint`：通过。
- `:sample:lint`：通过。
- `:liquid-glass:publishToMavenLocal`：通过，生成 AAR、POM、
  Gradle Module Metadata 和 sources JAR。
- `consumer-test/:app:assembleDebug`：通过，只使用 Maven Local 坐标，
  不依赖工程内 `project(":liquid-glass")`。

AAR 内已包含：

- `assets/licenses/LICENSE`
- `assets/licenses/NOTICE`

## 视觉冒烟

- API 36 Catalog 首页：[`sample-smoke-api36.png`](sample-smoke-api36.png)
- API 36 Glass Playground：
  [`glass-playground-api36.png`](glass-playground-api36.png)

Glass Playground 的背景、玻璃容器、Slider 和 LiquidButton 均完成渲染。
该次运行使用无窗口软件渲染模拟器，启动帧数据不代表真机性能，不作为
色散性能结论。

## minSdk 校正

计划中的 `minSdk 21` 与 AndroidX Compose 1.11.1 不兼容：
`foundation-layout-android:1.11.1` 的 Manifest 明确要求 minSdk 23。
独立 consumer 在 minSdk 21 时由 Manifest Merger 拒绝构建。

最终使用安全且可验证的 `minSdk 23`，没有通过
`tools:overrideLibrary` 强制绕过依赖约束。API 23–30 仍保持上游的
静默降级行为；API 31+ 启用 RenderEffect，API 33+ 启用 RuntimeShader。

## 保留的上游状态

- `LayerBackdrop` transform 坐标 TODO 未修改。
- AGSL 色散采样算法未优化。
- Compose Android 桥接的 `asFrameworkPaint()` 产生弃用警告，但构建、
  测试和 lint 均通过；为保持上游 Android 实现，本次未重写。
