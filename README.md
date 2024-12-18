# GG-mybatis-generator
自定义的 mybatis generator  

特点：
- 可以在 Java 代码调用 Mapper `select` 方法时指定要 SELECT 的列
- 可以在 Java 代码调用 Mapper `update` 方法时指定要 SET 为 `NULL` 的列
- 可以在 Java 代码调用 Mapper `select`、`update`、`delete`、`count` 方法时通过创建 Where 对象指定 WHERE 条件
- 在 Java 代码调用 Mapper `select`、`update`、`delete`、`count` 方法时无需关心软删除字段，仅需在生成 Java 代码、Mapper xml 代码时指定表的软删除属性即可
- 可以指定每个表生成的代码的目录

## 整体工作流程

![图1](https://github.com/zhong15/GG-mybatis-generator/blob/main/arch.png?raw=true)

## 工程介绍

- gg-mybatis-generator-common
  - 公共的依赖，包含最核心的一个接口 Where
- gg-mybatis-generator-example
  - 使用范例：生成 mybatis 相关 Java 代码和 xml 文件
  - 入口是 src/test/java 的 gg.mybatis.generator.example.MyGenerator
  - 配置是 src/test/resources 的 generatorConfig.xml
- gg-mybatis-generator-runner
  - 使用 mybatis-generator 和 freemarker 生成自定义的 mybatis 相关类和 xml
  - 使用时 Maven 依赖 scope 设置为 `test` 即可
  - freemarker 用于生成 xml，模版在 src/main/resources 的 tpl 目录下
  - 配置模版是 src/main/resources 的 generatorConfig.xml.example

## 使用示例

### 1、添加 Maven 依赖

```xml
<dependency>
    <groupId>zhong</groupId>
    <artifactId>gg-mybatis-generator-common</artifactId>
    <version>${revision}</version>
</dependency>

<dependency>
    <groupId>zhong</groupId>
    <artifactId>gg-mybatis-generator-runner</artifactId>
    <version>${revision}</version>
    <scope>test</scope>
</dependency>

<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>${version.mysql-connector-java}</version>
</dependency>
```

### 2、在 src/test/java 目录下新建 MyGenerator 类
```java
import gg.mybatis.generator.runner.Gen;

public class MyGenerator {

    public static void main(String[] args) {
        Gen.run();
    }
}
```

### 3、在 src/test/resources 目录下新建文件 generatorConfig.xml

文件模版位于 gg-mybatis-generator-runner 工程的 src/main/resources/generatorConfig.xml.example，具体配置可根据中文注释修改

### 4、生成文件

配置 src/main/java/resources/application.yml 的 MySQL 选项，运行 MyGenerator.main(String[]args) 方法

### 5、使用生成的 Java Mapper 方法示例

```java
// SELECT a.id, a.nickname
// ...
// WHERE id >= ? AND (nickname LIKE ? OR nickname LIKE ?)
// ORDER BY nickname ASC, id DESC
// LIMIT ?, ?
//
// 1(Long), %abc%(String), %abc%(String), 0(Long), 10(Integer)
List<User> userList = userMapper.selectByWherePageIdIn(
        SqlUtils.select(User.ColumnEnum.id, User.ColumnEnum.nickname),
        new DefaultWhere()
                .col(User.ColumnEnum.id.toString()).gte(1L)
                .and()
                .open()
                    .col(User.ColumnEnum.nickname.toString()).like("%abc%")
                    .or(User.ColumnEnum.nickname.toString()).like("%abc%")
                .close(),
        SqlUtils.orderBy(User.ColumnEnum.nickname.toString(), SqlUtils.ASC,
                User.ColumnEnum.id.toString(), SqlUtils.DESC),
        SqlUtils.offset(pageNum, pageSize),
        pageSize);

// DELETE FROM
// ...
// WHERE id >= ? AND (nickname LIKE ? OR nickname LIKE ?)
//
// 1(Long), %abc%(String), %abc%(String)
int rows = userMapper.deleteByWhere(
        new DefaultWhere()
                // 必须先调用 .withoutParamAnnotation()
                .withoutParamAnnotation()
                .col(User.ColumnEnum.id.toString()).gte(1L)
                .and()
                .open()
                    .col(User.ColumnEnum.nickname.toString()).like("%abc%")
                    .or(User.ColumnEnum.nickname.toString()).like("%abc%")
                .close()
);
```

## 生成文件示例

### 生成的 Java Model 介绍

```java
/**
 * 用户表
 * <p>
 * t_user
 */
public class User extends BaseEntity implements java.io.Serializable {
    // 数据库表字段，命名规则：字段名

    // 序列化版本号

    // get set 方法

    // 通用方法

    @Override
    public String toString() { ... }

    @Override
    public boolean equals(Object that) { ... }

    @Override
    public int hashCode() { ... }

    @Override
    public void clear() { ... }

    // 数据库表字段常量枚举类

    public enum ColumnEnum {
      /**
       * 主键，类型：Long
       */
      id,
      ...
      ;

      public static boolean isColumn(String column) { ... }
    }
}
```

### 生成的 Java Mapper 方法介绍

```java
/**
 * 用户表 Mapper
 * <p>
 * t_user
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

### 生成的 Mapper xml 介绍
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="gg.mybatis.generator.example.user.mapper.UserMapper">
  <resultMap id="BaseResultMap" type="gg.mybatis.generator.example.user.model.User">
    ...
  </resultMap>
  <insert id="insertSelective" parameterType="gg.mybatis.generator.example.user.model.User">
    <selectKey keyProperty="id" order="AFTER" resultType="java.lang.Long">
      SELECT LAST_INSERT_ID()
    </selectKey>
    ...
  </insert>
  <sql id="sqlWhere">
    ...
  </sql>
  <sql id="sqlWhere2">
    ...
  </sql>
  <delete id="deleteById" parameterType="java.lang.Long">
    ...
  </delete>
  <delete id="deleteByIdList" parameterType="java.util.List">
    ...
  </delete>
  <delete id="deleteByWhere" parameterType="gg.mybatis.generator.common.sql.Where">
    ...
    <include refid="sqlWhere" />
  </delete>
  <update id="updateById">
    ...
  </update>
  <update id="updateByWhere">
    ...
    <include refid="sqlWhere2" />
  </update>
  <update id="updateColumnValueById">
    ...
  </update>
  <select id="selectById" resultMap="BaseResultMap">
    ...
  </select>
  <select id="selectByIdList" resultMap="BaseResultMap">
    ...
  </select>
  <select id="selectByWhere" resultMap="BaseResultMap">
    ...
    <include refid="sqlWhere2" />
    ...
  </select>
  <select id="selectByWherePageIdIn" resultMap="BaseResultMap">
    ...
    <include refid="sqlWhere2" />
    ...
  </select>
  <select id="countByWhere" resultType="java.lang.Long">
    ...
    <include refid="sqlWhere2" />
  </select>
</mapper>
```
