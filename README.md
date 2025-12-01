# 构建一个项目框架

**目标**：打造一个管理项目的基础框架，屏蔽开发人员不需要关注的非业务逻辑，例如日志、异常、缓存、消息队列等

## 项目分层  
### 1.`common-hello`：基础的工具包  
#### 核心类：manager和utils；
#### 其他都是测试验证
### 2.`server-hello`：服务端，直连数据库，对外接口服务
### 3.`service-hello`：业务逻辑代码
### 4.`third-hello`：缓存、消息队列等中间件
### 5.`web-hello`：web端，用户访问
### 6.`gaea-pom-manager`：管理 jar包，是所有模块的父引用



sequenceDiagram
participant User as 用户
participant Server as MCP Server<br/>(例如：weather)
participant Client as MCP Client<br/>(例如：Cline)
participant LLM as 大模型 (LLM)

    rect rgb(191, 223, 255)
    note right of Server: 连接和注册
    Client->>Server: 你好！我是 Client
    Server->>Client: 你好，我是weather
    Client->>Server: 你有哪些tools？
    Server->>Client: 我有get_forecast, get_alerts
    end

    Note over Client: 掌握了weather所有信息<br>（方法、参数、入参类型等）

    User->>Client: 深圳天气怎么样？

    Client-->>LLM: 问题（深圳天气怎么样） + <br>可用工具列表（weather所有信息）
    LLM-->>Client: thinking + 调用 get_forecast<br>(入参数city="深圳"，类型=字符)

    Client->>Server: 执行 get_forecast
    Server->>Client: 调用完，返回结果{"气温": 18, "天气": "晴朗"}

    Client-->>LLM: 结果是{"气温": 18, "天气": "晴朗"}
    LLM-->>Client: 深圳天晴，气温18度+ <br>结束标识（attempt_completion）

    Client->>User: 深圳天晴，气温18度。
