# Xbatis AI Agent 知识库

> 官方站点：https://xbatis.cn  
> DeepWiki 文档：https://deepwiki.xbatis.cn

面向 AI Agent 的 xbatis 框架知识库，整合官方中文资料中的核心信息，提供自足的说明，帮助自动化助手理解框架能力、常见模式与关键 API。

---

## 1. 框架总览

- **定位**：xbatis 基于 MyBatis，实现高度 ORM 化的数据库操作体验，强调“少写 SQL、链路化 DSL、跨数据库兼容”。
- **主要优势**：
  - 多表关联、子查询、链路分页、自动 SQL 优化（自动移除冗余 `LEFT JOIN`、`ORDER BY`，智能 `COUNT` 化简）。
  - 内置 `RETURNING` 支持、批量插入与批量更新链路、原生函数包装、SQL 模板。
  - 单 Mapper 模式，支持一个 `BasicMapper` 横跨全部实体。
  - 多数据库函数库、数据库差异化 `dbAdapt`、动态数据源路由。
  - 功能完备的注解生态：逻辑删除、多租户、乐观锁、结果映射、条件映射、动态填充值等。
- **核心特征**（源于框架特点说明）：
  1. 极致轻量：对 MyBatis 仅做封装而非侵入式改造。
  2. 高性能：保持接近手写 SQL 的执行效率。
  3. 灵活易用：链式 API 接近自然语言，学习成本低。
  4. 高可用：可覆盖超过 90% 常见 SQL 场景。
  5. 可靠安全：设计简洁但 API 丰富，稳健通过大量测试。
  6. 分页优化：自动优化 `JOIN`、`COUNT`、`ORDER BY`，内置分页器能力。

---

## 2. 核心模块与包路径

| 模块 | 典型包路径 | 核心类型 | 作用 |
| --- | --- | --- | --- |
| 核心 Mapper | `cn.xbatis.core.mybatis.mapper` | `MybatisMapper<T>`, `BasicMapper` | 提供基础 CRUD 与单 Mapper 能力 |
| 链式 DSL | `cn.xbatis.core.chain` | `QueryChain`, `InsertChain`, `UpdateChain`, `DeleteChain` | 构建复杂 SQL、批量操作、返回值链路 |
| 全局配置 | `cn.xbatis.core.config` | `XbatisGlobalConfig` | 统一配置命名规则、拦截器、动态值、分页等 |
| 注解体系 | `cn.xbatis.db.annotations` | `@Table`, `@TableId`, `@TableField`, `@LogicDelete`, `@TenantId`, `@Version`, `@Condition`, `@Fetch` 等 | 实体映射、注入规则、对象条件、结果加工等 |
| 数据库函数 | `db.sql.api.impl.cmd` | `Methods` | 提供跨库函数、SQL 模板、函数链式包装 |
| 多租户 | `cn.xbatis.core.tenant` | `TenantContext`, `TenantId` | 全局租户 ID 注册与透传 |
| 动态数据源 | `cn.xbatis.datasource.routing` | `@DS`, `JdbcConfigDecryptor` | 运行时切换数据源、加密配置、分组路由 |
| 逻辑删除 | `cn.xbatis.core.logic` | `LogicDeleteSwitch`, `LogicDeleteUtil` | 动态开关与便捷关闭逻辑删除 |
| 动态值 | `cn.xbatis.core.dynamic` | `XbatisGlobalConfig#setDynamicValue` | 定义 `{NOW}`、`{TODAY}` 等动态填充值 |
| 代码生成 | `cn.xbatis.codegen` | `GeneratorConfig` 及子配置 | 一站式生成实体、Mapper、Service 等骨架 |

---

## 3. 快速入门与依赖

### 3.1 Maven 坐标示例
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>cn.xbatis</groupId>
      <artifactId>xbatis-bom</artifactId>
      <version>1.9.2-RC5</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>cn.xbatis</groupId>
    <artifactId>xbatis-spring-boot3-starter</artifactId>
  </dependency>
</dependencies>
```

### 3.2 数据源配置示例
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dbName
    username: dbusername
    password: dbpassword
```

### 3.3 启动骨架示例
```java
@SpringBootApplication
@MapperScan("com.xx.xxx.mapper")
public class XbatisApplication {
    public static void main(String[] args) {
        SpringApplication.run(XbatisApplication.class, args);
    }
}
```

### 3.4 Spring Boot 2 集成

- **Maven 依赖**（使用 `xbatis-spring-boot-starter`）：
  ```xml
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>cn.xbatis</groupId>
        <artifactId>xbatis-spring-boot-parent</artifactId>
        <version>1.9.2-RC5</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>cn.xbatis</groupId>
      <artifactId>xbatis-spring-boot-starter</artifactId>
    </dependency>
  </dependencies>
  ```
- **数据源与 Mapper 扫描**：配置同 3.2/3.3；Spring Boot 2 下可继续使用 `application.yml` 与 `@MapperScan`。
- **启动类**：与 3.3 相同，确保使用 `xbatis-spring-boot-starter` 版本与 Spring Boot 2 兼容。

### 3.5 Solon 集成

- **Maven 依赖**：
  ```xml
  <!-- 注意顺序：先引入 xbatis 插件，再引入 mybatis-solon-plugin -->
  <dependency>
    <groupId>cn.xbatis</groupId>
    <artifactId>xbatis-solon-plugin</artifactId>
    <version>1.9.2-RC5</version>
  </dependency>
  <dependency>
    <groupId>org.noear</groupId>
    <artifactId>mybatis-solon-plugin</artifactId>
    <version>${mybatis.solon.version}</version>
  </dependency>
  ```
- **配置示例**：
  ```yaml
  # solon.yml
  ds:
    schema: demo        # 建议与数据库名一致
    jdbcUrl: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456

  # 规则：mybatis.数据源Bean名称
  mybatis.master:
    mappers:
      - "com.demo.mapper"           # 包扫描
      - "classpath:mapper/**/*.xml" # XML 扫描
  ```
- **数据源 Bean**：
  ```java
  @Configuration
  public class MybatisConfig {

      @Bean(name = "master", typed = true)
      public DataSource dataSource(@Inject("${ds}") HikariDataSource ds) {
          return ds;
      }
  }
  ```
