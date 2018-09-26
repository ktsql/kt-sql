# storage

解决分布式数据库存储层的问题

## key features

1. 分布方案
2. 高可用
3. scan, range, key查询(是否支持数据全局索引)
4. 跨行事务

1~3可由分布式存储层提供，4可由跨行事务支持(需存储层支持mvcc)

## distributed storage

高效的NoSQL存储层实现：
- MongoDB (https://github.com/igd-geo/mongomvcc)
- Couchbase
- OrientDB
- RethinkDB
- ArangoDB

Row column mixed
- HBase
- tera
- hypertable
- PaxosStore
- Pegasus
- Kudu
- TiKV

KV-store实现方案，参考：
- ssdb
- ledisdb
- ardb

### 异步的HBase客户端

提供异步接口有助于提升性能，但受限于tephra，要改进tephra才能实现全异步

AsyncHBase提供了通过ZKClient获取HBase RootRegion连接地址的方法

以下是asynchbase中的相关说明：

摘录自HBaseClient的注释

1. HBaseClient每连接只需创建一次（共享给多个协程使用）
2. 通过HBaseRpc请求HBaseClient，未完成回调前，不可修改HBaseRpc
3. Exception采用对defer进行处理的模式

摘录自RegionClient的注释
1. RegionClient并非线程安全

摘录自ZKClient的注释
1. zookeeper提供region server是否在线以及RootRegion的信息

考虑到HBase会动态扩展，所以每次表连接时获取region list才能保证拿到，
有两种方法可以保证这一点：1、监听zookeeper的变化；2、如果region client报错，重新连接

为了保证性能，现采用后者处理region list
1、schema获取region list并缓存
2、schema提供重新获取region list的接口，并初始化到table中
3、如果table发现regionclient出错，通过schema重新获取region list

## 参考链接

http://www.infoq.com/cn/articles/how-to-build-a-distributed-database
https://db-engines.com/en/ranking/document+store
https://db-engines.com/en/article/Wide+Column+Stores
https://db-engines.com/en/ranking/key-value+store
