# transcation

## 跨行事务

跨行事务可以纳入到存储层

- http://tephra.incubator.apache.org/#
- xiaomi themis
- apache fluo
- atlasDB

## apache tephra

https://github.com/caskdata/tephra

选择tephra的原因：
- 后端存储层选择了HBase
- tephra是有限活跃中的percolator开源实现

tephra包含以下组件：
- 服务端：提供全局的事务服务
- 客户端：负责开始、提交、回滚事务
- 存储端扩展：扩展存储端，对MVCC特性（如snapshot isolation、删除过时的事务数据）进行支持

服务端需要独立运行，客户端jar可以被调用者以lib方式包含，存储端扩展则以jar形式配置在HBase
- 客户端需包含tephra-api, tephra-core, tephra-hbase-compat-${version}
- 服务端可部署在HBase HMaster节点上，以单独的进程运行，服务端的地址需要在客户端进行配置
- 存储端扩展的jar需要在所有的HBase节点上进行配置

tephra官网简单介绍了服务端、客户端和存储端扩展的原理，需在percolator原理的基础上，对上述组件进行理解

## percolator

Google percolator提出在MVCC基础上，实现增量索引处理事务的方法

下面两文对percolator的阐述已经比较全面透彻
http://www.cnblogs.com/foxmailed/p/3887430.html
https://wenku.baidu.com/view/c1264b3a1eb91a37f1115cbf.html

如果tephra的核心逻辑实现没有问题，percolator论文泛泛读一下即可

## 事务实现的层次

foundationdb, spanner的设计，是把事务放在存储层实现
其他常见的NoSQL，并不把跨行事务认为是存储层需要支持的功能。