- **业务使用**：
  ```java
  @Controller
  public class DemoController {

      @Db            // 单数据源可省略名称，多数据源需指定 @Db("master")
      UserMapper mapper;

      @Get
      @Mapping("/test")
      public List<User> test() {
          return QueryChain.of(mapper)
                  .like(User::getName, "abc")
                  .list();
      }
  }
  ```
- 更多配置与 `mybatis-solon-plugin` 保持一致，可继续使用链式 DSL、分表、多租户等特性。
实体类、Mapper、Service 的典型写法：
```java
@Data
@Table
public class SysUser {
    @TableId
    private Integer id;
    private String userName;
    private String password;
    private Integer roleId;
    private LocalDateTime createTime;
}

public interface SysUserMapper extends MybatisMapper<SysUser> {}

@Service
public class TestService {
    @Autowired
    private SysUserMapper sysUserMapper;

    public Pager<SysUser> demo() {
        return QueryChain.of(sysUserMapper)
                .eq(SysUser::getId, 1)
                .like(SysUser::getUserName, "xxx")
                .paging(Pager.of(1, 10));
    }
}

> 分页器 `Pager` 位于 `cn.xbatis.core.mybatis.mapper.context.Pager`，实现接口 `cn.xbatis.page.IPager`，常用构造与方法包括：
> - 静态工厂：`Pager.of(size)`、`Pager.of(number, size)`。
> - `setExecuteCount(boolean)`：控制是否执行总量统计。
> - `paging(Pager)`：链式 API 传入 `Pager`，结果中可通过 `getResults()`、`getTotal()`、`getNumber()`、`getSize()`、`getTotalPage()` 等方法读取信息。
> - `PagerGetSetUtil` 支持按照 `PagerField` 动态读写扩展字段。
```

---

## 4. 全局配置 `XbatisGlobalConfig`（`cn.xbatis.core.config.XbatisGlobalConfig`）

- 可在 Spring 启动阶段通过 `ConfigurationCustomizer` 或 `@PostConstruct` 提前设置。
- 常用方法：
  - `setSingleMapperClass(MybatisBasicMapper.class)`：开启单 Mapper 模式，与 `BasicMapper` 配合。
  - `setTableUnderline(boolean)` / `setColumnUnderline(boolean)`：控制表名、列名的下划线策略。
  - `setDatabaseCaseRule(DatabaseCaseRule rule)` 或 `setDatabaseCaseRule(DbType, rule)`：设定大小写风格。
  - `setDynamicValue("{KEY}", (clazz, type) -> {...})` 与 `getDynamicValue(clazz, type, key)`：挂载并调用动态填充值。
  - `setLogicDeleteInterceptor((entity, update) -> {...})` 与 `setLogicDeleteSwitch(boolean)`：统一处理逻辑删除附加字段与开关。
  - `addMapperMethodInterceptor(new MyInterceptor())`、`enableInterceptOfficialMapperMethod()`：注册自定义 Mapper 方法拦截器，并可扩展至框架默认方法。
  - `setPagingProcessor(DbType, PagingProcessor)`：自定义不同数据库的分页处理。

---

## 5. 注解体系与属性说明

### 5.1 `@Table`（`cn.xbatis.db.annotations.Table`）

| 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `value` | 是 | – | 表名，默认按实体名驼峰转下划线，可覆盖 |
| `schema` | 是 | – | 指定数据库 schema |
| `columnNameRule` | 是 | `columnNameRule.IGNORE` | 定义列名规则，未显式配置列名时按规则生成 |
| `databaseCaseRule` | 是 | `DatabaseCaseRule.DEFAULT` | 控制大小写，可结合 `XbatisGlobalConfig.setDatabaseCaseRule(...)` 全局/分库设置 |

示例：
```java
@Table(databaseCaseRule = DatabaseCaseRule.UPPERCASE)
public class SysUser { }
```

### 5.2 `@TableId`（`cn.xbatis.db.annotations.TableId`）

| 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `value` | 是 | `IdAutoType.AUTO` | 主键策略，支持 `AUTO`, `NONE`, `SQL`, `GENERATOR` |
| `dbType` | 是 | – | 目标数据库类型，便于多库差异化配置 |
| `sql` | 是 | – | `value = SQL` 时必填，自定义取号语句 |
| `generatorName` | 是 | – | `value = GENERATOR` 时必填，对应注册的 ID 生成器 |

可重复标注适配多种数据库的主键策略。

### 5.3 `@TableField`（`cn.xbatis.db.annotations.TableField`）

| 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `value` | 是 | – | 列名，遵循驼峰转下划线可省略 |
| `select` | 是 | `true` | `select(Entity.class)` 时是否参与查询 |
| `insert` | 是 | `true` | `save` 时是否写入 |
| `update` | 是 | `true` | `update` 时是否更新 |
| `jdbcType` | 是 | – | 指定 JDBC 类型 |
| `typeHandler` | 是 | – | 指定类型处理器 |
| `defaultValue` | 是 | – | 插入默认值，支持静态与动态（如 `{NOW}`） |
| `updateDefaultValue` | 是 | – | 更新默认值 |
| `defaultValueFillAlways` | 是 | `false` | 插入时是否始终填充 `defaultValue` |
| `updateDefaultValueFillAlways` | 是 | `false` | 更新时是否始终填充 `updateDefaultValue` |
| `exists` | 是 | `true` | 是否真实存在于表中，`false` 可用于非持久化字段 |

### 5.4 `@LogicDelete`（`cn.xbatis.db.annotations.LogicDelete`）

| 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `beforeValue` | 是 | – | 删除前的标识值；为空视为 `NULL` |
| `afterValue` | 否 | – | 删除后的标识值，可使用动态值 `{NOW}` 等 |
| `deleteTimeField` | 是 | – | 删除时间字段名，支持 `LocalDateTime`、`Date`、`Long`(毫秒)、`Integer`(秒) |

结合 `XbatisGlobalConfig.setLogicDeleteInterceptor` 可额外填充删除人等字段。

### 5.5 `@TenantId`（`cn.xbatis.db.annotations.TenantId`）

