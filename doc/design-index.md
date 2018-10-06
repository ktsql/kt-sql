#index

## 二级索引

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

## 二级索引实现

1. 创建一个对应于index的hbase table，如table_name.index_name
2. 创建连接，获得table, table.index的读写权限，初始化事务读写环节（借助tephra）
3. 写入时，先获取hbase puts的信息，创建对应的index，同时写入目标表和索引表
4. 读取时，如果是有索引的表，先根据索引读取rowkey，再读取数据
