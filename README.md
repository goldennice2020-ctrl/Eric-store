# EarthOL / 地球 OL

「地球 OL」是一个离线优先的现实人生游戏化操作系统。它不是普通 Todo App，而是把生存状态、任务、项目、属性、天赋、世界规则、攻略、随机事件和人生档案组织成一个个人 RPG 存档。

## 运行

1. 用 Android Studio 打开当前目录。
2. 等待 Gradle Sync 完成。
3. 选择 `app` 配置，点击 Run 安装到手机或模拟器。

## 打包 APK

```bash
/Users/eric/.gradle/wrapper/dists/gradle-9.3.1-bin/23ovyewtku6u96viwx3xl3oks/gradle-9.3.1/bin/gradle assembleDebug
```

生成文件：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 清空数据重新初始化

开发阶段已启用 Room `fallbackToDestructiveMigration(false)`。如需重新初始化默认数据：

```bash
/Users/eric/Library/Android/sdk/platform-tools/adb shell pm clear com.golden.earthol
```

再次启动 App 后，会重新写入 `InitialData.kt` 中的默认玩家、状态、属性、天赋、项目、任务、资产、地点、世界规则、攻略、隐藏任务、随机事件和档案。

## 当前模块

- 首页：人生驾驶舱、今日策略、生存状态、Debuff、主线任务、项目进度。
- 任务：今日、主线、日常、支线、Boss、隐藏任务，并带任务攻略弹窗。
- 角色：角色总览、属性面板、天赋树、玩家流派。
- 世界：世界观、世界规则、人生阶段、容器地图、随机事件、隐藏任务。
- 攻略：总攻略和各模块攻略。
- 档案：项目、资产、地点、人生记录、年度日志说明。

## 完整设定文档动态导出系统

档案页提供核心功能「完整设定文档」。它会在导出瞬间实时读取当前 Room 数据库，动态生成《地球OL完整设定文档》，不是安装包内置静态模板，也不会读取旧缓存。

导出原则：

- App 当前是什么样，导出的文档就是什么样。
- 用户新增、修改、删除内容后，下一次导出会立即反映最新状态。
- 世界观、价值观、系统说明进入 `setting_entries` 统一设定源。
- 世界规则、攻略、属性、状态、任务、项目、资产、地图、人生阶段、隐藏任务、随机事件、Buff/Debuff、人生档案、用户资料库内容都从 Room 读取。
- 导入的世界观、价值观、攻略、长期理念、隐藏规则和其他长期文本进入 `library_contents`，会自动汇入完整设定文档。

导出内容覆盖：

- 世界观设定、核心价值观、游戏核心理念、地球 OL 规则。
- 总攻略、各模块攻略、用户新增设定、用户修改后的长期文本。
- 属性系统、状态系统、任务系统、关系系统、金钱系统、时间系统、地图系统、运气系统、身体状态、情绪系统、成就系统。
- 人生阶段、文明/阶层理解、Buff/Debuff、隐藏任务、随机事件、人生记录。
- 所有模块字段说明、系统参数解释、App 当前所有可导出的文本数据。

导出格式：

```text
地球OL_完整设定文档.md
地球OL_完整设定文档.txt
```

Android 端能力：

- 保存 Markdown 到 Downloads。
- 保存 TXT 到 Downloads。
- 系统分享，可分享到微信、文件管理器或其他支持文本文件的应用。
- 复制完整文档全文到剪贴板。
- 成功提示“完整设定文档已导出”，失败会显示明确错误原因。

新增可导出模块的方法：

1. 新模块的数据进入 Room 或统一设定源，不要只写在页面硬编码里。
2. DAO 提供实时 Flow 和 `getAll()` 快照读取。
3. 在 `SettingDocumentSnapshot` 和 `GameRepository.currentSettingDocumentSnapshot()` 中接入该模块。
4. 在 `SettingDocumentGenerator` 的模块章节或字段目录中注册该模块。
5. 页面继续从 Repository/Room 读取，导出也从同一数据源读取。

## 本地数据库系统

地球 OL 使用 Android Room + SQLite + Kotlin + MVVM + Repository 模式。App 不接云端，不需要登录，所有核心数据都在本地持久化。

核心架构：

```text
Compose UI
  ↓
ViewModel
  ↓
GameRepository
  ↓
DAO
  ↓
Room / SQLite
```

UI 不直接操作数据库。新增页面必须通过 ViewModel 调 Repository，Repository 再调用 DAO。

当前数据库：

- `AppDatabase`：统一 Room 数据库入口。
- `data/entity/`：Entity 层。
- `data/dao/`：DAO 层。
- `GameRepository`：Repository 层。
- 各页面 `*ViewModel`：MVVM 状态和行为层。

标准化核心表：

- Player：玩家基础资料。
- Task：任务系统。
- Event：事件系统。
- Skill：技能/能力系统。
- Relationship：关系系统。
- Inventory：物品/资产库存。
- WorldSetting：世界设定。
- Journal：日志/人生记录。
- Guide：攻略。
- AiMemory：AI 本地记忆。

每张标准化核心表都包含：

```text
id
createdAt
updatedAt
```

迁移机制：

- Room 使用显式 Migration，不使用云端。
- 当前项目已有历史数据库版本，因此 AppDatabase 继续从既有版本向前迁移。
- 标准化世界存档 JSON 的 `schemaVersion` 从 `1` 开始，便于未来 Supabase 或其他云同步层兼容。

JSON 导入导出：

- 档案页支持「导出世界存档 JSON」。
- 档案页支持「导入世界存档 JSON」恢复 Player、Task、Event、Skill、Relationship、Inventory、WorldSetting、Journal、Guide、AiMemory。
- 旧的资料库导入、玩家层导出、知识层导出仍保留。

示例代码：

- 示例 Entity：`AiMemoryEntity`
- 示例 DAO：`AiMemoryDao`
- 示例 Repository 方法：`GameRepository.upsertAiMemory`
- 示例 ViewModel：`DatabaseSampleViewModel`

## 全局 AI 攻略按钮

每个界面右下角都有「攻略」按钮。点击后可以选择：

- DeepSeek：适合普通建议、总结、整理和成本敏感的高频询问。
- ChatGPT：适合复杂判断、架构、方案、风险和决策建议。

AI 攻略会通过 `AiAdvisorViewModel` 调用 `GameRepository.currentSettingDocumentSnapshot()`，实时读取 Room 数据库里的完整数据，再由 `SettingDocumentGenerator` 生成当前上下文。也就是说它看到的是 App 当前数据库状态，包括玩家、任务、事件、技能、关系、物品、世界设定、日志、攻略、AI 记忆、资料库和完整设定文档数据。

数据流仍然遵守 MVVM：

```text
攻略按钮 / 弹窗
  ↓
AiAdvisorViewModel
  ↓
GameRepository
  ↓
Room 数据库快照
  ↓
DeepSeek / ChatGPT
```

API Key 只保存在手机本地 SharedPreferences，不接云端、不需要登录。

## 后续扩展

- 给生存状态增加每日编辑入口。
- 给随机事件增加选择和结算。
- 给隐藏任务增加触发规则。
- 给档案增加手动记录功能。
- 给天赋树增加父子节点视觉层级。