无额外属性。用于标记实体字段为租户 ID，框架在 `save/update/delete` 时自动填充，并在查询条件中追加租户约束。通过 `TenantContext.registerTenantGetter`（`cn.xbatis.core.tenant.TenantContext`）指定租户来源。

### 5.6 `@Version`（`cn.xbatis.db.annotations.Version`）

无额外属性。标记字段为乐观锁版本，`save` 自动置 0，`update/delete` 自动附加 `WHERE version = ?` 条件。

### 5.7 生命周期注解：`@OnInsert`（`cn.xbatis.db.annotations.OnInsert`）、`@OnUpdate`（`cn.xbatis.db.annotations.OnUpdate`）

| 注解 | 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| `@OnInsert` | `value` | 是 | – | 指定插入前监听器，实现 `OnInsertListener<T>` |
| `@OnUpdate` | `value` | 是 | – | 指定更新前监听器，实现 `OnUpdateListener<T>` |

除局部监听外，可通过 `XbatisGlobalConfig.setGlobalOnInsertListener`、`setGlobalOnUpdateListener` 注册全局处理。

### 5.8 结果映射注解（`@ResultEntity`/`@ResultField`/`@Fetch` 等均位于 `cn.xbatis.db.annotations` 包）

- `@ResultEntity`：声明 VO 与实体间的映射关系，自动补充 `SELECT` 列。
- `@ResultField`

  | 属性 | 是否可空 | 默认值 | 说明 |
  | --- | --- | --- | --- |
  | `value` | 是 | – | 可指定一个或多个列名，支持从多个列取值 |
  | `jdbcType` | 是 | – | 指定 JDBC 类型 |
  | `typeHandler` | 是 | – | 指定类型处理器 |

- `@Fetch`

  | 属性 | 是否可空 | 默认值 | 说明 |
  | --- | --- | --- | --- |
  | `column` | 是 | – | 指定列作为匹配条件；与 `property` 二选一，优先 `column` |
  | `property` | 否 | – | 源实体属性名 |
  | `source` | 否 | – | 源实体类型 |
  | `storey` | 是 | `-1` | 源实体层级，默认自动推断 |
  | `target` | 否 | – | 目标实体（被拉取的表） |
  | `targetProperty` | 否 | – | 目标实体关联列 |
  | `targetSelectProperty` | 是 | – | 指定返回列，可为单列或动态表达式 |
  | `middle` | 否 | – | 中间实体（中间表） |
  | `middleSourceProperty` | 否 | – | 中间实体与源实体关联列 |
  | `middleTargetProperty` | 否 | – | 中间实体与目标实体关联列 |
  | `orderBy` | 是 | – | 排序字段，支持 `"[{xx} desc]"` 或 `"field desc"` 等写法 |
  | `multiValueErrorIgnore` | 是 | `false` | 当期望 1 对 1 却返回多条时是否忽略异常 |
  | `limit` | 是 | `0` | 限制返回条数，0 表示不限制 |
  | `memoryLimit` | 是 | `false` | 是否在内存中执行 `limit`，配合 `IN` 减少查询次数 |
  | `nullFillValue` | 是 | `null` | 当结果为空时的填充值 |
  | `otherConditions` | 是 | – | 额外条件，格式如 `[{Type} = 2]` |

- **AI 生成建议**：推荐直接使用 `@ResultEntity` 及其系列注解（`@ResultField`、`@NestedResultEntity`、`@NestedResultEntityField`、`@ResultCalcField`、`@Fetch`、`@PutEnumValue`、`@PutValue` 等）定义 VO，作为查询结果返回，可省去二次转换逻辑并保持结构稳定。注意这些注解仅适用于 VO/DTO，禁止标注在实体类上；类似的 `@TypeHandler`, `@Put***` 注解也应限定在返回对象中，以避免污染实体模型。

- `@NestedResultEntity`（`cn.xbatis.db.annotations.NestedResultEntity`）  
  | 属性 | 是否可空 | 默认值 | 说明 |
  | --- | --- | --- | --- |
  | `target` | 是 | – | 对应的实体类，不填时默认取 `@ResultEntity` 指定的实体 |
  | `storey` | 是 | `1` | 存储层级；自连接场景用于区分不同层次 |
  将 VO 中的字段声明为另一个对象（实体或 VO），框架会自动在查询结果中填充嵌套结构，可继续递归嵌套。

- `@NestedResultEntityField`（`cn.xbatis.db.annotations.NestedResultEntityField`）  
  | 属性 | 是否可空 | 默认值 | 说明 |
  | --- | --- | --- | --- |
  | `value` | 是 | – | 指定嵌套实体的字段名 |
  当 VO 字段名与实体字段名不同，可在嵌套 VO 上逐字段标注，实现别名映射。常与 Lombok `@FieldNameConstants` 配合避免硬编码。

- `@ResultCalcField`（`cn.xbatis.db.annotations.ResultCalcField`）  
  | 属性 | 是否可空 | 默认值 | 说明 |
  | --- | --- | --- | --- |
  | `value` | 否 | – | 计算 SQL，如 `count(1)`、`sum({id})` |
  | `target` | 是 | – | 对应实体类，缺省继承 `@ResultEntity` |
  | `storey` | 是 | `1` | 存储层级 |
  在 `select(VO.class)` 时自动生成聚合列，无需手写 SQL，动态列请使用 `{字段名}` 占位。

- `@PutEnumValue`（`cn.xbatis.db.annotations.PutEnumValue`）  
  | 属性 | 是否可空 | 默认值 | 说明 |
  | --- | --- | --- | --- |
  | `source` | 否 | – | 对应实体类 |
  | `property` | 否 | – | 实体字段名（枚举编码列） |
  | `storey` | 是 | `1` | 存储层级 |
  | `target` | 否 | – | 枚举类 |
  | `code` | 否 | `code` | 枚举中表示编码的字段名 |
  | `value` | 否 | `name` | 枚举中表示显示值的字段名 |
  | `required` | 否 | `false` | 找不到枚举是否报错 |
  | `defaultValue` | 是 | – | 找不到枚举时的默认值 |
  适合“状态码自动转名称”等场景，支持多层嵌套与多列匹配。

