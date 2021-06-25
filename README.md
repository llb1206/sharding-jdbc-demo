## SpringBoot2.0整合Sharding-Jdbc实现分库分表

当业务发展到一定程度，分库分表是一种必然的要求，分库可以实现资源隔离，分表则可以降低单表数据量，提高访问效率。因此笔者选用目前市面上使用最多的中间件Sharding-Jdbc，整合SpringBoot2.0实现分库分表。



### 1.Sharding-Jdbc介绍

#### 1.1. 简介

定位为轻量级Java框架，在Java的JDBC层提供的额外服务。 它使用客户端直连数据库，以jar包形式提供服务，无需额外部署和依赖，可理解为增强版的JDBC驱动，完全兼容JDBC和各种ORM框架。

- 适用于任何基于JDBC的ORM框架，如：JPA, Hibernate, Mybatis, Spring JDBC Template或直接使用JDBC。
- 支持任何第三方的数据库连接池，如：DBCP, C3P0, BoneCP, Druid, HikariCP等。
- 支持任意实现JDBC规范的数据库。目前支持MySQL，Oracle，SQLServer，PostgreSQL以及任何遵循SQL92标准的数据库。

![Sharding-JDBC Architecture](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-1.png)

官方文档介绍：http://shardingsphere.apache.org/index_zh.html



#### 1.2.水平分割

（1）水平分库

- 概念：以字段为依据，按照一定策略，将一个库中的数据拆分到多个库中。
- 结果：每个库的结构都一样；数据都不一样；所有库的并集是全量数据；

（2）水平分表

- 概念：以字段为依据，按照一定策略，将一个表中的数据拆分到多个表中。
- 结果：每个表的结构都一样；数据都不一样；所有表的并集是全量数据；



### 2.利用SpringBoot实现分库分表

#### 2.1.数据源设定

要分库分表首先需要有不同的数据源，因此笔者准备了两个数据库作为数据源。分别在库1和库2中执行以下创表SQL。（笔者使用的是Oracle数据源，其他数据库如MySQL修改为对应的SQL即可）

```sql
CREATE TABLE test_user_0 (
  id NUMBER(30) primary key NOT NULL,
  username VARCHAR2(225) DEFAULT NULL,
  password VARCHAR2(225) DEFAULT NULL
);

CREATE TABLE test_user_1 (
  id NUMBER(30) primary key NOT NULL,
  username VARCHAR2(225) DEFAULT NULL,
  password VARCHAR2(225) DEFAULT NULL
);
```

SQL执行后，分别查看库1和库2。

**第一个库**

![image-20201107122057903](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-2.png)

**第二个库**

![image-20201107122146077](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-3.png)



