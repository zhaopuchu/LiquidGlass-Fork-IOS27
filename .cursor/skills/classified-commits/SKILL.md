---
name: classified-commits
description: 按功能分类后分别提交 git commit，消息格式为当天日期加编号功能说明。在用户说提交、分别提交、commit、git commit、把工作区改动提交时使用。
---

# 分类提交 Commit

用户要求提交工作区改动时，先分类再分别 commit。不要把无关改动塞进同一笔。

## 提交前

1. 并行查看 `git status`、`git diff`、`git log`（看近期风格，但消息格式以本 skill 为准）。
2. 按功能分类，例如：社区文档、闲置工具配置、构建工具、业务修复。一类一笔。
3. 不提交密钥（`.env`、`docker/.env`、credentials）。用户点名要提交时先警告。
4. 未要求则不 `git push`。不改 git config，不 `--no-verify`，不 force push 到 main/master。

## 消息格式（原文，不得改写）

XXXXXX-1.功能。2.功能。......

XXXXXX 的部分为提交当天的时间，示例如下 20260817，这就是一个示例。

在提交之前进行功能分类，不要杂乱无章的提交，在提交时需要写清楚提交的功能是什么、解决了什么问题、实现了什么功能、修复了什么问题等。

## 写法

- 日期用本地当天 `YYYYMMDD`，不要用对话里的旧日期。
- 同一笔里用 `1.` `2.` 列出该分类下的具体项，句号分隔。
- 每项写清：做了什么、解决了什么问题 / 实现了什么 / 修复了什么。
- 一笔一个主题。例如只删 Turborepo 就不要混进 README。

## 示例

```
20260817-1.移除上游开源社区行为准则 CODE_OF_CONDUCT.md。2.移除指向上游维护者邮箱的 SECURITY.md。解决内部二开仓库仍展示 OpenWork 对外披露渠道的问题，避免安全漏洞被误报到外部。
```

```
20260817-1.删除 turbo.json 与根依赖 turbo。2.从 pnpm-lock 与上游合并保护名单去掉 turbo。解决日常三条启动命令不使用 Turborepo 却仍安装编排工具的问题。
```

## 执行

对每一类：`git add` 仅该类文件 → `git commit`（PowerShell 用 here-string 传消息）→ 全部完成后 `git status` 确认干净，并列出各 commit。