- `@PutValue`（`cn.xbatis.db.annotations.PutValue`）  
  | 属性 | 是否可空 | 默认值 | 说明 |
  | --- | --- | --- | --- |
  | `source` | 否 | – | 对应实体类 |
  | `property` | 否 | – | 实体字段名 |
  | `storey` | 是 | `1` | 存储层级 |
  | `factory` | 否 | – | 工厂类，需实现指定静态方法 |
  | `method` | 否 | – | 工厂中提供值的方法名 |
  | `required` | 否 | `false` | 找不到值是否报错 |
  | `defaultValue` | 是 | – | 默认值 |
  支持基于多个字段注入动态值，方法返回结果会按照 `factory+method+参数` 做 session 级缓存。

上述注解可组合使用：`@ResultEntity` 定义整体映射、`@NestedResultEntity` 构建嵌套结构、`@ResultCalcField` 生成聚合列、`@PutEnumValue`/`@PutValue` 注入附加信息，从而在 VO 中获得接近实体的自动装配体验。

### 5.9 分表注解（`@SplitTable`、`@SplitTableKey`、`TableSplitter` 均位于 `cn.xbatis.db.annotations` 包）

- `@SplitTable`

  | 属性 | 是否可空 | 默认值 | 说明 |
  | --- | --- | --- | --- |
  | `value` | 否 | – | 指定 `TableSplitter` 实现类，用来根据分表键计算真实表名 |

- `@SplitTableKey`：标记实体中的分表字段，仅支持单列，用于在运行时传入分片值。

`TableSplitter` 接口核心方法：

```java
boolean support(Class<?> type);
String split(String sourceTableName, Object splitValue);
```

`support` 用于声明支持的分片键类型，`split` 根据原始表名和分片值生成真实表名（例如 `sys_user_0`~`sys_user_9`）。查询或更新时需包含分表键条件，框架才能定位具体表。

### 5.10 条件注解组合

#### `@ConditionTarget`（`cn.xbatis.db.annotations.ConditionTarget`）

| 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `value` | 否 | – | 目标实体类，未指定时默认取当前类 |
| `logic` | 是 | `Logic.AND` | 全局默认逻辑，可切换为 `Logic.OR` 实现顶层 OR 组合 |

#### `@Condition`（`cn.xbatis.db.annotations.Condition`）

| 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `value` | 是 | `Condition.Type.EQ` | 条件类型，如 `LIKE`、`GT`、`BETWEEN`、`EXISTS` 等 |
| `target` | 是 | – | 目标实体类，默认继承 `@ConditionTarget` 或当前类 |
| `property` | 是 | – | 目标实体属性名，默认与字段同名 |
| `storey` | 是 | – | 源实体存储层级，用于嵌套对象 |
| `likeMode` | 是 | – | `LEFT`、`RIGHT`、`BOTH` 等模糊匹配模式 |
| `toEndDayTime` | 是 | `false` | 当类型为 `LTE` 或 `BETWEEN` 第二个参数时，自动补齐到当天末秒 |
| `defaultValue` | 是 | – | 默认值，可为基础类型或动态键（如 `{NOW}`、`{TODAY}`、自定义值） |

#### `@Conditions`（`cn.xbatis.db.annotations.Conditions`）

| 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `value` | 是 | – | 多个 `@Condition` 组合，常用于关键字多列搜索 |
| `logic` | 是 | `Logic.OR` | 组合条件间的逻辑运算符 |

#### `@ConditionGroup`（`cn.xbatis.db.annotations.ConditionGroup`）

| 属性 | 是否可空 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `value` | 是 | – | 同组字段集合 |
| `logic` | 是 | `Logic.AND` | 组内条件的逻辑关系，可改为 `Logic.OR` |

DTO 若实现 `ObjectConditionLifeCycle`（`cn.xbatis.core.sql.ObjectConditionLifeCycle`），可在 `beforeBuildCondition()` 中做预处理（如根据 `timeType` 注入近 7 天区间），与上述注解协作构建多层嵌套条件。

---

---

## 6. Mapper 基础能力

`MybatisMapper<T>`（`cn.xbatis.core.mybatis.mapper.MybatisMapper`）默认提供：

- **查询**：`get`, `list`, `listAll`, `listByIds`, `cursor`, `exists`, `count`, `mapWithKey`, `page` 等，可直接接收 Lambda `Where` 闭包。
- **新增**：`save`, `saveOrUpdate`, `saveBatch`, `saveModel`, `saveModelBatch`, `saveBatch(list, fields)` 等，支持实体和 `Model`。
- **更新**：`update`, `updateBatch`, `update(model, where)`, `update(list, forceFields)`, `saveOrUpdate`，并支持指定强制更新字段和批量 `CASE WHEN` 更新。
- **删除**：`deleteById`, `deleteByIds`, `delete(entity)`, `delete(where)`, `deleteAll`，以及 `DeleteChain`。
- **单 Mapper 模式**（适合实体众多、希望统一数据访问层的场景）：
  1. **用户自己定义 MybatisBasicMapper Mapper 接口和 继承 BasicMapper **  
     ```java
     public interface MybatisBasicMapper extends BasicMapper {
     }
     ```
     `BasicMapper` 位于 `cn.xbatis.core.mybatis.mapper.BasicMapper`，封装所有通用 CRUD。
  2. **配置扫描**（Spring Boot 示例）  
     ```java
     @MapperScan(basePackageClasses = MybatisBasicMapper.class,
                 markerInterface = BasicMapper.class)
     ```
     这样只生成一个 Mapper Bean，避免为每个实体单独建接口。
  3. **注册全局单 Mapper**  
     在启动阶段执行 `XbatisGlobalConfig.setSingleMapperClass(MybatisBasicMapper.class);`，告知框架以该 Mapper 作为默认入口。
  4. **业务层使用方式**  
     ```java
     @Autowired
     private MybatisBasicMapper mybatisBasicMapper;

     public void demo() {
         // 实体 CRUD
         mybatisBasicMapper.save(new SysUser());
         mybatisBasicMapper.deleteById(SysUser.class, 1);

         // 链式 DSL：需显式传入实体 Class
         QueryChain.of(mybatisBasicMapper, SysUser.class)
                 .eq(SysUser::getId, 1)
                 .list();
         UpdateChain.of(mybatisBasicMapper, SysUser.class)
                 .set(SysUser::getUserName, "basic")
                 .eq(SysUser::getId, 1)
                 .execute();
     }
     ```
     结合 `BasicDaoImpl`（位于 `cn.xbatis.core.mvc.impl.BasicDaoImpl`）可继续保持三层架构。
  5. **处理复杂 SQL**  
     单 Mapper 仍可配合 XML，通过命名空间 `xxx.MybatisBasicMapper` 与 `withSqlSession` 执行自定义语句：
     ```java
     List<SysRole> roles = mybatisBasicMapper.withSqlSession(
         SysRole.class,
         "selectByIds",
         params,
         (statement, p, sqlSession) -> sqlSession.selectList(statement, p)
     );
     ```
  6. **注意事项**  
     - DSL 需显式提供实体 Class，才能推断表名/字段。
     - 若仍需局部自定义 Mapper，可与单 Mapper 并存，但建议统一规范。
     - XML 中的 `<select id="EntityName:method">` 命名需与 `withSqlSession` 调用保持一致。

