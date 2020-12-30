# 基于阿里云的DTS封装
最近公司需要应用订阅阿里云RDS相关的binlog，[基于阿里云提供的案例subscribe_exampleale演化而来](https://github.com/LioRoger/subscribe_example)，重构成了SpringBoot、并且升级了相应的jar包，避免了很多版本上面带来的问题，还新增集成了新的客户端kafka、后续会考虑redis等等。

功能点:
- binlog格式化
- 数据过滤
- 集成客户端kafka消息发送
- 监控规则
- 位点回滚: 回到指定时间点重新开始

后续还会加强的功能点:

1. 消费位点的更新(目前位点是基于kafka同步,可以做到容灾切换)
2. 消息流转的监控
   1. 消费情况
      - 延迟
      - 丢失
      - 重复消费
   2. 业务数据处理情况
      1. 表
      2. 操作类型

由于DTS集成的kafka是基于单分区的，所以同一时刻只能有一个消费者消费（也就是自身自带的消费者），在某些情况可能会产生消息堆积，导致消费延迟(如果业务直接基于dts单个消费者开发的话)，这里只是将消费过来的消息进行格式化转换之后，直接放入一个全新的kafka，交给业务方自己去消费。避免binlog消费能力下降。

## 项目结构

- config : 配置类
- listener: 所有binlog消费到的消息都会被监听处理并且消费
- sender: 消息发送
- DtsServerApplication: 启动类

**其他的基本和案例保持一致**

> 另外可能需要看一下application.yml配置,将你的应用的一些环境配置上去. 如果涉及到环境切分的话

> KafkaConfiguration代码中有一个环境变量的配置: dev  如果了解环境切分的可以新增一个application-dev.yml的环境里面配置ssl相关的参数

如果启动时候，发现数据没进来，请查看配置`spring.dts.include-data-info`是否关注了该数据，又或者在`logback.xml`文件中将日志级别调整至DEBUG，但是可能会产生大量刷屏日志，这个在调试的时候注意一下。

## 代码流程介绍

`DtsServerApplication` : 启动整个应用

`StartCallback`: Spring的容器构建完成之后回调该方法，启动dts的运行环境。(dts-kafka的构建，以及一些位点线程、找对应消费者的流程初始化

以上环境基本初始化完成。

`RecordConsumerListener` : binlog回调类。

`AbstractEventProcess`: 事件处理类，包括关注事件、消息格式化、业务处理、消息发送等等

- `DDLEventProcess` : 针对表的DDL操作进行回调处理
- `DataEventProcess`: 针对表数据的增删改查回调处理

### binlog格式化模版

#### 增删改数据
参考类: `com.elab.data.dts.model.DMLData`
```json
{
"changeFieldList":[                                // 修改的字段名
        "updated"
    ],
    "databaseName":"databaseName",                  // 数据库名称
    "fieldDataMap":{
        "id":{
            "dataType":"java.lang.Integer",        // 数据类型
            "field":"id",                          // 字段名称
            "oldValue":"502941",                   // 老的字段
            "value":"502941"                       // 当前字段
        },
        "updated":{
            "dataType":"java.util.Date",
            "field":"updated",
            "oldValue":"2020-10-16 11:30:06",
            "value":"2020-10-16 11:35:06"
        }
    },
    "operation":"UPDATE",                         // 操作类型 UPDATE、INSERT、DELETE等等
    "sourceTimestamp":1602819306,                 // 触发时间戳
    "tableName":"table_info"                      // 表名
}
```

#### 表结构变化语句
参考类: `com.elab.data.dts.model.DDLData`
```json
{
    "databaseName":"库名",                                               // 数据库名称
    "operation":"DDL",                                                  // 表示操作类型
    "sourceTimestamp":1605582383,                                       // 数据产生时间戳
    "sql":"alter table 表名 modify 字段名 varchar(100) COMMENT '描述'",	// 具体的执行SQL
    "tableName":"表名"                                                   // 表名
}
```

如果有需要可以在这个基础上进行二次开发，节省更多时间。


### 注意的点

`use-config-checkpoint-name`: 为true的情况,非第一次才会参考initial-checkpoint-name的时间戳为主,达到位点偏移到具体的时间戳

```yaml
spring:
  dts:
    initial-checkpoint-name: 1596440543    # 注意这里是秒的时间戳，为空的话默认是当前时间戳。第一次启动的时候会参考这个时间戳，后续配合use-config-checkpoint-name属性以文件或者kafka的存储位点为主,
    use-config-checkpoint-name: false      # 强制使用当前位点，这里使用的时候要特别注意，不是非得回滚到指定位点，不要用true，否则重启的时候会重复消费，通常用来回到特定时间点的数据进行消费
    subscribe-mode-name: subscribe         # subscribe表示多机主备,如果有多台,只有其中一台会消费,其他只是等待这个消费挂掉,后续补上,起到容灾作用
    max-poll-records: 100
    include-data-info:          # 数据过滤  包含数据信息
      marketing_db_prod: [ all,abc ]  # marketing_db_prod : 对应的库名    [ all,abc ] 对应的表名 : all 代表所有表, abc 代表具体的表名
      # 如果有多个的话可以继续添加例如(marketing_db_prod2: [ all,abc ])
    exclude-table-change-field: # 过滤掉binlog中发生改变的字段
      all: [updated,updator]    # 过滤掉所有表的发生改变的字段
      c_user_info: [ updated ]  # 过滤掉指定表的发生改变的字段
    table-partition-map:        # 表和分区进行绑定
      content_materials_label_rlat_info: 6
```
**后续还会将一些dts使用心得分享处理,会持续更新.**

**欢迎大家一起交流**