
# DESIGN

## 模块

### 协议

支持mysql原生协议
https://dev.mysql.com/doc/dev/mysql-server/latest/PAGE_PROTOCOL.html
https://mariadb.com/kb/en/library/clientserver-protocol/

### 后端

实现对后端数据存储层的操作，calcite不负责实现后端多存储节点的处理，
该实现由后端提供

后端的选择是多样的，暂选择HBase作为后端的存储层，主要考虑的因素：
1. 自带分布方案
2. 高可用
3. mvcc支持

作为后端分布式Hash的存储层，有多个可选方案，后续根据情况进一步优化

### 工具

各种支撑的工具类，及周边功能
- 分布式事务？
- 如果后端不支持类SQL查询怎么办？

## 架构

### 单节点

1. 可分布
每一个节点的处理为无状态模式，可通过负载均衡分摊到不同节点上。
事务处理需要保存上下文，

2. 高性能
采用异步回调的方式实现高性能处理，需要采用异步的方式对后端存储层进行处理。
现Vert.x支持的后端为：Mongo, Redis, MySQL, Postgres, JDBC

### 多节点

暂不考虑