---

### 6.1 新增/批量新增冲突处理

xbatis 的 `save`/`saveBatch`/`InsertChain` 支持跨数据库的重复键策略，允许在主键或唯一约束冲突时选择“忽略”或“更新”。

| 数据库       | 重复时忽略 | 重复时修改 |
|-------------|------------|------------|
| MySQL       | ✅          | ✅          |
| MariaDB     | ✅          | ✅          |
| Oracle      | ✅          | ❌          |
| H2          | ✅          | ✅          |
| PostgreSQL  | ✅          | ✅          |
| Kingbase    | ✅          | ✅          |
| SQLite      | ✅          | ✅          |
| openGauss   | ✅          | ✅          |

`onConflict` 接口位于 `cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy`/`SaveBatchStrategy` 以及链式 SQL 的 `db.sql.api.cmd.executor.IInsert`，内部基于 `IConflictAction` 构建数据库兼容语句。

#### 单条写入

```java
MultiPk entity = new MultiPk();
entity.setId1(1);
entity.setId2(2);
entity.setName("init");

mapper.save(entity, strategy -> {
    strategy.onConflict(action -> action.doNothing()); // 忽略
    // 或者针对冲突进行更新（覆盖全部字段）
    // strategy.onConflict(action -> action.doUpdate(update -> update.overwriteAll()));
});
```

#### 多条写入

```java
List<MultiPk> list = List.of(entity1, entity2);

// mapper.save(List<T>, ...)
mapper.save(list, strategy ->
        strategy.onConflict(action -> action.doUpdate(update -> update.overwrite(MultiPk::getName)))
);

// mapper.saveBatch(List<T>, ...)
mapper.saveBatch(list, strategy ->
        strategy.onConflict(action -> action.doNothing())
);
```

`overwriteAll()` 表示更新全部列；也可以通过 `overwrite(Entity::getField)` 精准控制更新列。

#### InsertChain 冲突策略

```java
InsertChain.of(sysUserMapper)
    .insert(SysUser.class)
    .values(Arrays.asList("basic", "123456"))
    .onConflict(action -> action.doUpdate(update -> update.overwrite(SysUser::getPassword)))
    .execute();
```

如果需要按数据库类型自定义冲突行为，可结合 `dbAdapt` 在策略内做差异化处理。

---

## 7. 链式 DSL

### 7.1 QueryChain

链式查询引擎 `cn.xbatis.core.chain.QueryChain`，可自由组合 `select`, `from`, `join`, `where`, `groupBy`, `having`, `orderBy`、分页、嵌套条件、子查询等。

- **AI 生成建议**：在自动生成的业务代码中，应优先使用 xbatis 内置的条件忽略能力（如 `.forSearch(true)` 或 `eq(field, value, predicate)`或 `eq(boolean,field, value)`）替代手写 `if/else` 去拼接 `WHERE` 条件，既能保持代码整洁，又可避免遗漏校验。

典型片段：
```java
SysUserRoleVo vo = QueryChain.of(sysUserMapper)
    .select(SysUser.class, SysRole.class)
    .from(SysUser.class)
    .join(SysUser::getRoleId, SysRole::getId)
    .eq(SysUser::getId, 1)
    .like(SysUser::getUserName, "abc")
    .groupBy(SysUser::getId)
    .having(SysUser::getId, c -> c.count().gt(0))
    .orderBy(SysUser::getId)
    .returnType(SysUserRoleVo.class)
    .get();
```

关键能力：
- `select` 支持实体、VO、Lambda 字段、函数包装、忽略字段。
- `join` 支持 `inner`, `left`, `right` 多种模式，可利用 `@ForeignKey` 自动识别关联条件。
- `andNested` / `orNested` 可构造括号条件；`and()` 与 `or()` 控制后续拼接逻辑。
- 条件方法丰富，常见包括：
  - `eq` / `ne`：等于 / 不等于
  - `gt` / `gte`：大于 / 大于等于
  - `lt` / `lte`：小于 / 小于等于
  - `between` / `notBetween`：范围内 / 范围外
  - `in` / `notIn`（`not` 别名）：集合包含 / 不包含
  - `like` / `notLike`：模糊匹配 / 非模糊匹配
  - `isNull` / `isNotNull`：为空 / 非空
  - `empty` / `notEmpty`：空字符串 / 非空字符串
  - `exists` / `notExists`：子查询存在 / 不存在
  可结合第三个参数传入 Predicate 控制是否拼接（如 `eq(SysUser::getId, id, Objects::nonNull)`），也可在链式上使用 `.ignoreNullValueInCondition(true)` 等全局开关。
- `forSearch(true)` 一次性启用忽略 `null`、空字符串、`trim` 行为，适合搜索表单。
- `returnType(...)` 建议放在条件、limit构建后面、`get`/`list`/`paging`/`count` 终止方法前立即调用 returnType，形成统一的调用规范。
- `where(queryObject)` 将带注解的对象转换成条件。
- `dbAdapt(selector -> ...)` 根据当前数据库类型拼接不同 SQL。
- 支持 `get`, `list`, `count`, `exists`, `paging(Pager)`, `cursor`, `mapWithKey` 等多种终端调用。

