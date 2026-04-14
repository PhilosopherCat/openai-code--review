# AI 代码评审平台

基于 DeepSeek AI 的自动化代码评审工具。

## 技术架构

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   前端 Vue  │ ──► │  后端 Java  │ ──► │  DeepSeek   │
│   :3000     │     │   :8091     │     │    API      │
└─────────────┘     └─────────────┘     └─────────────┘
                          │
                          ▼
                    ┌─────────────┐
                    │   GitHub    │ (可选：记录评审日志)
                    └─────────────┘
```

## 快速开始

### 方式一：Docker 一键启动（推荐）

1. 克隆项目
```bash
git clone <你的仓库地址>
cd <项目目录>
```

2. 复制并配置环境变量
```bash
cp .env.example .env
# 编辑 .env 文件，填入你的配置
```

3. 一键启动
```bash
docker-compose up -d
```

4. 访问 http://localhost:3000

### 方式二：本地开发

1. 配置环境变量
```bash
export DEEPSEEK_APIHOST="https://api.deepseek.com/v1/chat/completions"
export DEEPSEEK_APIKEYSECRET="your-api-key"
# ... 其他变量见 .env.example
```

2. 启动后端
```bash
mvn spring-boot:run -pl openai-code-review-test
```

3. 启动前端（新终端）
```bash
cd frontend && npm install && npm run dev
```

4. 访问 http://localhost:3000

## 功能特性

- ✅ AI 智能代码评审
- ✅ 代码评分 (0-100)
- ✅ Markdown 格式结果展示
- ✅ 评审记录自动保存
- ✅ 微信/邮件通知（可选）
- ✅ Docker 容器化部署
- ✅ GitHub Actions CI/CD

## 环境变量说明

| 变量名 | 必须 | 说明 |
|--------|------|------|
| `DEEPSEEK_APIHOST` | ✅ | DeepSeek API 地址 |
| `DEEPSEEK_APIKEYSECRET` | ✅ | DeepSeek API Key |
| `GITHUB_REVIEW_LOG_URI` | ❌ | 评审日志仓库 |
| `GITHUB_TOKEN` | ❌ | GitHub 访问令牌 |
| `WEIXIN_*` | ❌ | 微信通知配置 |

## GitHub Actions

推送代码到 `main` 分支自动触发构建：

```yaml
# .github/workflows/deploy.yml
on:
  push:
    branches: [ main ]
```

## 项目结构

```
.
├── frontend/              # Vue 3 前端
│   ├── src/
│   ├── Dockerfile
│   └── nginx.conf
├── openai-code-review-test/    # Spring Boot 后端
├── openai-code-review-sdk/     # SDK 核心模块
├── .env.example          # 环境变量模板
├── docker-compose.yml    # Docker 编排
├── Dockerfile            # 后端镜像
└── README.md
```

## 比赛/演示亮点

1. **前后端分离架构**：Spring Boot + Vue 3
2. **容器化部署**：Docker + Docker Compose
3. **CI/CD 自动化**：GitHub Actions
4. **AI 能力集成**：DeepSeek 大模型
5. **完整工作流**：提交 → 评审 → 记录 → 通知