#### 2.2.pom.xml添加相关依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.calvin</groupId>
    <artifactId>sharding-jdbc-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>sharding-jdbc-demo</name>
    <description>SpringBoot2.0整合Sharding-Jdbc实现分库分表</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <ojdbc.version>12.1.0.1.0</ojdbc.version>
        <sharding-sphere.version>4.0.0-RC1</sharding-sphere.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.10</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.23</version>
        </dependency>

        <!-- 分库分表-->
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
            <version>${sharding-sphere.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>sharding-core-common</artifactId>
            <version>${sharding-sphere.version}</version>
        </dependency>
        <!-- 分库分表 end-->

        <dependency>
            <groupId>com.oracle</groupId>
            <artifactId>ojdbc7</artifactId>
            <version>${ojdbc.version}</version>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.1.2</version>
        </dependency>
    </dependencies>

    <pluginRepositories>
        <pluginRepository>
            <id>aliyun-repos</id>
            <url>https://maven.aliyun.com/repository/public</url>
        </pluginRepository>
    </pluginRepositories>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```



#### 2.3.配置数据源及分库分表策略

```yml
server:
  port: 8090

spring:
  application:
    name: sharding-jdbc-demo
  # 配置真实数据源
  shardingsphere:
    datasource:
      names: ds-master-0,ds-master-1
      # 配置第1个数据源
      ds-master-0:
        # 数据库连接池类名称
        type: com.alibaba.druid.pool.DruidDataSource
        # 数据库驱动类名
        driver-class-name: oracle.jdbc.driver.OracleDriver
        # 数据库url连接
        url: 你自己的库连接地址
        #数据库用户名
        username: 你自己的库连接用户名
        # 数据库密码
        password: 你自己的库连接密码
      # 配置第2个数据源
      ds-master-1:
        # 数据库连接池类名称
        type: com.alibaba.druid.pool.DruidDataSource
        # 数据库驱动类名
        driver-class-name: oracle.jdbc.driver.OracleDriver
        # 数据库url连接
        url: 你自己的库连接地址
        #数据库用户名
        username: 你自己的库连接用户名
        # 数据库密码
        password: 你自己的库连接密码
    # 配置 user 表规则
    sharding:
      tables:
        test_user:
          actual-data-nodes: ds-master-$->{0..1}.test_user_$->{0..1}
          # 配置分表策略 主键取模 0在0表 1在1表
          table-strategy:
            inline:
              #分片列名称
              sharding-column: id
              #分片算法行表达式
              algorithm-expression: test_user_$->{id % 2}
          # 主键策略 雪花算法
          key-generator:
            column: id
            type: SNOWFLAKE
      # 配置分库策略 主键取模0在0库 1在1库
      default-database-strategy:
        inline:
          sharding-column: id
          #分片算法行表达式
          algorithm-expression: ds-master-$->{id % 2}
    # 打开sql控制台输出日志
    props:
      sql:
        show: true

  main:
    allow-bean-definition-overriding: true

management:
  endpoints:
    web:
      exposure:
        include: '*'

mybatis-plus:
  type-aliases-package: com.calvin.sharding.pojo
  mapper-locations: classpath:mapper/*.xml
  configuration:
    jdbc-type-for-null: null
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl #开启sql日志
  global-config:
    banner: false

```



#### 2.4.代码实现

##### 2.4.1.项目整体结构

![image-20201107124219138](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-4.png)



##### 2.4.2.pojo层

```java
package com.calvin.sharding.pojo;
 
import java.io.Serializable;

public class TestUser implements Serializable ,Comparable{
 
    private static final long serialVersionUID = -1205226416664488559L;
    private Long id;
    private String username;
    private String password;
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
 
    @Override
    public int compareTo(Object o) {
        TestUser u= (TestUser) o;
        return this.id.compareTo(u.id);
    }
}
```



##### 2.4.3.dao层

```java
package com.calvin.sharding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.calvin.sharding.pojo.TestUser;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends BaseMapper<TestUser> {


}
```



##### 2.4.4.service层

```java
package com.calvin.sharding.service;

import com.calvin.sharding.pojo.TestUser;
import java.util.List;
 
public interface UserService {
 
    Integer addUser(TestUser user);
 
    List<TestUser> getUsers();

    TestUser getUser(Integer id);
 
    boolean deleteOne(Integer id);
 
}


package com.calvin.sharding.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.calvin.sharding.mapper.UserMapper;
import com.calvin.sharding.pojo.TestUser;
import com.calvin.sharding.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Integer addUser(TestUser user) {
        return userMapper.insert(user);
    }

    @Override
    public List<TestUser> getUsers() {
        return userMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public TestUser getUser(Integer id) {
        return null;
    }

    @Override
    public boolean deleteOne(Integer id) {
        int i = userMapper.deleteById(id);
        return i > 0;
    }

}
```



##### 2.4.5.controller层

```java
package com.calvin.sharding.controller;

import com.calvin.sharding.pojo.TestUser;
import com.calvin.sharding.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/getUsers")
    public Object getUsers() {
        List<TestUser> list = userService.getUsers();
        Collections.sort(list);
        return list;
    }

    @GetMapping("/getUser")
    public Object getUser(@RequestParam Integer id) {
        return userService.getUser(id);
    }

    @GetMapping("/addUsers")
    public Object add() {
        for (int i = 1; i <= 5; i++) {
            TestUser user = new TestUser();
            user.setUsername("sharding-" + (i));
            user.setPassword("pw" + i);
            long resutl = userService.addUser(user);
            logger.info("insert:" + user.toString() + " result:" + resutl);
        }
        return "添加成功";
    }

    @GetMapping("/deleteOne")
    public Object deleteOne(@RequestParam Integer id) {
        userService.deleteOne(id);
        return "删除成功！";
    }
}
```



##### 2.4.6.启动类

```java
package com.calvin.sharding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.**.mapper")
public class ShardingJdbcDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcDemoApplication.class, args);
    }

}
```



### 3.测试分库分表

#### 3.1.添加数据

由于在代码中写了for循环，添加数据，为了演示方便，直接使用GET请求。

```java
@GetMapping("/addUsers")
public Object add() {
    for (int i = 1; i <= 5; i++) {
        TestUser user = new TestUser();
        user.setUsername("sharding-" + (i));
        user.setPassword("pw" + i);
        long resutl = userService.addUser(user);
        logger.info("insert:" + user.toString() + " result:" + resutl);
    }
    return "添加成功";
}
```

浏览器访问：http://localhost:8090/addUsers

![image-20201107124535827](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-5.png)

查看IDEA控制台

```verilog
2020-11-07 12:52:35.615 [http-nio-8090-exec-1   ] INFO  o.a.c.core.ContainerBase.[Tomcat].[localhost].[/] - Initializing Spring DispatcherServlet 'dispatcherServlet'
2020-11-07 12:52:35.615 [http-nio-8090-exec-1   ] INFO  org.springframework.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'
2020-11-07 12:52:35.622 [http-nio-8090-exec-1   ] INFO  org.springframework.web.servlet.DispatcherServlet - Completed initialization in 7 ms
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6175db86] was not registered for synchronization because synchronization is not active
JDBC Connection [org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection@25caea94] will not be managed by Spring
==>  Preparing: INSERT INTO test_user ( id, username, password ) VALUES ( ?, ?, ? ) 
==> Parameters: 1324937779126198273(Long), sharding-1(String), pw1(String)
2020-11-07 12:52:37.505 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Rule Type: sharding
2020-11-07 12:52:37.506 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Logic SQL: INSERT INTO test_user  ( id,
username,
password )  VALUES  ( ?,
?,
? )
2020-11-07 12:52:37.506 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=test_user, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=id, tableName=test_user), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=test_user, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=23)], parametersIndex=3, logicSQL=null), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[id, username, password], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@326385b2, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@33798356, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@387f85cd])])
2020-11-07 12:52:37.507 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-1 ::: INSERT INTO test_user_1   (id, username, password) VALUES (?, ?, ?) ::: [1324937779126198273, sharding-1, pw1]
<==    Updates: 1
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@6175db86]
2020-11-07 12:52:37.573 [http-nio-8090-exec-1   ] INFO  com.calvin.sharding.controller.UserController - insert:User{id=1324937779126198273, username='sharding-1', password='pw1'} result:1
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2290b8fe] was not registered for synchronization because synchronization is not active
JDBC Connection [org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection@bf099b6] will not be managed by Spring
==>  Preparing: INSERT INTO test_user ( id, username, password ) VALUES ( ?, ?, ? ) 
==> Parameters: 1324937780040556545(Long), sharding-2(String), pw2(String)
2020-11-07 12:52:37.574 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Rule Type: sharding
2020-11-07 12:52:37.574 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Logic SQL: INSERT INTO test_user  ( id,
username,
password )  VALUES  ( ?,
?,
? )
2020-11-07 12:52:37.574 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=test_user, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=id, tableName=test_user), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=test_user, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=23)], parametersIndex=3, logicSQL=null), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[id, username, password], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@326385b2, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@33798356, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@387f85cd])])
2020-11-07 12:52:37.574 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-1 ::: INSERT INTO test_user_1   (id, username, password) VALUES (?, ?, ?) ::: [1324937780040556545, sharding-2, pw2]
<==    Updates: 1
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2290b8fe]
2020-11-07 12:52:37.620 [http-nio-8090-exec-1   ] INFO  com.calvin.sharding.controller.UserController - insert:User{id=1324937780040556545, username='sharding-2', password='pw2'} result:1
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@62d41134] was not registered for synchronization because synchronization is not active
JDBC Connection [org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection@45a402d8] will not be managed by Spring
==>  Preparing: INSERT INTO test_user ( id, username, password ) VALUES ( ?, ?, ? ) 
==> Parameters: 1324937780241883138(Long), sharding-3(String), pw3(String)
2020-11-07 12:52:37.621 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Rule Type: sharding
2020-11-07 12:52:37.621 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Logic SQL: INSERT INTO test_user  ( id,
username,
password )  VALUES  ( ?,
?,
? )
2020-11-07 12:52:37.621 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=test_user, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=id, tableName=test_user), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=test_user, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=23)], parametersIndex=3, logicSQL=null), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[id, username, password], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@326385b2, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@33798356, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@387f85cd])])
2020-11-07 12:52:37.622 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-0 ::: INSERT INTO test_user_0   (id, username, password) VALUES (?, ?, ?) ::: [1324937780241883138, sharding-3, pw3]
<==    Updates: 1
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@62d41134]
2020-11-07 12:52:37.669 [http-nio-8090-exec-1   ] INFO  com.calvin.sharding.controller.UserController - insert:User{id=1324937780241883138, username='sharding-3', password='pw3'} result:1
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@35d3cbd9] was not registered for synchronization because synchronization is not active
JDBC Connection [org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection@774c31e6] will not be managed by Spring
==>  Preparing: INSERT INTO test_user ( id, username, password ) VALUES ( ?, ?, ? ) 
==> Parameters: 1324937780447404034(Long), sharding-4(String), pw4(String)
2020-11-07 12:52:37.671 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Rule Type: sharding
2020-11-07 12:52:37.671 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Logic SQL: INSERT INTO test_user  ( id,
username,
password )  VALUES  ( ?,
?,
? )
2020-11-07 12:52:37.671 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=test_user, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=id, tableName=test_user), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=test_user, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=23)], parametersIndex=3, logicSQL=null), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[id, username, password], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@326385b2, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@33798356, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@387f85cd])])
2020-11-07 12:52:37.671 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-0 ::: INSERT INTO test_user_0   (id, username, password) VALUES (?, ?, ?) ::: [1324937780447404034, sharding-4, pw4]
<==    Updates: 1
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@35d3cbd9]
2020-11-07 12:52:37.717 [http-nio-8090-exec-1   ] INFO  com.calvin.sharding.controller.UserController - insert:User{id=1324937780447404034, username='sharding-4', password='pw4'} result:1
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@68002a33] was not registered for synchronization because synchronization is not active
JDBC Connection [org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection@43a6a9e9] will not be managed by Spring
==>  Preparing: INSERT INTO test_user ( id, username, password ) VALUES ( ?, ?, ? ) 
==> Parameters: 1324937780648730625(Long), sharding-5(String), pw5(String)
2020-11-07 12:52:37.718 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Rule Type: sharding
2020-11-07 12:52:37.718 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Logic SQL: INSERT INTO test_user  ( id,
username,
password )  VALUES  ( ?,
?,
? )
2020-11-07 12:52:37.718 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - SQLStatement: InsertStatement(super=DMLStatement(super=AbstractSQLStatement(type=DML, tables=Tables(tables=[Table(name=test_user, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[AndCondition(conditions=[Condition(column=Column(name=id, tableName=test_user), operator=EQUAL, compareOperator=null, positionValueMap={}, positionIndexMap={0=0})])])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=test_user, quoteCharacter=NONE, schemaNameLength=0), SQLToken(startIndex=23)], parametersIndex=3, logicSQL=null), deleteStatement=false, updateTableAlias={}, updateColumnValues={}, whereStartIndex=0, whereStopIndex=0, whereParameterStartIndex=0, whereParameterEndIndex=0), columnNames=[id, username, password], values=[InsertValue(columnValues=[org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@326385b2, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@33798356, org.apache.shardingsphere.core.parse.old.parser.expression.SQLPlaceholderExpression@387f85cd])])
2020-11-07 12:52:37.718 [http-nio-8090-exec-1   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-1 ::: INSERT INTO test_user_1   (id, username, password) VALUES (?, ?, ?) ::: [1324937780648730625, sharding-5, pw5]
<==    Updates: 1
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@68002a33]
2020-11-07 12:52:37.765 [http-nio-8090-exec-1   ] INFO  com.calvin.sharding.controller.UserController - insert:User{id=1324937780648730625, username='sharding-5', password='pw5'} result:1
```

可以看到，根据雪花算法计算出来的id取模后，会路由到对应的数据库及表。

**库1：test_user_0**   2条数据

![image-20201107125450026](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-6.png)

**库1：test_user_1**   0条数据

![image-20201107125601306](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-7.png)

**库2：test_user_0**   0条数据

![image-20201107125641138](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-8.png)

**库2：test_user_1**   3条数据

![image-20201107125745898](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-9.png)

可以看到，5条数据分别落在不同的库不同的表中，总数正确。



#### 3.2.查询数据

浏览器访问：http://localhost:8090/getUsers

![image-20201107130203681](https://ecblog.oss-cn-hangzhou.aliyuncs.com/blog/blog-10.png)

查看IDEA控制台

```verilog
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@652705ab] was not registered for synchronization because synchronization is not active
JDBC Connection [org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection@3890e0e8] will not be managed by Spring
==>  Preparing: SELECT id,username,password FROM test_user 
==> Parameters: 
2020-11-07 13:01:38.299 [http-nio-8090-exec-4   ] INFO  ShardingSphere-SQL - Rule Type: sharding
2020-11-07 13:01:38.299 [http-nio-8090-exec-4   ] INFO  ShardingSphere-SQL - Logic SQL: SELECT  id,username,password  FROM test_user
2020-11-07 13:01:38.299 [http-nio-8090-exec-4   ] INFO  ShardingSphere-SQL - SQLStatement: SelectStatement(super=DQLStatement(super=AbstractSQLStatement(type=DQL, tables=Tables(tables=[Table(name=test_user, alias=Optional.absent())]), routeConditions=Conditions(orCondition=OrCondition(andConditions=[])), encryptConditions=Conditions(orCondition=OrCondition(andConditions=[])), sqlTokens=[TableToken(tableName=test_user, quoteCharacter=NONE, schemaNameLength=0)], parametersIndex=0, logicSQL=null)), containStar=false, firstSelectItemStartIndex=0, selectListStopIndex=30, groupByLastIndex=0, items=[CommonSelectItem(expression=id, alias=Optional.absent()), CommonSelectItem(expression=username, alias=Optional.absent()), CommonSelectItem(expression=password, alias=Optional.absent())], groupByItems=[], orderByItems=[], limit=null, subqueryStatement=null, subqueryStatements=[], subqueryConditions=[])
2020-11-07 13:01:38.299 [http-nio-8090-exec-4   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-0 ::: SELECT  id,username,password  FROM test_user_0
2020-11-07 13:01:38.299 [http-nio-8090-exec-4   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-0 ::: SELECT  id,username,password  FROM test_user_1
2020-11-07 13:01:38.299 [http-nio-8090-exec-4   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-1 ::: SELECT  id,username,password  FROM test_user_0
2020-11-07 13:01:38.300 [http-nio-8090-exec-4   ] INFO  ShardingSphere-SQL - Actual SQL: ds-master-1 ::: SELECT  id,username,password  FROM test_user_1
<==    Columns: ID, USERNAME, PASSWORD
<==        Row: 1324937780241883138, sharding-3, pw3
<==        Row: 1324937780447404034, sharding-4, pw4
<==        Row: 1324937780648730625, sharding-5, pw5
<==        Row: 1324937779126198273, sharding-1, pw1
<==        Row: 1324937780040556545, sharding-2, pw2
<==      Total: 5
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@652705ab]
```

可以看到，查询出来的数据是两个库的4张表数据合集，数据正确。



#### 3.3.总结

其实，Sharding-Jdbc通过这种可插拔的组件思想，实现了我们的分库分表需求。至于项目中的增删改查的写法跟以前没有区别，说明该中间件是无感知的。

项目源码：https://gitee.com/calvin1993/sharding-jdbc-demo