### 7.2 InsertChain

插入链 `cn.xbatis.core.chain.InsertChain` 支持 `INSERT ... VALUES` 与 `INSERT ... SELECT` 混合构建：
```java
InsertChain.of(sysUserMapper)
    .insert(SysUser.class)
    .fields(SysUser::getUserName, SysUser::getRoleId)
    .fromSelect(Query.create()
        .select(SysUser2::getUserName, SysUser2::getRoleId)
        .from(SysUser2.class)
    )
    .execute();
```
也可通过 `values` 多次追加批量数据。

### 7.3 UpdateChain

更新链 `cn.xbatis.core.chain.UpdateChain` 支持动态 `set`、函数自增、`RETURNING`、条件构建：
```java
SysUser user = UpdateChain.of(sysUserMapper)
    .update(SysUser.class)
    .set(SysUser::getUserName, "new name")
    .set(SysUser::getVersion, c -> c.plus(1))
    .eq(SysUser::getId, 1)
    .returning(SysUser.class)
    .returnType(SysUser.class)
    .executeAndReturning();
```

### 7.4 DeleteChain

删除链 `cn.xbatis.core.chain.DeleteChain` 构建删除条件并支持 `RETURNING` 查看被删除数据：
```java
List<SysUser> removed = DeleteChain.of(sysUserMapper)
    .in(SysUser::getId, 1, 2)
    .returning(SysUser.class)
    .returnType(SysUser.class)
    .executeAndReturningList();
```

---

## 8. 对象驱动的条件与排序

- 通过 `@ConditionTarget` 标记 DTO 指向的实体；
- `@Condition` 支持 EQ、NE、GT、LT、BETWEEN、EXISTS、LIKE 等类别，并可指定目标属性、默认值、`toEndDayTime`、`likeMode` 等；
- `@Conditions` 支持单个字段映射到多列条件并控制 `AND/OR`；
- `@ConditionGroup` 支持字段分组和嵌套逻辑；
- 若实现 `ObjectConditionLifeCycle`，可通过 `beforeBuildCondition()` 在拼接前处理入参（例如根据枚举生成日期区间）；
- 排序同理，使用 `@Condition` 的排序类型结合 `objToOrderBy` 工具即可自动生成 `ORDER BY`;
- 示例：
```java
@Data
@ConditionTarget(SysUser.class)
public class QueryREQ {
    private Integer id;

    @Condition(value = Condition.Type.LIKE)
    private String userName;

    @Conditions(
        logic = Logic.OR,
        value = {
            @Condition(property = SysUser.Fields.userName, value = Condition.Type.LIKE),
            @Condition(property = SysUser.Fields.password, value = Condition.Type.LIKE)
        }
    )
    private String keyword;
}
```

---

## 9. 数据库函数与 SQL 模板

- 通过 `import static db.sql.api.impl.cmd.Methods.*;` 引入统一函数。
- 支持的函数涵盖 `count`, `sum`, `avg`, `min`, `max`, `abs`, `ceil`, `floor`, `rand`, `sign`, `pi`, `truncate`, `round`, `pow`, `sqrt`, `exp`, `mod`, `log`, `sin`, `cos`, `tan`, `charLength`, `concat`, `upper`, `lower`, `substring`, `currentDate`, `dateDiff`, `dateAdd`, `inetAton` 等，以及 MySQL 专属 `findInSet`, `md5`, `jsonExtract`, `groupConcat` 等。
- SQL 模板体系：
  - `Methods.tpl`：普通 SQL，占位 `{0}`、`{1}` 并自动替换。
  - `Methods.fTpl`：函数模板，可继续链式调用框架函数。
  - `Methods.cTpl`：条件模板，适合构造复杂 `WHERE` 片段。
  - 模板兼容单引号自动转义，可使用 `Methods.cTpl(true, "...", ...)` 让模板自动替换 `'`。
- 示例：
```java
QueryChain.of(sysUserMapper)
    .select(SysUser::getRoleId, c -> Methods.tpl("count({0})+{1}", c, "1"))
    .and(GetterFields.of(SysUser::getId, SysUser::getId),
         cs -> Methods.cTpl("{0}+{1}={2}", cs[0], cs[1], 2));
```

---

## 10. 多数据库与动态数据源

### 10.1 多数据库差异化

- `QueryChain`、`UpdateChain`、`DeleteChain`、`InsertChain` 均可调用 `dbAdapt`：
```java
QueryChain.of(sysUserMapper)
    .select(SysUser::getId)
    .dbAdapt((query, selector) -> selector
        .when(DbType.H2, db -> query.eq(SysUser::getId, 3))
        .when(DbType.MYSQL, db -> query.eq(SysUser::getId, 2))
        .otherwise(db -> query.eq(SysUser::getId, 1))
    )
    .get();
```
- 也可直接在 Mapper 层使用 `mapper.dbAdapt(selector -> {...})` 编写完全不同流程。

### 10.2 动态数据源路由

- 引入 `cn.xbatis:xbatis-datasource-routing`。
- `spring.ds.routing.*` 可定义多数据源、主从、副本分组、连接池属性（支持 Hikari、Druid、自定义）。
- 使用 `@DS("master")`、`@DS("slave")` 注解在类或方法上切换数据源。
- 注意事项：
  - 事务传播：在主从切换方法间需使用 `@Transactional(propagation = Propagation.NOT_SUPPORTED)` 或 `REQUIRES_NEW` 避免事务锁定数据源。
  - 同类内部调用需通过 `AopContext.currentProxy()` 或拆分类。
  - 可通过 `spring.ds.jdbc-config-decrypt=true` 与 `JdbcConfigDecryptor` 接口实现 JDBC 配置加密。
  - 支持分布式事务（如 Seata）集成：`spring.ds.seata=true`。

---

## 11. 多租户、逻辑删除、乐观锁

### 11.1 多租户

