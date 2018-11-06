# sql exec

## 1. SQL扩展
https://calcite.apache.org/docs/adapter.html#extending-the-parser
CREATE INDEX index_name[(indextype)] ON table(column_name[ ASC | DESC ] [ ,...n ])
DROP INDEX <index_name> ON table

### 1.1 Calcite语法分析实现

Calcite的语法解析文件 file (Parser.jj) 用 javacc (http://javacc.java.net/)
和 Freemarker (http://freemarker.org/) 编写，Freemaker为Parser.jj提供了变量设置的功能，
通过fmpp(freemaker)的支持，可以在语法解析前，先做变量替换，然后再做代码生成

#### 1.1.1 FreeMaker

fmpp: http://fmpp.sourceforge.net

fmpp设置的值，会被替换到.jj文件中，可以通过.fmpp文件进行配置，fmpp文件是kv类型的配置文件

fmpp内置了FreeMaker的引擎，通过调用fmpp core即可使用FreeMaker和fmpp的扩展功能

#### 1.1.2 JavaCC

https://www.ibm.com/developerworks/cn/xml/x-javacc/part1/index.html
https://www.ibm.com/developerworks/cn/xml/x-javacc/part2/
JavaCC .jj 文件语法和标准的 BNF 之间的主要区别在于：利用 JavaCC 版本，您可以在语法中嵌入操作。

.jj 语法构成

1. parser head
2. token描述
   - java语言体
   - token bnf描述
3. 标记符描述：<TOKEN>在.jj中有特殊含义，需要定义，并被解释

http://www.cnblogs.com/Gavin_Liu/archive/2009/03/07/1405029.html

#### 1.1.3 语法树实现、执行的说明

如果要生成CREATE TABLE的语法树，calcite的实现方法是：
1. 在calcite-core中预留了create开头的语法（这意味着如果要创建新的语法需要修改calcite-core）
2. 在calcite-server中，对create的实例进行初始化
  - 通过配置config.fmpp，完成create的后续语法的对应函数（在calcite-core定义了create后继语法为相应的SqlCreate）
  - 通过配置parserImpls.ftl，完成后续节点的语法解析，并完成JavaCC生成的parser函数与实际创建语法树节点的调用关联
3. 按parser.jj规范完成逻辑的代码实现后，生成的结果为SqlNode，SqlNode生成语法树、逻辑计划优化等环节不需关注，直接关注节点执行即可
4. 由SqlNode转换成RelNode节点后，进行RelNode Tree优化，RelNode节点再通过implement转换成binary code，由Prepare.callback负责执行

因为JavaCC并没有生成语法树这一环节，所以从解析到语法树建立这一步需要手动写代码，
在JavaCC BNF表达式中，如果碰到匹配的语法，则创建相应的语法树节点，通过深度搜索建立SqlNode嵌套关联的方式，建立语法树

### 1.2 扩展语法的步骤

如果是已经在语法中支持的操作，如create/drop操作

1. 使用calcite项目的calcite-server进行代码扩展
2. 修改config.fmpp（可以参考calcite-core/server）的例子，添加keyword，必要的对应处理函数
3. 在parserImpls.ftl/compoundIdentifier.ftl补充相应的JavaCC BNF表达式，以及对应的SqlNode创建函数
4. 拓展org.apache.calcite.sql.ddl，在SqlDdlNodes增加上一步添加的创建函数，并创建相应的SqlNode
5. 根据SqlNode的逻辑要求，实现创建的节点的相关逻辑，融入到calcite-core的执行流程中

注：
- 如果是基于calcite-core扩展的节点，在优化逻辑计划的时候，节点会有对应匹配的扩展规则，否则需要扩展calcite-core
- 如果SqlNode需要表达的元素不足，如在核心库中不支持的SqlKind，则需要通过修改calcite-core完成

## 2. SQL执行速度的提升

一个完整的SQL语句执行的流程是：
1. 解析输入流，生成语法树
2. 语法树优化，生成逻辑计划
3. 在一个运行环境中运行执行计划

因为SQL语言应用时的复杂度并不高，所以代码解析、生成、优化环节复杂度有限，
运行环节的消耗才是最耗时的环节，在一个分布式的网络环境中对大量的数据进行处理，
数据的读取、计算、汇总处理都有大量的耗时

如果把处理分布到设备中，读取、计算进行下推后，汇总处理节点则成为性能的瓶颈，
如在一个万兆的网络环境，每秒读入约1G的数据，当数据压缩且为投影数据时，
这意味着需要在秒级处理上亿行的数据，计算单元必须要具备对应的计算能力

SQL的操作可以拆分为以下几个步骤
1. Scan (FROM).
2. Join: Join two results as cross-product (JOIN).
3. LeftJoin: The algebraic rules for an outer join are so different from a Join that this needs to be a separate operator (LEFT JOIN).
4. Filter: Removing rows based on a conditional expression (WHERE, HAVING).
5. Select(Project): Evaluating new values based on a value expression list (SELECT list).
6. Aggregate: Grouping rows based on an aggregate expression list (GROUP BY, UNION). <- 这里开始由enumerable处理
7. Sort (ORDER BY).
8. Limit (LIMIT).
9. Merge: Merge two results into one (UNION ALL).

Calcite已经支持把Filter、Project分布式下推到存储节点上，但聚合计算执行的运行空间仍为单核单线程，性能有待提升。
org.apache.calcite.adapter.enumerable 有内存化计算的实现

## 3. Calcite代码笔记

### 3.1 calcite-core

calcite-core依赖以下模块：
1. calcite-linq4j
2. calcite-avatica
3. calcite-avatica-server

linq4j是底层数据查询修改操作的支持类

calcite对外调用提供了jdbc接口：
jdbc是calcite-core内置功能，在官方文档中，如何使用calcite也是以jdbc为基础介绍如何使用

avatica/avatica-server是支持网络连接的jdbc接口，calcite-core中的jdbc(本地)实现，
使用了avatica中的实现代码

注意jdbc中connection的继承关系：
abstract class CalciteConnectionImpl
    extends AvaticaConnection
    implements CalciteConnection, QueryProvider
CalciteConnectionImpl最终实现了AvaticaConnection(jdbc), CalciteConnection(calciteI), QueryProvider(linq4j)

Calcite首先会实现自己的逻辑，像Calcite-AvaticaFactory, CalciteFactory都是沿于自己的逻辑体系。
然后，通过实现jdbc的接口，实现了逻辑和jdbc接口的融合。

calcite在作为lib内嵌到其他程序中时，注册了jdbc driver(有remote和local两种模式），
当访问jdbc connection时，会访问org.apache.calcite.avatica.UnregisteredDriver的connect(url, info)函数，
(通过DriverManager.getConnection()访问org.apache.calcite.jdbc.Driver，Driver是UnregisteredDriver子类)。
connect(url,info)会调用this.factory.newConnection(UnregisteredDriver var1, AvaticaFactory var2, String var3, Properties var4)。
CalciteFactory.newConnection是abstract，如果要改变返回的Connection(包含Driver)，则需要重载该函数

CalciteConnectionImpl创建时，会生成CalciteSchema，CalciteSchema创建MetadataSchema，因为Schema是一个可传进去的参数，
所以存在在调用connect函数时，把自定义的Schema传进去的可能，只需重写自己的Driver即可

calcite对jdbc的处理提供了几种扩展机制，如子类继承、设置handler对connection、statement进行处理，
具备了一定情况下无需入侵源代码即可实现自定义逻辑的可能

CalciteConnection创建时，会完成相关元素的创建，参考调用链：AvaticaConnection<-CalciteConnectionImpl<-CalciteJdbc41Connection
最终在AvaticaConnection完成了相关元素的初始化：
```java
    this.id = UUID.randomUUID().toString();
    this.handle = new Meta.ConnectionHandle(this.id);
    this.driver = driver; // 由上层传入
    this.factory = factory; // 由上层传入
    this.url = url; // 调用参数
    this.info = info; // 调用参数
    this.meta = driver.createMeta(this);
    this.metaData = factory.newDatabaseMetaData(this);
```

### 3.2 calcite-jdbc

因为对jdbc的扩展，是实现calcite定制的关键，所以这里梳理一下calcite-jdbc的类关系

依据jdbc driver的规范，需要实现以下元素：
1. driver，用于把代码注册到Jdbc Driver中
2. connection，当Driver调用connect()时，返回
2. statement，通过connection创建

calcite-jdbc的初始化实现代码逻辑如下：
1. Driver，用于注册driver，包含CalciteFactory，当调用Driver的connect时，实际调用CalciteFactory的newConnection
2. CalciteFactory负责创建connection，因为CalciteFactory是抽象类，实际调用的是CalciteJdbc41Factory.newConnection，即创建CalciteJdbc41Connection
3. CalciteConnectionImpl是CalciteJdbc41Connection的抽象父类，因为CalciteJdbc41Connection什么都没有做，其实创建的connection就是CalciteConnectionImpl
4. CalciteConnection包含了上下文Driver, CalciteFactory, CalciteSchema, JavaTypeFactory，这些都是由Factory传过去的
5. Driver继承于UnregisteredDriver，使用了UnregisteredDriver.createFactory，createFactory创建的Factory类为CalciteJdbc41Factory，Driver改写了getFactoryClassName
6. 传给CalciteConnection的上下文中，Driver为UnregisteredDriver，Factory为CalciteJdbc41Factory，CalciteSchema和JavaTypeFactory都是null
7. CalciteConnectionImpl在初始化的时候，如果发现CalciteSchema为null，通过CalciteSchema.createRootSchema初始化Schema
8. CalciteSchema.createRootSchema根据上下文，按需创建CachingCalciteSchema或SimpleCalciteSchema，并把MetadataSchema作为子Schema添加到rootSchema
9. CalciteConnectionImpl在初始化的时候，如果发现JavaTypeFactory为null，通过创建RelDataTypeSystem.class初始化JavaTypeFactory

calcite-jdbc的表读写实现代码逻辑需要adaptor的支持，table需要实现Table、ModifiableTable对读写操作进行支持

calcite-jdbc的表创建实现代码逻辑如下(查看SqlCreateTable.execute)：
1. 在Schema中添加表的Meta信息
   1. CalciteSchema.add(new MutableArrayTable(...))
2. 在SqlDdlNodes.populate创建表
   1. 首先创建 PreparedStatement： prepare = context.getRelRunner().prepare(r.rel)
   2. 然后通过prepare.executeUpdate() 执行添加表操作, context(DataContext)由调用者触发，使用调用者的上下文
   3. AvaticaPreparedStatement提供executeUpdate()的实现，实际调用的是this.getConnection().executeQueryInternal
   4. AvaticaConnection提供了executeQueryInternal()的实现，实际调用的是Meta.execute
   5. Meta是interface，最终执行的是CalciteMetaImpl

### 3.3 Meta

在calcite中引入了几个概念：
1. schema 用于表述初始化所需的信息，A namespace for tables and functions，因其实现的原因，只能只读
2. metadata 用于sql解析的元数据信息
3. meta 用于表达SQL的接口，针对每一driver实现，官方建议都实现自己的meta

CalciteSchema根据配置文件生成，保存在单个JVM的内存中，在一个分布式的环境，
Calcite的设计决定了CalciteSchema统一保存、只读访问的属性

因为calcite实现的设计，如果要保存表结构、表索引等信息，应当采用Meta。
如果需要对Meta的信息进行控制，需要实现自己的Meta，如在分布式环境可用的Meta，可以通过继承的方式创建新的Meta实现类。
calcite-core实现了CalciteMetaImpl，并把CalciteMetaImpl作为组件，放到了MetadataSchema中。
MetadataSchema是schema的子类，在rootSchema创建时被初始化并作为subSchema添加到rootSchema中，
rootSchema在创建后，会被传到SchemaFactory中，通过该方式，可以借助Schema获取meta的信息。

CalciteMetaImpl独立创建了一个连接，用于访问Meta信息。Meta在设计的时候，考虑的是自己管理连接、逻辑、存储，
不会与数据库的请求连接捆绑在一起。这种非捆绑的设计，有助于Meta逻辑的独立。

jdbc规范中，包含两个metadata的定义：1、connection获取的metadata。2、resultset的metadata；
前者对应calcite-meta，后者对应calcite-metadata(org.apache.calcite.sql.parser.MetaData, org.apache.calcite.rel.metadata.Metadata)

因为要支持index和事务的原因，需要接管calcite的meta实现，方法为：
通过继承改写org.apache.calcite.jdbc.Driver，实现原有逻辑的子类，传一个新的CalciteSchema进去，
需要改写的类包括Driver(connect、createFactory)、AvaticaFactory(newConnection)、以及SqlSchema相关
需重新实现CalciteSchema，对MetadataSchema进行改写，实现新的MetaImpl逻辑

calcite-server CreateTable包含两个环节：1、在Schema中添加对应的KeyValue；2、执行SQL创建表
calcite-server中，CreateTable是通过在Schema中的TableMap添加一个KeyValue实现的，
这意味着沿用calcite-server，需要对相关的Meta处理进行完善。

Calcite实现了自己的DatabaseMetadata：AvaticaJdbc41DatabaseMetaData，继承自AvaticaDatabaseMetaData，
AvaticaJdbc41DatabaseMetaData由CalciteJdbc41Factory(继承CalciteFactory)创建。
Calcite DatabaseMetaData通过connection的类成员meta获取对应的metadata信息

Calcite的ResultSetMeta实现，可以参考AvaticaResultSetMetaData

依Calcite的设计思路，所有装配的工作，都在CalciteFactory完成，且调用CalciteFactory的使用者和CalciteFactory捆绑，
如果要改写，不但需要改写CalciteFactory，还需要修改CalciteFactory相关的使用者

获取table的meta时，调用CalciteMetaImpl.schemas()，获得表的相关信息：
- 获取所有的表，通过schema获取 getConnection().rootSchema.getSubSchemaMap()
- 传进去的参数为catalog，通过catalog参数筛选出目标tables

依据DatabaseMetaData的代码，只需要改写SqlSchema即可支持table/index meta
如果要运行DDL(创建表)，会进入到CalciteMetaImpl.prepareAndExecute中，再进入到CalcitePrepareImpl.prepare2_，
if (sqlNode.getKind().belongsTo(SqlKind.DDL)) 会调用SqlNode(SqlExecutableStatement).execute(),
最后是通过Schema.add把表添加到Schema中，可通过接管Schema实现自定义的

### 3.4 SQL执行

执行的入口为CalcitePrepareImpl的prepare，调用链：
statement.prepareAndExecute()->connection.prepareAndExecute()->meta.prepareAndExecute()
->connection.parseQuery()->prepareImpl.prepareSql()->prepareImpl.prepare2_()

入口为prepare2_()，对各类的SQL处理如下：
1. DDL处理，进入到executeDdl()
2. DML执行，进入到prepareSql()，先解析为PrepareStatement，然后再调用

SQL解析执行的过程为 <= 参考：Prepare.prepareSql()：
1. 解析SQL，转换成SqlNode
2. 对SqlNode进行检查（采取自顶向下的递归方式），确认每一节点的语法有效性（是否和Meta信息一致）
3. 把SqlNode转换成为RelNode，然后对RelNode进行结构优化
4. 调用adaptor的执行函数，如Prepare.implement()，返回Enumerable(此时完成外部的Scan或Modify的代码生成并返回结果)
5. 最后经过ResultSet.execute()返回enumerable，调用bindable.bind(dataContext)，触发全部的逻辑(在calcite本地计算的逻辑)

SqlNode的生成采用JavaCC BNF解析的方式生成，以嵌套语法树的方式组织。
Calcite提供了SqlToRelConverter把SqlNode转换成RelNode，以便执行计划优化器对语法树进行优化，
RelNode的结构组织也是嵌套语法树，完成优化后，由CalciteResultSet模块解释执行。解释执行的入口为execute调用。

RelNode转换成为代码（通过Expression），Expression可以通过compiler生成binary code

#### 3.4.1 Prepare.implement()

Prepare.implement()把需要执行的RelNode操作，转换成Java代码，然后通过compiler，
把代码转换成可执行的binary code，参考IClassBodyEvaluator

调用链：
EnumerableInterpretable.toBindable()->EnumerableInterpretable.getBindable()->IClassBodyEvaluator.createInstance()

比如，"insert into ${TEST_TABLE_NAME} values ('XXXX')"这样的SQL，经过RelNode的处理后，
会转换成以下的字符串，并使用Janino编译器，编译成可以执行的binary code
```java
org.apache.calcite.DataContext root;

public org.apache.calcite.linq4j.Enumerable bind(final org.apache.calcite.DataContext root0) {
  root = root0;
  final java.util.Collection collection = ((org.apache.calcite.schema.ModifiableTable) root.getRootSchema().getTable("T")).getModifiableCollection();
  final int _count = collection.size();
  org.apache.calcite.linq4j.Linq4j.asEnumerable(new String[] {
    "XXXX"}).select(new org.apache.calcite.linq4j.function.Function1() {
    public String apply(String o) {
      return o;
    }
    public Object apply(Object o) {
      return apply(
        (String) o);
    }
  }
  ).into(collection);
  final int _updatedCount = collection.size();
  return org.apache.calcite.linq4j.Linq4j.singletonEnumerable((long) (_updatedCount >= _count ? _updatedCount - _count : _count - _updatedCount));
}


public Class getElementType() {
  return long.class;
}
```

以下是Scannable查询时，对应生成的binary code
```java
org.apache.calcite.DataContext root;

public org.apache.calcite.linq4j.Enumerable bind(final org.apache.calcite.DataContext root0) {
  root = root0;
  return org.apache.calcite.schema.Schemas.queryable(
  	root, root.getRootSchema().getSubSchema("HBASE"), java.lang.Object[].class, "T").asEnumerable();
}


public Class getElementType() {
  return java.lang.String.class;
}
```

#### 3.4.2 DML操作

QUERY/INSERT/UPDATE/DELETE

插入操作的操作次序为：
1. 获得可以操作的collection列表
2. 采用linq4j的接口，把需要修改的数据放入collection
3. 返回执行代码Signature(JaninoCompiler返回)，看CalciteMetaImpl把parseQuery返回的signature
4. 对返回的代码进行调用(查看AvaticaConnection对execute的实现 line:666)
5. 使用cursor等方式产生最终的结果列表，并放到statement中返回

Result实现了对signature的操作，参考CalciteResultSet.execute()，这里算是lazy execute()，先prepare后获取数据

CalciteResultSet.execute() -> AvaticaResultSet.execute() -> CalciteMetaImpl.createIterable()
-> CalciteMetaImpl.getConnection().enumerable -> signature.enumerable() -> bindable.bind()

查询调用的入口为：
Schemas.enumerable()

```java
org.apache.calcite.DataContext root;

public org.apache.calcite.linq4j.Enumerable bind(final org.apache.calcite.DataContext root0) {
  root = root0;
  return org.apache.calcite.runtime.Enumerables.slice0(
    org.apache.calcite.schema.Schemas.enumerable(
      (org.apache.calcite.schema.FilterableTable) root.getRootSchema().getSubSchema("HBASE").getTable("T"), root));
}

public Class getElementType() {
  return java.lang.String.class;
}

```

#### 3.4.3 建表

calcite建表流程：
1. 前置检查(parse)，直接进入execute，不走convert->validate->trim->optimize->implement
2. 调用执行语句，附上下文
3. 获取将要修改的CalcitSchema
4. 获取使用到的typeFactory
5. 获取columnList，并把定义的columnList转换成实际存储的类型
6. 初始化建表所需的相关参数
7. 判断表是否存在，如果存在，报错
8. 建表，把表加到CalciteSchema

### 3.5 adaptor

数据的查询，可以通过TableScan或者ScannableTable来实现。

如果是通过TableScan的方式来实现，需要register(RelOptPlanner planner)，以下为注册相关代码代码：
```java
  planner.addRule(CsvProjectTableScanRule.INSTANCE);

  public static final CsvProjectTableScanRule INSTANCE =
      new CsvProjectTableScanRule(RelFactories.LOGICAL_BUILDER);

  /**
   * Creates a CsvProjectTableScanRule.
   *
   * @param relBuilderFactory Builder for relational expressions
   */
  public CsvProjectTableScanRule(RelBuilderFactory relBuilderFactory) {
    super(
        operand(LogicalProject.class,
            operand(CsvTableScan.class, none())),
        relBuilderFactory,
        "CsvProjectTableScanRule");
  }

   @Override public void onMatch(RelOptRuleCall call) {
     final LogicalProject project = call.rel(0);
     final CsvTableScan scan = call.rel(1);
     int[] fields = getProjectFields(project.getProjects());
     if (fields == null) {
       // Project contains expressions more complex than just field references.
       return;
     }
     call.transformTo(
         new CsvTableScan(
             scan.getCluster(),
             scan.getTable(),
             scan.csvTable,
             fields));
   }
```
在代码从SqlNode->RelNode->Node进行转换的时候，根据类型转换成对应的RelNode操作。

如果是ScannableTable，TableScanNode.create会根据传进去的上下文转换成实际的实现，
在执行TableScanNode的时候，调用其实现implement。在Validate的时候，会填充好上下文，
CalciteCatalogReader.getTable调用RelOptTableImpl.create，创建对应的实现类，
如QueryableTable或者是ScannableTable, FilterableTable...
```
	at me.principality.ktsql.backend.hbase.HBaseTable.asQueryable(HBaseTable.kt:71)
	at org.apache.calcite.schema.Schemas.queryable(Schemas.java:212)
	at Baz.bind(Unknown Source)
	at org.apache.calcite.jdbc.CalcitePrepare$CalciteSignature.enumerable(CalcitePrepare.java:356)
	at org.apache.calcite.jdbc.CalciteConnectionImpl.enumerable(CalciteConnectionImpl.java:309)
	at org.apache.calcite.jdbc.CalciteMetaImpl._createIterable(CalciteMetaImpl.java:506)
	at org.apache.calcite.jdbc.CalciteMetaImpl.createIterable(CalciteMetaImpl.java:497)
	at org.apache.calcite.avatica.AvaticaResultSet.execute(AvaticaResultSet.java:182)
	at org.apache.calcite.jdbc.CalciteResultSet.execute(CalciteResultSet.java:64)
	at org.apache.calcite.jdbc.CalciteResultSet.execute(CalciteResultSet.java:43)
	at org.apache.calcite.avatica.AvaticaConnection$1.execute(AvaticaConnection.java:667)
	at org.apache.calcite.jdbc.CalciteMetaImpl.prepareAndExecute(CalciteMetaImpl.java:566)
	at org.apache.calcite.avatica.AvaticaConnection.prepareAndExecuteInternal(AvaticaConnection.java:675)
	at org.apache.calcite.avatica.AvaticaStatement.executeInternal(AvaticaStatement.java:156)
	at org.apache.calcite.avatica.AvaticaStatement.executeQuery(AvaticaStatement.java:227)
```
先通过Schemas.queryable把Table转换成QueryableTable，再通过asEnumerable()转成enumerator

数据的插入，通过TableModify实现。通过语法分析产生的SqlInsert，会在Sql2Rel时，
调用convertQuery->convertQueryRecursive->convertInsert->createModify，
此时ModifiableTable.toModificationRel被调用，返回TableModify(jdbcTable返回LogicalTableModify)

TableModify在Prepare.prepareSql中，被转换成EnumerableTableModify，
```
EnumerableTableModify(table=[[HBASE, T]], operation=[INSERT], flattened=[false]): rowcount = 1.0, cumulative cost = {2.0 rows, 1.0 cpu, 0.0 io}, id = 17
  EnumerableValues(tuples=[[{ 'XXXX' }]]): rowcount = 1.0, cumulative cost = {1.0 rows, 1.0 cpu, 0.0 io}, id = 13
```
在EnumerableInterpretable.toBindable中，EnumerableTableModify.implement生成相应的处理代码(调用getModifiableCollection)，
并转换成binary code，由PrepareCallback.callback执行，最终调用AvaticaResultSet.execute

数据的修改，convertQuery->convertQueryRecursive->convertUpdate时，会创建LogicalTableModify，
```
LogicalTableModify(table=[[T]], operation=[UPDATE], updateColumnList=[[ROWKEY]], sourceExpressionList=[['ZZZZ']], flattened=[false])
  LogicalProject(ROWKEY=[$0], EXPR$0=['ZZZZ'])
    LogicalFilter(condition=[=($0, 'XXXX')])
      EnumerableTableScan(table=[[T]])

EnumerableTableModify(table=[[T]], operation=[UPDATE], updateColumnList=[[ROWKEY]], sourceExpressionList=[['ZZZZ']], flattened=[false]): rowcount = 15.0, cumulative cost = {145.0 rows, 231.0 cpu, 0.0 io}, id = 50
  EnumerableProject(ROWKEY=[$0], EXPR$0=['ZZZZ']): rowcount = 15.0, cumulative cost = {130.0 rows, 231.0 cpu, 0.0 io}, id = 49
    EnumerableFilter(condition=[=($0, 'XXXX')]): rowcount = 15.0, cumulative cost = {115.0 rows, 201.0 cpu, 0.0 io}, id = 48
      EnumerableTableScan(table=[[T]]): rowcount = 100.0, cumulative cost = {100.0 rows, 101.0 cpu, 0.0 io}, id = 19
```
经过optimize的处理后，转换为
```
EnumerableCalc(expr#0=[{inputs}], expr#1=['ZZZZ'], expr#2=['XXXX'], expr#3=[=($t0, $t2)], proj#0..1=[{exprs}], $condition=[$t3]): rowcount = 15.0, cumulative cost = {115.0 rows, 801.0 cpu, 0.0 io}, id = 62
  EnumerableTableScan(table=[[T]]): rowcount = 100.0, cumulative cost = {100.0 rows, 101.0 cpu, 0.0 io}, id = 19
```
