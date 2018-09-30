# sql exec

## SQL扩展
https://calcite.apache.org/docs/adapter.html#extending-the-parser
CREATE INDEX(indextype) index_name ON table(column_name[ ASC | DESC ] [ ,...n ])
DROP INDEX <index_name> ON table

## Calcite语法分析实现

Calcite的语法解析文件 file (Parser.jj) 用 javacc (http://javacc.java.net/)
和 Freemarker (http://freemarker.org/) 编写，Freemaker为Parser.jj提供了变量设置的功能，
通过fmpp(freemaker)的支持，可以在语法解析前，先做变量替换，然后再做代码生成

### FreeMaker

fmpp: http://fmpp.sourceforge.net

fmpp设置的值，会被替换到.jj文件中，可以通过.fmpp文件进行配置，fmpp文件是kv类型的配置文件

fmpp内置了FreeMaker的引擎，通过调用fmpp core即可使用FreeMaker和fmpp的扩展功能

### JavaCC

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

### 扩展语法的步骤

如果是已经在语法中支持的操作，如create/drop操作

1. 使用calcite项目的calcite-server
2. 修改config.fmpp（可以参考calcite-core/server）的说明，添加keyword、对应的处理函数
3. 在parserImpls.ftl/compoundIdentifier.ftl补充相应的JavaCC BNF表达式