- 实体字段使用 `@TenantId`，自动在写入时填充租户 ID，查询/更新/删除自动附加租户条件。
- 通过 `TenantContext.registerTenantGetter(() -> tenantId)`（`cn.xbatis.core.tenant.TenantContext`）注册租户获取器，支持返回单个 ID 或 `TenantId`（多租户集合）。
- 若需要临时关闭租户限制，可让注册函数返回 `null`。
- 支持 ThreadLocal 自定义：可封装 `TenantTLUtil`，在拦截器或过滤器中 `set` 或 `clear`，再在 `TenantContext` 回调中返回。

### 11.2 逻辑删除

- `@LogicDelete(beforeValue = "0", afterValue = "1", deleteTimeField = "deleteTime")` 控制删除前后值及删除时间字段。
- `XbatisGlobalConfig.setLogicDeleteInterceptor` 可在删除时自动填充操作人等字段。
- 全局开关 `setLogicDeleteSwitch(true/false)`，局部可使用：
```java
try (LogicDeleteSwitch ignored = LogicDeleteSwitch.with(false)) {
    mapper.getById(1);
}
// 或
LogicDeleteUtil.execute(false, () -> mapper.getById(1));
```
- 注意：`DeleteChain` 默认执行物理删除，只有 `Mapper` 的删除方法受逻辑删除控制。

### 11.3 乐观锁

- `@Version` 字段在 `save` 时自动写 0。
- `update` / `delete` 会追加 `WHERE version=?`，更新成功后自动递增版本。

### 11.4 分表（SplitTable）

- 在实体上使用 `@SplitTable(TableSplitterClass.class)` 声明分表实体；`value` 指向实现了 `TableSplitter` 接口的分隔器。
- 使用 `@SplitTableKey` 标记分表键字段，当前仅支持单列分片。
- `TableSplitter#support(Class<?>)` 用于声明可接受的分片键类型；`TableSplitter#split(String table, Object key)` 根据原始表名与键值返回真实表名，例如 `sys_user_3`。
- 链式查询、更新、删除与普通实体无差异，但必须在条件中包含分表键，否则无法定位真实表。
- 可结合 `QueryChain`, `UpdateChain` 等链式 API，传入分表键并复用多数据库、租户、逻辑删除等特性。

---

## 12. 动态默认值与事件监听

- 内置动态值：`{BLANK}`、`{NOW}`（支持 `LocalDateTime`, `LocalDate`, `Date`, `Long`, `Integer`, `String`）、`{TODAY}`（日期区间）。
- 自定义动态值示例：
```java
XbatisGlobalConfig.setDynamicValue("{day7}", (clazz, type) -> new LocalDate[]{
    LocalDate.now().minusDays(7), LocalDate.now()
});
```
- 在对象条件中可通过 `XbatisGlobalConfig.getDynamicValue(clazz, LocalDate[].class, "{day7}")` 获取并赋值。
- 全局监听：
```java
XbatisGlobalConfig.setGlobalOnInsertListener(entity -> {
    // 统一填充创建人等
});
XbatisGlobalConfig.setGlobalOnUpdateListener(entity -> {
    // 统一填充更新人等
});
```

---

## 13. SQL 模板与 XML 整合

- 模板机制允许重用 SQL 片段、函数包装、条件组合，避免字符串硬编码。
- 框架仍兼容传统 MyBatis XML，可通过命名约定在单 Mapper 模式下调用：
```xml
<mapper namespace="xxx.MybatisBasicMapper">
  <select id="SysRole:selectByIds" resultType="com.xbatis.core.test.DO.SysRole">
    select * from sys_role
    where id >= #{id} and id <= #{id2}
    order by id asc
  </select>
</mapper>
```
调用方式：
```java
List<SysRole> roleList = mybatisBasicMapper.withSqlSession(
    SysRole.class, "selectByIds", params,
    (statement, p, sqlSession) -> sqlSession.selectList(statement, p)
);
```
- XML 可与链式 DSL 协同使用，按需在链式查询中引用模板或在 XML 中传递动态参数。

---

## 14. 分页与性能调优

- 内置 `Pager` 对象处理分页：`Pager.of(current, size)`，`QueryChain.paging(pager)` 自动执行分页查询和统计。
- 框架会自动优化分页 SQL：剔除非必要 `LEFT JOIN`、`ORDER BY`，将 `SELECT` 替换为 `COUNT(*)`，提升性能。
- 可通过 `XbatisGlobalConfig.setPagingProcessor` 针对 Oracle、SQL Server 等数据库定制分页语法。
- 自定义分页策略可重写 `customizePager` 接口，例如自定义 count 查询、序列化方式。
- 查询链可指定 `.limit(n)` 控制结果条数，结合 `.forSearch(true)` 对搜索接口做性能优化。

---

## 15. 代码生成器能力

- `GeneratorConfig` 支持配置实体、Mapper、Mapper XML、DAO、Service、Controller、DTO 等生成选项。
- 可定义包路径、文件后缀、是否生成注解、主键策略、列名映射、数据库类型映射、父类、是否生成 XML 等。
- 支持单 Mapper 模式：`mapperConfig.enable(false).superClass(MybatisBasicMapper.class)`.
- 可配置是否生成 `@Mapper` 注解、是否生成泛型、是否生成 `resultMap`、是否输出列信息。
- 支持多语言模板组合，帮助快速搭建项目骨架。

---

## 16. 启动时安全检查（AI 项目必选）

无论是手工接入还是 AI 自动生成项目骨架，都强烈建议在开发阶段开启 POJO 安全检查：xbatis 会在启动时对 VO（`@ResultEntity`）、Model（实现 `cn.xbatis.core.mvc.Model`）、条件/排序对象（`@ConditionTarget`、`@OrderByTarget`）进行结构校验，提前发现字段映射或注解遗漏。AI Agent 在生成代码时，务必同步生成以下配置，避免遗漏：

```java
// 必选：主应用入口启用 POJO 检查
@SpringBootApplication
@XbatisPojoCheckScan(basePackages = "com.example.project.pojo")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
```

```java
// 推荐：开发环境下的专用配置，可由 AI 自动生成
@Profile("dev")
@Configuration
@XbatisPojoCheckScan(
    basePackages = "com.example.api.dto",
    modelPackages = "com.example.api.model",
    resultEntityPackages = "com.example.api.vo"
)
public class XbatisSafeCheckConfig {
}
```

