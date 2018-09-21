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

KV-store实现方案，参考：
- ssdb
- ledisdb
- ardb

## 跨行事务

跨行事务可以纳入到存储层

- http://tephra.incubator.apache.org/#
- xiaomi themis
- apache fluo
- atlasDB

## 参考链接

http://www.infoq.com/cn/articles/how-to-build-a-distributed-database
https://db-engines.com/en/ranking/document+store
https://db-engines.com/en/article/Wide+Column+Stores
https://db-engines.com/en/ranking/key-value+store
