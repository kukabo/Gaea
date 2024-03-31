# 构建一个项目框架

**目标**：打造一个管理项目的基础框架，屏蔽开发人员不需要关注的非业务逻辑，例如日志、异常、缓存、消息队列等

**项目分层**：
1. `common-hello`：基础的工具包、bean、entity
2. `server-hello`：服务端，直连数据库，对外接口服务
3. `service-hello`：业务逻辑代码
4. `third-hello`：缓存、消息队列等中间件
5. `web-hello`：web端，用户访问
6. `parent-hello`：管理 jar包，是所有模块的父引用