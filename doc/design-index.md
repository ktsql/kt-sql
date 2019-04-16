# index

## 二级索引实现

1. 创建一个对应于index的hbase table，如table_name.index_name
2. 创建连接，获得table, table.index的读写权限，初始化事务读写环节
3. 写入时，先获取hbase puts的信息，创建对应的index，同时写入目标表和索引表
4. 读取时，如果是有索引的表，先根据索引读取rowkey，再读取数据

在分布式环境下，二级索引也存在节点分布（需解决CAP）、数据规模大、保存不在本地的问题

## 二级索引分类

### KV型二次索引

华为hbase二级索引的实现
https://github.com/Huawei-Hadoop/hindex.git

region start key + index name + indexed column(s) value + user table rowkey

二级索引思路：通过索引值反查RowKey，每一个索引键创建一个相应的表
http://www.infoq.com/cn/articles/hbase-second-index-engine 在rowkey包含index
https://www.jianshu.com/p/194b982571fa 利用coprocessor在每次写入数据的时候，在索引表增加索引
https://cloud.tencent.com/developer/article/1084837 一些HBase二级思路的笔记
https://www.cnblogs.com/mthoutai/p/7323316.html 一些HBase二级索引的思路，适用不同场景
https://blog.csdn.net/DPnice/article/details/81132348 一些HBase二级索引的思路，及分析
https://www.cnblogs.com/cxzdy/p/5169075.html 华为方案补充
https://wenku.baidu.com/view/e08f261058fafab068dc020a.html 华为方案详解
http://dequn.github.io/2016/08/29/HBase-Secondary-Index-Theory/ 华为方案相关资料

参考华为二级索引的方案，索引表rowkey：IndexName + IndexValue + DataTable RowKey

### BitmapInvertedIndex

Bitmap索引，把键映射成bitmap，建立bitmap和rowkey的对应关系，BitmapInvertedIndex，
则是把整个row的必要值映射成bitmap，再建立bitmap和rowkey的对应关系。
Bitmap带来的优越性：索引空间小，部分条件处理只需索引即可计算。

bitmap在以下场景，有较快的记录筛选速度：
1. 对集合求交集、并集
2. 根据特定值检索
3. 求rank
4. 集合中是否包含

https://github.com/RoaringBitmap/RoaringBitmap roaringbitmap
https://hexiaoqiao.github.io/blog/2016/11/27/exact-count-and-global-dictionary-of-apache-kylin/ kylin的bitmap
https://github.com/shunfei/sfmind/blob/master/indexr_white_paper/indexr_white_paper.md indexr的白皮书
https://blog.bcmeng.com/post/indexr.html indexr的bitmap
http://hbasefly.com/2018/06/19/timeseries-database-8/ druid的bitmap
https://hortonworks.com/blog/apache-hive-druid-part-1-3/ druid性能贴
http://druid.io/blog/2012/09/21/druid-bitmap-compression.html druid技术文
https://zhuanlan.zhihu.com/p/20119525 pinot的bitmap

实现的考虑：bitmap的全局编码信息保存在内存中，索引信息保存在存储层
bitmap的使用必须要考虑把字符值类型，映射到bitmap编码，在一个分布式环境，需要特定的实现


