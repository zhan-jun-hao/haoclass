# AI客服联调手册

## 1. 启动 Qdrant

```bash
cd D:\haoclass\haoclass-server\devops
docker compose up -d qdrant
```

Qdrant 端口：

- HTTP：6333
- gRPC：6334

## 2. 创建向量集合

当前默认使用阿里云百炼的 `text-embedding-v4` 作为向量模型。

经验建议：Qdrant 集合的 `vectors.size` 必须和向量模型实际输出维度一致。你在百炼控制台切换 embedding 模型后，要重新确认模型维度；如果维度变了，就新建集合，或者删除旧集合后重建。

```http
PUT http://localhost:6333/collections/haoclass_customer_service
Content-Type: application/json

{
  "vectors": {
    "size": 1024,
    "distance": "Cosine"
  }
}
```

如果你实际使用的 `text-embedding-v4` 维度不是 1024，就把上面的 `size` 改成百炼文档或控制台显示的维度。

## 3. 配置百炼模型环境变量

`ai-service` 走 OpenAI 兼容协议，所以切到阿里云百炼主要改这几项配置。

Windows PowerShell：

```powershell
$env:DASHSCOPE_API_KEY="你的百炼API Key"
$env:AI_CHAT_BASE_URL="https://dashscope.aliyuncs.com/compatible-mode/v1"
$env:AI_CHAT_MODEL_NAME="qwen3.7-plus"
$env:AI_EMBEDDING_BASE_URL="https://dashscope.aliyuncs.com/compatible-mode/v1"
$env:AI_EMBEDDING_MODEL_NAME="text-embedding-v4"
```

如果你的百炼控制台提示使用带 `WorkspaceId` 的专属地址，就把两个 `BASE_URL` 都改成控制台给你的地址，例如：

```powershell
$env:AI_CHAT_BASE_URL="https://你的WorkspaceId.cn-beijing.maas.aliyuncs.com/compatible-mode/v1"
$env:AI_EMBEDDING_BASE_URL="https://你的WorkspaceId.cn-beijing.maas.aliyuncs.com/compatible-mode/v1"
```

老师傅提醒你一句：`qwen3.7-plus` 是对话模型，用来回答问题；`text-embedding-v4` 是向量模型，用来把知识库和用户问题变成向量。RAG 项目里这两个模型通常是分开的。

## 4. 启动 ai-service

如果你只是想单独启动 `ai-service`，不经过 gateway 联调，可以使用 `local` 环境：

```bash
cd D:\haoclass\haoclass-server
mvn -pl ai-service -Dspring-boot.run.profiles=local spring-boot:run
```

`local` 环境会关闭 Nacos 注册，避免本地没有启动 Nacos 时服务直接失败。

如果你要经过 gateway 联调，就需要先启动 Nacos，并使用默认配置启动 `ai-service`。

## 5. Gateway 路由

Nacos 里的 `gateway-service.yaml` 需要增加：

```yaml
- id: ai-service
  uri: lb://ai-service
  predicates:
    - Path=/api/ai/client/**,/api/ai/admin/**
```

## 6. 知识入库

```http
POST http://localhost:10010/api/ai/admin/knowledge/ingest
Authorization: Bearer 管理员token
Content-Type: application/json

{
  "title": "退款规则",
  "source": "产品设计书",
  "content": "用户支付成功后可以申请退款。退款成功后，课程订单会变成已退款，课程权益会被回收，课程销量会减少。"
}
```

## 7. 客服提问

```http
POST http://localhost:10010/api/ai/client/chat
Authorization: Bearer 用户token
Content-Type: application/json

{
  "question": "退款成功后我还能继续看课程吗？",
  "topK": 5
}
```

## 8. 当前限制

- 第一版没有保存会话记录。
- 第一版没有 MySQL 知识库原文表。
- 第一版 AI 只回答问题，不直接执行退款、发券、取消订单等业务动作。