### 16.1 Spring / Spring Boot

- 注解属性说明（`org.mybatis.spring.boot.autoconfigure.XbatisPojoCheckScan`）：

  | 属性名 | 说明 |
  | --- | --- |
  | `basePackages` | 基础包扫描路径 |
  | `modelPackages` | Model 实现所在包，缺省时使用 `basePackages` |
  | `resultEntityPackages` | VO（`@ResultEntity`）包路径，缺省时使用 `basePackages` |
  | `conditionTargetPackages` | 条件 DTO（`@ConditionTarget`）包路径 |
  | `orderByTargetPackages` | 排序 DTO（例如 `@OrderByTarget`）包路径 |

### 16.2 Solon

- 在 `solon.yml` 中配置：
  ```yaml
  mybatis.master:
    pojoCheck:
      basePackages: com.example.**.pojo
      modelPackages: com.example.**.model
      resultEntityPackages: com.example.**.vo
      conditionTargetPackages: com.example.**.condition
      orderByTargetPackages: com.example.**.orderby
    mappers:
      - "com.example.mapper"
  ```
- `pojoCheck` 配置与 `@XbatisPojoCheckScan` 参数一致，多包路径使用逗号分隔。建议在开发/测试环境开启，以减少生产环境启动耗时。

---

## 17. 常见排查与实践技巧

- **日志调试**：在日志配置中添加 `<logger name="cn.xbatis" level="trace"/>` 可打印生成 SQL 与链路信息。
- **忽略空值条件**：`QueryChain` 提供 `.ignoreNullValueInCondition(true)`, `.ignoreEmptyInCondition(true)`, `.trimStringInCondition(true)` 或直接 `.forSearch(true)`.
- **安全更新**：`UpdateChain.set` 支持传入函数 `c -> c.plus(1)` 等，用于自增版本或计数字段。
- **批量能力**：通过 `saveBatch`, `updateBatch`, `InsertChain.values` 等高性能批量 API，或 `batchOpt` 模块进一步优化批处理。
- **枚举与字典**：`@PutEnumValue` 可在返回结果中注入枚举文本，`@ResultField` 支持字段级结果映射，`@Fetch` 可自动级联查询。
- **SQL 模板陷阱**：使用模板时若出现单引号问题，可开启封装模式或写双引号。
- **事务下的数据源切换**：跨数据源调用需注意事务传播策略，避免因 Spring 代理导致的数据源无法切换。

---

## 17. 面向 AI Agent 的实用建议

- **检索策略**：优先考虑 `QueryChain`、`InsertChain`、`UpdateChain`、`DeleteChain` 与注解组合的解决方案，覆盖绝大多数 CRUD 与统计场景。
- **代码生成建议**：
  - 统一使用方法引用，如 `SysUser::getId`，避免硬编码列名。
  - 搜索接口调用 `.forSearch(true)` 或使用带注解的 DTO，以减少手动判断空值。
  - 多库场景添加 `dbAdapt`，多租户场景自动带上 `TenantContext`。
  - 更新或删除需返回结果时，选择 `.executeAndReturning()` 或 `.executeAndReturningList()`。
- **项目结构建议**：
  - 单 Mapper 模式适合大型项目，结合 `BasicMapper` 和链式 DSL 统一访问层。
  - 动态数据源、租户、逻辑删除等横切特性需在 Agent 生成的代码中显式说明开关状态。
  - 通过 `GeneratorConfig` 生成初始骨架，再用 Agent 生成增量链式查询或 Service 逻辑。
- **安全审计**：借助 `@OnInsert`, `@OnUpdate`, 逻辑删除拦截器与租户上下文自动填充创建人、更新时间、租户信息，满足审计需求。

---

## 附录：开发提效速查（源自 `fast-dev.md`，AI 项目必须考虑）

### 指定数据库类型
为减少自动识别的开销，可在配置中显式声明 `databaseId`：
```yaml
mybatis:
  configuration:
    databaseId: MYSQL   # 参见 db-support.md 获取可用 ID
```

### 链式 API 的省略写法（AI 代码生成务必采用）
- 若查询、`from`、`returnType` 都针对当前 Mapper 的实体类，可省略 `select`/`from`/`returnType`：
```java
SysUser user = QueryChain.of(sysUserMapper)
        .eq(SysUser::getId, 1)
        .get();
```
- `select(VO.class)` 与 `returnType(VO.class)` 相同时，可只写 `returnType`，框架会根据 VO 注解自动补全列。

- 批量忽略：`.forSearch(true)` 同时启用忽略 `null`、空字符串并自动 `trim`，AI 默认应启用此策略。
- 精准忽略：
```java
QueryChain.of(sysUserMapper)
    .eq(SysUser::getId, id, Objects::nonNull)
    .like(SysUser::getUserName, userName, StringUtils::isNotBlank)
    .get();
```

### VO 自动映射
- `@ResultEntity` + `@NestedResultEntity` 可构建任意嵌套结构，字段名与实体一致时可省略注解属性。
- 建议结合 Lombok `@FieldNameConstants`，在注解中引用 `X.Fields.xxx` 避免硬编码。

### 链路 `connect` 的使用
`connect` 可在链式查询中获取自身句柄，构建复杂子查询：
```java
QueryChain.of(sysUserMapper)
    .select(SysUser::getId, SysUser::getUserName)
    .connect(query -> query.exists(SubQuery.create()
        .select1()
        .from(SysUser.class)
        .eq(SysUser::getId, query.$(SysUser::getId))
        .isNotNull(SysUser::getPassword)
        .limit(1)))
    .list();
```

### 推荐的数据库函数调用方式
通过静态导入 `db.sql.api.impl.cmd.Methods.*`，让链式代码更简洁：
```java
Integer id = QueryChain.of(sysUserMapper)
        .select(SysUser::getId, c -> sum(add(c, 1)))
        .returnType(Integer.TYPE)
        .get();
```

这些“省力写法”可极大提升日常开发效率，在构建 AI 生成策略时亦可作为默认建议。

---

建议将本知识库连同项目源码、依赖 API 文档一起构建为向量知识库，使 AI Agent 能够准确生成符合 xbatis 规范的代码与配置。***


