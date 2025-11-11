# Xbatis AI Agent Knowledge Base

> Official site: https://xbatis.cn  
> DeepWiki documentation: https://deepwiki.xbatis.cn

Knowledge pack for AI agents working with the Xbatis framework. It distills the official Chinese materials into a self-contained explanation so that automated assistants can understand the framework capabilities, common patterns, and key APIs.

# English | [简体中文](README.zh-CN.md)

---

## 1. Framework Overview

- **Positioning**: Xbatis is built on MyBatis and delivers a highly ORM-like database experience, emphasizing "less SQL, fluent DSL, cross-database compatibility".
- **Primary advantages**:
  - Multi-table joins, subqueries, fluent pagination, automatic SQL optimization (auto-removal of redundant `LEFT JOIN` / `ORDER BY`, smarter `COUNT`).
  - Built-in `RETURNING` support, batch insert/update chains, native function wrappers, SQL templates.
  - Single mapper mode that allows one `BasicMapper` to cover every entity.
  - Database function library, database-specific `dbAdapt`, dynamic datasource routing.
  - Full annotation ecosystem: logical delete, multi-tenancy, optimistic locking, result mapping, condition mapping, dynamic default value injection, etc.
- **Core characteristics** (from the official highlights):
  1. Extremely lightweight: only wraps MyBatis without invasive modification.
  2. High performance: keeps execution efficiency close to handwritten SQL.
  3. Flexible and easy to use: fluent APIs read like natural language.
  4. Highly available: covers more than 90% of common SQL scenarios.
  5. Reliable and safe: concise design yet feature-rich, hardened by extensive testing.
  6. Pagination optimization: automatically tunes `JOIN`, `COUNT`, `ORDER BY`, and ships with a built-in pager.

---

## 2. Core Modules & Package Paths

| Module | Typical package | Core types | Purpose |
| --- | --- | --- | --- |
| Core Mapper | `cn.xbatis.core.mybatis.mapper` | `MybatisMapper<T>`, `BasicMapper` | Provides base CRUD plus single-mapper capabilities |
| Fluent DSL | `cn.xbatis.core.chain` | `QueryChain`, `InsertChain`, `UpdateChain`, `DeleteChain` | Build complex SQL, batch ops, returning clauses |
| Global config | `cn.xbatis.core.config` | `XbatisGlobalConfig` | Unified naming rules, interceptors, dynamic values, paging |
| Annotation suite | `cn.xbatis.db.annotations` | `@Table`, `@TableId`, `@TableField`, `@LogicDelete`,`@LogicDeleteTime`, `@TenantId`, `@Version`, `@Condition`, `@Fetch`, etc. | Entity mapping, injection rules, condition objects, result shaping |
| Database functions | `db.sql.api.impl.cmd` | `Methods` | Cross-database functions, SQL templates, fluent wrappers |
| Multi-tenant | `cn.xbatis.core.tenant` | `TenantContext`, `TenantId` | Register and propagate tenant IDs globally |
| Dynamic datasource | `cn.xbatis.datasource.routing` | `@DS`, `JdbcConfigDecryptor` | Runtime datasource switching, encrypted configs, grouped routing |
| Logical delete | `cn.xbatis.core.logic` | `LogicDeleteSwitch`, `LogicDeleteUtil` | Toggle logical delete, easy overrides |
| Dynamic values | `cn.xbatis.core.dynamic` | `XbatisGlobalConfig#setDynamicValue` | Define tokens like `{NOW}`, `{TODAY}` |
| Code generation | `cn.xbatis.codegen` | `GeneratorConfig` and sub modules | One-stop generation for entities, mapper, service skeletons, etc. |

---

## 3. Quick Start & Dependencies

### 3.1 Maven coordinates example
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>cn.xbatis</groupId>
      <artifactId>xbatis-bom</artifactId>
      <version>1.9.2-RC6</version>
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

### 3.2 Datasource configuration example
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dbName
    username: dbusername
    password: dbpassword
```

### 3.3 Bootstrapping skeleton example
```java
@SpringBootApplication
@MapperScan("com.xx.xxx.mapper")
public class XbatisApplication {
    public static void main(String[] args) {
        SpringApplication.run(XbatisApplication.class, args);
    }
}
```

### 3.4 Spring Boot 2 integration

- **Maven dependencies** (using `xbatis-spring-boot-starter`):
  ```xml
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>cn.xbatis</groupId>
        <artifactId>xbatis-spring-boot-parent</artifactId>
        <version>1.9.2-RC6</version>
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
- **Datasource & mapper scanning**: same as 3.2/3.3; Spring Boot 2 continues to use `application.yml` and `@MapperScan`.
- **Bootstrap class**: identical to 3.3, ensure the starter version matches Spring Boot 2.

### 3.5 Solon integration

- **Maven dependencies**:
  ```xml
  <!-- Order matters: register Xbatis plugin before mybatis-solon-plugin -->
  <dependency>
    <groupId>cn.xbatis</groupId>
    <artifactId>xbatis-solon-plugin</artifactId>
    <version>1.9.2-RC6</version>
  </dependency>
  <dependency>
    <groupId>org.noear</groupId>
    <artifactId>mybatis-solon-plugin</artifactId>
    <version>${mybatis.solon.version}</version>
  </dependency>
  ```
- **Configuration example**:
  ```yaml
  # solon.yml
  ds:
    schema: demo        # recommend matching the database name
    jdbcUrl: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456

  # Rule: mybatis.<datasource bean name>
  mybatis.master:
    mappers:
      - "com.demo.mapper"           # package scan
      - "classpath:mapper/**/*.xml" # XML scan
  ```
- **Datasource bean**:
  ```java
  @Configuration
  public class MybatisConfig {

      @Bean(name = "master", typed = true)
      public DataSource dataSource(@Inject("${ds}") HikariDataSource ds) {
          return ds;
      }
  }
  ```
- **Business usage**:
  ```java
  @Controller
  public class DemoController {

      @Db            // omit name for single datasource; specify @Db("master") for multi-datasource
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
- Further configuration aligns with `mybatis-solon-plugin`; chaining DSL, sharding, and multi-tenancy remain available.
  Entity, mapper, and service examples:
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

> Pager resides in `cn.xbatis.core.mybatis.mapper.context.Pager` and implements `cn.xbatis.page.IPager`. Common usage:
> - Static factories: `Pager.of(size)`, `Pager.of(number, size)`.
> - `setExecuteCount(boolean)`: control whether to run the count query.
> - `paging(Pager)`: pass into chain APIs and read `getResults()`, `getTotal()`, `getNumber()`, `getSize()`, `getTotalPage()`, etc.
> - `PagerGetSetUtil` enables dynamic read/write of extension fields by `PagerField`.
```

---

## 4. Global Configuration `XbatisGlobalConfig` (`cn.xbatis.core.config.XbatisGlobalConfig`)

- Configure ahead of time via Spring `ConfigurationCustomizer` or `@PostConstruct`.
- Common methods:
  - `setSingleMapperClass(MybatisBasicMapper.class)`: enable single-mapper mode with `BasicMapper`.
  - `setTableUnderline(boolean)` / `setColumnUnderline(boolean)`: control table/column naming.
  - `setDatabaseCaseRule(DatabaseCaseRule rule)` or `setDatabaseCaseRule(DbType, rule)`: adjust casing strategy.
  - `setDynamicValue("{KEY}", (clazz, type) -> {...})` and `getDynamicValue(clazz, type, key)`: register and use dynamic placeholders.
  - `setLogicDeleteInterceptor((entity, update) -> {...})` and `setLogicDeleteSwitch(boolean)`: standardize logical delete metadata & toggles.
  - `addMapperMethodInterceptor(new MyInterceptor())`, `enableInterceptOfficialMapperMethod()`: register custom mapper interceptors, optionally extending official methods.
  - `setPagingProcessor(DbType, PagingProcessor)`: customize pagination for each database.

---

## 5. Annotation Suite & Property Reference

### 5.1 `@Table` (`cn.xbatis.db.annotations.Table`)

| Property | Nullable | Default | Description |
| --- | --- | --- | --- |
| `value` | Yes | – | Table name; defaults to camelCase-to-snake-case of entity name |
| `schema` | Yes | – | Database schema |
| `columnNameRule` | Yes | `columnNameRule.IGNORE` | Rule to derive column names when not specified |
| `databaseCaseRule` | Yes | `DatabaseCaseRule.DEFAULT` | Controls casing; works with `XbatisGlobalConfig.setDatabaseCaseRule(...)` |

Example:
```java
@Table(databaseCaseRule = DatabaseCaseRule.UPPERCASE)
public class SysUser { }
```

### 5.2 `@TableId` (`cn.xbatis.db.annotations.TableId`)

| Property | Nullable | Default | Description |
| --- | --- | --- | --- |
| `value` | Yes | `IdAutoType.AUTO` | Primary key strategy: `AUTO`, `NONE`, `SQL`, `GENERATOR` |
| `dbType` | Yes | – | Target database type for differentiating strategies |
| `sql` | Yes | – | Required when `value = SQL`; custom SQL for ID retrieval |
| `generatorName` | Yes | – | Required when `value = GENERATOR`; refers to registered ID generator |

Repeatable to accommodate multiple database strategies.

### 5.3 `@TableField` (`cn.xbatis.db.annotations.TableField`)

| Property | Nullable | Default | Description |
| --- | --- | --- | --- |
| `value` | Yes | – | Column name; can be omitted when camelCase-to-snake-case applies |
| `select` | Yes | `true` | Whether to participate when calling `select(Entity.class)` |
| `insert` | Yes | `true` | Whether to write during `save` |
| `update` | Yes | `true` | Whether to update |
| `jdbcType` | Yes | – | Explicit JDBC type |
| `typeHandler` | Yes | – | Custom type handler |
| `defaultValue` | Yes | – | Insert default; supports static values and dynamic tokens like `{NOW}` |
| `updateDefaultValue` | Yes | – | Default during updates |
| `defaultValueFillAlways` | Yes | `false` | Always apply `defaultValue` on insert |
| `updateDefaultValueFillAlways` | Yes | `false` | Always apply `updateDefaultValue` |
| `exists` | Yes | `true` | Whether the field physically exists (false for transient fields) |

### 5.4 `@LogicDelete` (`cn.xbatis.db.annotations.LogicDelete`)

Logical delete annotation.

| Property | Nullable | Default | Description |
| --- | --- | --- | --- |
| `beforeValue` | Yes | – | Value before deletion; null treated as `NULL` |
| `afterValue` | No | – | Value after deletion; supports dynamic tokens `{NOW}`, etc. |

Combine with `XbatisGlobalConfig.setLogicDeleteInterceptor` to fill deleter metadata.

#### 5.4.2 `@LogicDeleteTime` (`cn.xbatis.db.annotations.LogicDeleteTime`)

Field name for logic delete timestamp; supports `LocalDateTime`, `Date`, `Long` (ms), `Integer` (seconds)

### 5.5 `@TenantId` (`cn.xbatis.db.annotations.TenantId`)

No extra properties. Marks tenant ID fields; automatically filled on `save/update/delete` and appended to query conditions. Register tenant suppliers through `TenantContext.registerTenantGetter` (`cn.xbatis.core.tenant.TenantContext`).

### 5.6 `@Version` (`cn.xbatis.db.annotations.Version`)

No extra properties. Marks optimistic-lock version fields. `save` sets it to 0; `update/delete` append `WHERE version = ?` and successful updates increment it automatically.

### 5.7 Lifecycle annotations: `@OnInsert` (`cn.xbatis.db.annotations.OnInsert`), `@OnUpdate` (`cn.xbatis.db.annotations.OnUpdate`)

| Annotation | Property | Nullable | Default | Description |
| --- | --- | --- | --- | --- |
| `@OnInsert` | `value` | Yes | – | Assign pre-insert listener implementing `OnInsertListener<T>` |
| `@OnUpdate` | `value` | Yes | – | Assign pre-update listener implementing `OnUpdateListener<T>` |

Besides local listeners, register global handlers via `XbatisGlobalConfig.setGlobalOnInsertListener` / `setGlobalOnUpdateListener`.

### 5.8 Result-mapping annotations (`@ResultEntity` / `@ResultField` / `@Fetch`, etc. under `cn.xbatis.db.annotations`)

- `@ResultEntity`: declare VO ↔ entity mapping and auto-complete `SELECT` columns.
- `@ResultField`:

  | Property | Nullable | Default | Description |
    | --- | --- | --- | --- |
  | `value` | Yes | – | Column(s) mapped to the field; supports multi-column inputs |
  | `jdbcType` | Yes | – | JDBC type |
  | `typeHandler` | Yes | – | Custom type handler |

- `@Fetch`:

  | Property | Nullable | Default | Description |
    | --- | --- | --- | --- |
  | `column` | Yes | – | Matching column; alternate to `property`, takes precedence |
  | `property` | No | – | Source entity property |
  | `source` | No | – | Source entity type |
  | `storey` | Yes | `-1` | Source hierarchy; autodetect by default |
  | `target` | No | – | Target entity (to be fetched) |
  | `targetProperty` | No | – | Target entity join column |
  | `targetSelectProperty` | Yes | – | Selected columns or expressions |
  | `middle` | No | – | Middle entity (join table) |
  | `middleSourceProperty` | No | – | Middle entity column bound to source |
  | `middleTargetProperty` | No | – | Middle entity column bound to target |
  | `orderBy` | Yes | – | Order clause, e.g., `"[{xx} desc]"` or `"field desc"` |
  | `multiValueErrorIgnore` | Yes | `false` | Ignore multi-row result when expecting 1-to-1 |
  | `limit` | Yes | `0` | Limit result size; 0 = unlimited |
  | `memoryLimit` | Yes | `false` | Apply limit in-memory with IN to reduce queries |
  | `nullFillValue` | Yes | `null` | Default when result is empty |
  | `otherConditions` | Yes | – | Extra conditions, e.g., `[{Type} = 2]` |

- **AI generation tip**: prefer using `@ResultEntity` and the related annotations (`@ResultField`, `@NestedResultEntity`, `@NestedResultEntityField`, `@ResultCalcField`, `@Fetch`, `@PutEnumValue`, `@PutValue`, etc.) on VO/DTOs for direct return types, eliminating manual transformation. Do not annotate entities with these; keep `@TypeHandler`, `@Put***` on result objects only to avoid polluting entity models.

- `@NestedResultEntity` (`cn.xbatis.db.annotations.NestedResultEntity`):

  | Property | Nullable | Default | Description |
    | --- | --- | --- | --- |
  | `target` | Yes | – | Target entity; defaults to `@ResultEntity` target |
  | `storey` | Yes | `1` | Storage level; differentiate layers in self-joins |
  Declaring VO fields as nested objects automatically fills nested results recursively.

- `@NestedResultEntityField` (`cn.xbatis.db.annotations.NestedResultEntityField`):

  | Property | Nullable | Default | Description |
    | --- | --- | --- | --- |
  | `value` | Yes | – | Field name inside the nested entity |
  Use when VO field names differ from entity fields. Works well with Lombok `@FieldNameConstants`.

- `@ResultCalcField` (`cn.xbatis.db.annotations.ResultCalcField`):

  | Property | Nullable | Default | Description |
    | --- | --- | --- | --- |
  | `value` | No | – | Calculation SQL, e.g., `count(1)`, `sum({id})` |
  | `target` | Yes | – | Target entity; defaults to `@ResultEntity` target |
  | `storey` | Yes | `1` | Storage level |
  When `select(VO.class)`, automatically produces aggregate columns. Use `{field}` placeholders for dynamic columns.

- `@PutEnumValue` (`cn.xbatis.db.annotations.PutEnumValue`):

  | Property | Nullable | Default | Description |
    | --- | --- | --- | --- |
  | `source` | No | – | Source entity |
  | `property` | No | – | Field storing enum code |
  | `storey` | Yes | `1` | Storage level |
  | `target` | No | – | Enum class |
  | `code` | No | `code` | Field name representing the code |
  | `value` | No | `name` | Field name representing display value |
  | `required` | No | `false` | Throw if enum not found |
  | `defaultValue` | Yes | – | Fallback when enum missing |
  Suitable for auto-converting status codes to labels; supports nested mappings and multi-column matching.

- `@PutValue` (`cn.xbatis.db.annotations.PutValue`):

  | Property | Nullable | Default | Description |
    | --- | --- | --- | --- |
  | `source` | No | – | Source entity |
  | `property` | No | – | Source field |
  | `storey` | Yes | `1` | Storage level |
  | `factory` | No | – | Factory class providing static method |
  | `method` | No | – | Method name inside factory |
  | `required` | No | `false` | Throw if value missing |
  | `defaultValue` | Yes | – | Default value |
  Supports injecting dynamic values based on multiple fields. Results are cached per session using `factory+method+args`.

Combine these annotations: use `@ResultEntity` for overall mapping, `@NestedResultEntity` for nested structure, `@ResultCalcField` for aggregates, and `@PutEnumValue`/`@PutValue` for extra data—delivering near-entity auto-wiring for VOs.

### 5.9 Sharding annotations (`@SplitTable`, `@SplitTableKey`, `TableSplitter` in `cn.xbatis.db.annotations`)

- `@SplitTable`:

  | Property | Nullable | Default | Description |
    | --- | --- | --- | --- |
  | `value` | No | – | `TableSplitter` implementation used to compute actual table name |

- `@SplitTableKey`: marks the sharding key field; supports a single column and takes sharding input at runtime.

`TableSplitter` interface methods:

```java
boolean support(Class<?> type);
String split(String sourceTableName, Object splitValue);
```

`support` declares supported key types; `split` returns actual table names like `sys_user_0` ~ `sys_user_9`. Queries/updates must include the shard key so Xbatis can locate the table.

### 5.10 Condition annotation combinations

#### `@ConditionTarget` (`cn.xbatis.db.annotations.ConditionTarget`)

| Property | Nullable | Default | Description |
| --- | --- | --- | --- |
| `value` | No | – | Target entity; defaults to current class if omitted |
| `logic` | Yes | `Logic.AND` | Default top-level logic; set to `Logic.OR` for OR combinations |

#### `@Condition` (`cn.xbatis.db.annotations.Condition`)

| Property | Nullable | Default | Description |
| --- | --- | --- | --- |
| `value` | Yes | `Condition.Type.EQ` | Condition type, e.g., `LIKE`, `GT`, `BETWEEN`, `EXISTS` |
| `target` | Yes | – | Target entity; defaults to `@ConditionTarget` or current class |
| `property` | Yes | – | Target property name; defaults to field name |
| `storey` | Yes | – | Source storage level for nested objects |
| `likeMode` | Yes | – | `LEFT`, `RIGHT`, `BOTH`, etc. |
| `toEndDayTime` | Yes | `false` | Auto-extend to end of day for `LTE` or second parameter of `BETWEEN` |
| `defaultValue` | Yes | – | Static value or dynamic key `{NOW}`, `{TODAY}`, custom entries |

#### `@Conditions` (`cn.xbatis.db.annotations.Conditions`)

| Property | Nullable | Default | Description |
| --- | --- | --- | --- |
| `value` | Yes | – | Multiple `@Condition` elements, often for keyword multi-column search |
| `logic` | Yes | `Logic.OR` | Logic among combined conditions |

#### `@ConditionGroup` (`cn.xbatis.db.annotations.ConditionGroup`)

| Property | Nullable | Default | Description |
| --- | --- | --- | --- |
| `value` | Yes | – | Grouped fields |
| `logic` | Yes | `Logic.AND` | Group logic; change to `Logic.OR` as needed |

If DTO implements `ObjectConditionLifeCycle` (`cn.xbatis.core.sql.ObjectConditionLifeCycle`), it can preprocess input in `beforeBuildCondition()` (e.g., derive past 7 days based on `timeType`) and cooperate with the annotations above for deep nesting.

---

---

## 6. Mapper Basic Capabilities

`MybatisMapper<T>` (`cn.xbatis.core.mybatis.mapper.MybatisMapper`) ships with:

- **Read**: `get`, `list`, `listAll`, `listByIds`, `cursor`, `exists`, `count`, `mapWithKey`, `page`, etc., accepting lambda `Where` builders.
- **Create**: `save`, `saveOrUpdate`, `saveBatch`, `saveModel`, `saveModelBatch`, `saveBatch(list, fields)`, supporting entity and `Model`.
- **Update**: `update`, `updateBatch`, `update(model, where)`, `update(list, forceFields)`, `saveOrUpdate`, plus forced update fields and batch `CASE WHEN` updates.
- **Delete**: `deleteById`, `deleteByIds`, `delete(entity)`, `delete(where)`, `deleteAll`, and `DeleteChain`.
- **Single-mapper mode** (for large entity counts or unified data-access layers):
  1. **Define your own MybatisBasicMapper interface extending BasicMapper**
     ```java
     public interface MybatisBasicMapper extends BasicMapper {
     }
     ```
     `BasicMapper` lives in `cn.xbatis.core.mybatis.mapper.BasicMapper` and wraps all general CRUD.
  2. **Configure scanning** (Spring Boot example)
     ```java
     @MapperScan(basePackageClasses = MybatisBasicMapper.class,
                 markerInterface = BasicMapper.class)
     ```
     Generates a single mapper bean instead of one per entity.
  3. **Register the global mapper**
     Call `XbatisGlobalConfig.setSingleMapperClass(MybatisBasicMapper.class);` during startup to designate the default entry.
  4. **Usage pattern**
     ```java
     @Autowired
     private MybatisBasicMapper mybatisBasicMapper;

     public void demo() {
         // Entity CRUD
         mybatisBasicMapper.save(new SysUser());
         mybatisBasicMapper.deleteById(SysUser.class, 1);

         // Fluent DSL: explicitly pass entity class
         QueryChain.of(mybatisBasicMapper, SysUser.class)
                 .eq(SysUser::getId, 1)
                 .list();
         UpdateChain.of(mybatisBasicMapper, SysUser.class)
                 .set(SysUser::getUserName, "basic")
                 .eq(SysUser::getId, 1)
                 .execute();
     }
     ```
     Combine with `BasicDaoImpl` (`cn.xbatis.core.mvc.impl.BasicDaoImpl`) to keep a classic three-layer architecture.
  5. **Handling complex SQL**
     Single mapper still cooperates with XML using namespace `xxx.MybatisBasicMapper` and `withSqlSession`:
     ```java
     List<SysRole> roles = mybatisBasicMapper.withSqlSession(
         SysRole.class,
         "selectByIds",
         params,
         (statement, p, sqlSession) -> sqlSession.selectList(statement, p)
     );
     ```
  6. **Notes**
    - DSL requires explicit entity class to resolve table/columns.
    - You can keep traditional mappers alongside single mapper, but maintain consistent conventions.
    - XML `<select id="EntityName:method">` naming must align with `withSqlSession` calls.

---

### 6.1 Insert/batch insert conflict handling

Xbatis `save` / `saveBatch` / `InsertChain` support cross-database duplicate-key strategies (ignore or update on conflict).

| Database   | Ignore on conflict | Update on conflict |
|------------|-------------------|--------------------|
| MySQL      | ✅                 | ✅                  |
| MariaDB    | ✅                 | ✅                  |
| Oracle     | ✅                 | ❌                  |
| H2         | ✅                 | ✅                  |
| PostgreSQL | ✅                 | ✅                  |
| Kingbase   | ✅                 | ✅                  |
| SQLite     | ✅                 | ✅                  |
| openGauss  | ✅                 | ✅                  |

`onConflict` lives in `cn.xbatis.core.mybatis.mapper.context.strategy.SaveStrategy` / `SaveBatchStrategy` and the chain SQL interface `db.sql.api.cmd.executor.IInsert`, building portable statements via `IConflictAction`.

#### Single insert

```java
MultiPk entity = new MultiPk();
entity.setId1(1);
entity.setId2(2);
entity.setName("init");

mapper.save(entity, strategy -> {
    strategy.onConflict(action -> action.doNothing()); // ignore
    // or update on conflict (overwrite all fields)
    // strategy.onConflict(action -> action.doUpdate(update -> update.overwriteAll()));
});
```

#### Multiple inserts

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

`overwriteAll()` updates every column; `overwrite(Entity::getField)` targets specific columns.

#### InsertChain conflict strategy

```java
InsertChain.of(sysUserMapper)
    .insert(SysUser.class)
    .values(Arrays.asList("basic", "123456"))
    .onConflict(action -> action.doUpdate(update -> update.overwrite(SysUser::getPassword)))
    .execute();
```

Use `dbAdapt` inside the strategy for database-specific tuning.

---

## 7. Fluent DSL

### 7.1 QueryChain

`cn.xbatis.core.chain.QueryChain` constructs combinations of `select`, `from`, `join`, `where`, `groupBy`, `having`, `orderBy`, pagination, nested conditions, and subqueries.

- **AI generation tip**: default to Xbatis condition helpers such as `.forSearch(true)` or `eq(field, value, predicate)` / `eq(boolean, field, value)` instead of manual `if/else` on `WHERE` clauses to keep code tidy and robust.

Example:
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

Key capabilities:
- `select` accepts entities, VO classes, lambda fields, function wrappers, ignored columns.
- `join` supports `inner`, `left`, `right`; leverage `@ForeignKey` for automatic join conditions.
- `andNested` / `orNested` add parentheses; `and()` / `or()` control subsequent logic.
- Condition methods include `eq`, `ne`, `gt`, `gte`, `lt`, `lte`, `between`, `notBetween`, `in`, `notIn`, `like`, `notLike`, `isNull`, `isNotNull`, `empty`, `notEmpty`, `exists`, `notExists`. Each accepts a predicate argument for conditional inclusion (e.g., `eq(SysUser::getId, id, Objects::nonNull)`). Global toggles include `.ignoreNullValueInCondition(true)`, `.ignoreEmptyInCondition(true)`.
- `forSearch(true)` simultaneously ignores null/blank values and trims strings—ideal for search forms.
- `returnType(...)` is recommended right before terminal operations, after conditions/limits.
- `where(queryObject)` converts annotated DTO fields into conditions.
- `dbAdapt(selector -> ...)` builds DB-specific behavior.
- Terminal APIs: `get`, `list`, `count`, `exists`, `paging(Pager)`, `cursor`, `mapWithKey`.

### 7.2 InsertChain

`cn.xbatis.core.chain.InsertChain` supports `INSERT ... VALUES` and `INSERT ... SELECT` combos:
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
Add `values` repeatedly for batch inserts.

### 7.3 UpdateChain

`cn.xbatis.core.chain.UpdateChain` enables dynamic `set`, arithmetic updates, `RETURNING`, nested conditions:
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

`cn.xbatis.core.chain.DeleteChain` builds delete conditions and can return affected entities:
```java
List<SysUser> removed = DeleteChain.of(sysUserMapper)
    .in(SysUser::getId, 1, 2)
    .returning(SysUser.class)
    .returnType(SysUser.class)
    .executeAndReturningList();
```

---

## 8. Object-driven Conditions & Sorting

- Mark DTOs with `@ConditionTarget`.
- Use `@Condition` for EQ, NE, GT, LT, BETWEEN, EXISTS, LIKE, etc., specifying target property, defaults, `toEndDayTime`, `likeMode`.
- `@Conditions` maps a single field to multiple columns with AND/OR control.
- `@ConditionGroup` groups and nests logic.
- Implement `ObjectConditionLifeCycle` for preprocessing (e.g., deriving date ranges from enums) before building conditions.
- Sorting uses similar patterns via condition annotations and helper utilities.

Example:
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

## 9. Database Functions & SQL Templates

- Import `import static db.sql.api.impl.cmd.Methods.*;` for unified functions.
- Supported functions include `count`, `sum`, `avg`, `min`, `max`, `abs`, `ceil`, `floor`, `rand`, `sign`, `pi`, `truncate`, `round`, `pow`, `sqrt`, `exp`, `mod`, `log`, `sin`, `cos`, `tan`, `charLength`, `concat`, `upper`, `lower`, `substring`, `currentDate`, `dateDiff`, `dateAdd`, `inetAton`, plus MySQL-specific `findInSet`, `md5`, `jsonExtract`, `groupConcat`, etc.
- SQL template system:
  - `Methods.tpl` – general SQL template with placeholders `{0}`, `{1}`.
  - `Methods.fTpl` – function template, chainable with other functions.
  - `Methods.cTpl` – condition template for complex `WHERE` fragments.
  - Templates auto-escape single quotes; use `Methods.cTpl(true, "...", ...)` for automatic `'` handling.

Example:
```java
QueryChain.of(sysUserMapper)
    .select(SysUser::getRoleId, c -> Methods.tpl("count({0})+{1}", c, "1"))
    .and(GetterFields.of(SysUser::getId, SysUser::getId),
         cs -> Methods.cTpl("{0}+{1}={2}", cs[0], cs[1], 2));
```

---

## 10. Multi-database & Dynamic Datasource

### 10.1 Database differentiation

- `QueryChain`, `UpdateChain`, `DeleteChain`, `InsertChain` all support `dbAdapt`:
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
- You can also call `mapper.dbAdapt(selector -> {...})` for entirely different flows.

### 10.2 Dynamic datasource routing

- Add `cn.xbatis:xbatis-datasource-routing`.
- Configure `spring.ds.routing.*` for multiple datasources, master-slave, replica groups, and pool properties (supports Hikari, Druid, custom).
- Annotate classes/methods with `@DS("master")`, `@DS("slave")` to switch datasource.
- Notes:
  - Transaction propagation: use `@Transactional(propagation = Propagation.NOT_SUPPORTED)` or `REQUIRES_NEW` when switching datasources mid-call.
  - For self-invocation within same class, use `AopContext.currentProxy()` or split the class.
  - `spring.ds.jdbc-config-decrypt=true` with `JdbcConfigDecryptor` enables encrypted JDBC configs.
  - Supports distributed transaction frameworks like Seata (`spring.ds.seata=true`).

---

## 11. Multi-tenancy, Logical Delete, Optimistic Locking

### 11.1 Multi-tenancy

- Annotate entity fields with `@TenantId`; automatically fills tenant ID on write and appends conditions for query/update/delete.
- Register tenant suppliers via `TenantContext.registerTenantGetter(() -> tenantId)` (`cn.xbatis.core.tenant.TenantContext`), returning a single ID or `TenantId` (multi-tenant set).
- Return `null` from supplier to temporarily disable tenant enforcement.
- ThreadLocal usage: wrap helper like `TenantTLUtil` to set/clear tenants in filters/interceptors and return via `TenantContext`.

### 11.2 Logical delete

- `@LogicDelete(beforeValue = "0", afterValue = "1")` controls before/after markers and timestamp.
- `@LogicDeleteTime` controls logic delete time markers 
- `XbatisGlobalConfig.setLogicDeleteInterceptor` can fill deleter info automatically.
- Toggle globally via `setLogicDeleteSwitch(true/false)`; locally via:
```java
try (LogicDeleteSwitch ignored = LogicDeleteSwitch.with(false)) {
    mapper.getById(1);
}
// or
LogicDeleteUtil.execute(false, () -> mapper.getById(1));
```
- Note: `DeleteChain` performs physical delete; only mapper delete methods respect logical delete.

### 11.3 Optimistic lock

- `@Version` fields default to 0 on `save`.
- `update` / `delete` append `WHERE version=?`; updates increment version on success.

### 11.4 Table sharding (SplitTable)

- Annotate entity with `@SplitTable(TableSplitterClass.class)`; `value` points to `TableSplitter` implementation.
- Annotate sharding key with `@SplitTableKey`; only single-column sharding is supported.
- `TableSplitter#support(Class<?>)` declares supported key types; `split(String, Object)` returns real table name (e.g., `sys_user_3`).
- Chains behave like regular entities, but queries must include sharding key conditions to resolve actual tables.
- Works seamlessly with multi-db, tenant, logical delete, etc.

---

## 12. Dynamic Defaults & Event Listeners

- Built-in tokens: `{BLANK}`, `{NOW}` (supports `LocalDateTime`, `LocalDate`, `Date`, `Long`, `Integer`, `String`), `{TODAY}` (date range).
- Custom dynamic example:
```java
XbatisGlobalConfig.setDynamicValue("{day7}", (clazz, type) -> new LocalDate[]{
    LocalDate.now().minusDays(7), LocalDate.now()
});
```
- Fetch dynamic value inside condition objects via `XbatisGlobalConfig.getDynamicValue(clazz, LocalDate[].class, "{day7}")`.
- Global listeners:
```java
XbatisGlobalConfig.setGlobalOnInsertListener(entity -> {
    // fill creator, etc.
});
XbatisGlobalConfig.setGlobalOnUpdateListener(entity -> {
    // fill updater, etc.
});
```

---

## 13. SQL Templates & XML Integration

- Template mechanism enables reusable SQL fragments, function wrappers, condition composition without string splicing.
- Fully compatible with traditional MyBatis XML; use naming conventions in single-mapper mode:
```xml
<mapper namespace="xxx.MybatisBasicMapper">
  <select id="SysRole:selectByIds" resultType="com.xbatis.core.test.DO.SysRole">
    select * from sys_role
    where id >= #{id} and id <= #{id2}
    order by id asc
  </select>
</mapper>
```
Invocation:
```java
List<SysRole> roleList = mybatisBasicMapper.withSqlSession(
    SysRole.class, "selectByIds", params,
    (statement, p, sqlSession) -> sqlSession.selectList(statement, p)
);
```
- XML works alongside chain DSL: call templates within chains or pass dynamic params from XML as needed.

---

## 14. Pagination & Performance Tuning

- Built-in `Pager` handles pagination: `Pager.of(current, size)`, `QueryChain.paging(pager)` automatically executes query + count.
- Xbatis auto-optimizes pagination SQL: removes unnecessary `LEFT JOIN`, `ORDER BY`, replaces `SELECT` with `COUNT(*)` to improve speed.
- Customize via `XbatisGlobalConfig.setPagingProcessor` for Oracle, SQL Server, etc.
- Override `customizePager` for custom count queries or serialization.
- Use `.limit(n)` for top-N results and `.forSearch(true)` to optimize search endpoints.

---

## 15. Code Generator Capabilities

- `GeneratorConfig` configures generation of entities, Mapper interfaces, Mapper XML, DAO, Service, Controller, DTO, etc.
- Customize package paths, suffixes, annotation generation, ID strategy, column mapping, DB type mapping, base classes, XML generation, etc.
- Supports single-mapper mode: `mapperConfig.enable(false).superClass(MybatisBasicMapper.class)`.
- Options for generating `@Mapper`, generics, `resultMap`, and column info.
- Multi-language templates accelerate project scaffolding.

---

## 16. Startup Safety Checks (Mandatory for AI Projects)

Regardless of manual or AI-generated scaffolding, enable POJO safety checks during development: Xbatis validates VO (`@ResultEntity`), Model (`cn.xbatis.core.mvc.Model`), and condition/sort objects (`@ConditionTarget`, `@OrderByTarget`) at startup to catch mapping or annotation gaps. AI agents must generate the following config by default:

```java
// Required: enable POJO checks at the main application entry
@SpringBootApplication
@XbatisPojoCheckScan(basePackages = "com.example.project.pojo")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
```

```java
// Recommended: dev-only config, can be generated by AI
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

- Annotation attributes (`org.mybatis.spring.boot.autoconfigure.XbatisPojoCheckScan`):

  | Attribute | Description |
    | --- | --- |
  | `basePackages` | Base package path |
  | `modelPackages` | Model implementations; defaults to `basePackages` |
  | `resultEntityPackages` | VO (`@ResultEntity`) package; defaults to `basePackages` |
  | `conditionTargetPackages` | Condition DTO (`@ConditionTarget`) package |
  | `orderByTargetPackages` | Sorting DTO (`@OrderByTarget`) package |

### 16.2 Solon

- Configure in `solon.yml`:
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
- `pojoCheck` mirrors `@XbatisPojoCheckScan`; separate multiple paths with commas. Enable during dev/test to reduce production startup overhead.

---

## 17. Common Troubleshooting & Practical Tips

- **SQL logging**: add `<logger name="cn.xbatis" level="trace"/>` to print generated SQL and chain traces.
- **Ignore empty conditions**: `QueryChain` offers `.ignoreNullValueInCondition(true)`, `.ignoreEmptyInCondition(true)`, `.trimStringInCondition(true)`, or simply `.forSearch(true)`.
- **Safe updates**: `UpdateChain.set` accepts lambdas such as `c -> c.plus(1)` for increments.
- **Batch capabilities**: use `saveBatch`, `updateBatch`, `InsertChain.values`, or the `batchOpt` module for efficient batch handling.
- **Enum & dictionary mapping**: `@PutEnumValue` injects enum labels; `@ResultField` handles field-level mapping; `@Fetch` automates cascading queries.
- **SQL template gotchas**: if single quotes cause problems, enable protected templates or switch to double quotes.
- **Datasource switching under transactions**: mind transaction propagation when calling across datasources to avoid Spring proxies blocking switches.

---

## 17. Practical Advice for AI Agents

- **Retrieval strategy**: prioritize `QueryChain`, `InsertChain`, `UpdateChain`, `DeleteChain` plus annotations to cover most CRUD/statistics tasks.
- **Code generation tips**:
  - Always use method references (e.g., `SysUser::getId`) instead of hard-coded column names.
  - Search endpoints should call `.forSearch(true)` or rely on annotated DTOs to avoid manual null checks.
  - For multi-database scenarios, add `dbAdapt`; multi-tenant scenarios should wire `TenantContext` automatically.
  - When updates/deletes must return values, use `.executeAndReturning()` or `.executeAndReturningList()`.
- **Project structure tips**:
  - Single-mapper mode suits large projects; combine `BasicMapper` with DSL for unified data access.
  - Document dynamic datasource, tenant, and logical delete switches explicitly in generated code.
  - Use `GeneratorConfig` for initial scaffolding, then have the agent generate incremental chains/service logic.
- **Security & auditing**: leverage `@OnInsert`, `@OnUpdate`, logical delete interceptors, tenant context to auto-fill creator/update/tenant fields and satisfy audit requirements.

---

## Appendix: Productivity Cheatsheet (from `fast-dev.md`, essential for AI projects)

### Specify database type
Set `databaseId` explicitly to avoid detection overhead:
```yaml
mybatis:
  configuration:
    databaseId: MYSQL   # see db-support.md for available IDs
```

### Fluent API shorthand (AI-generated code should apply)
- When query, `from`, and `returnType` all target the mapper entity, omit them:
```java
SysUser user = QueryChain.of(sysUserMapper)
        .eq(SysUser::getId, 1)
        .get();
```
- If `select(VO.class)` equals `returnType(VO.class)`, just call `returnType`; the framework auto-selects columns based on VO annotations.

- Bulk ignore: `.forSearch(true)` enables ignore-null/blank and auto-trim; use it by default.
- Precise ignore:
```java
QueryChain.of(sysUserMapper)
    .eq(SysUser::getId, id, Objects::nonNull)
    .like(SysUser::getUserName, userName, StringUtils::isNotBlank)
    .get();
```

### VO auto-mapping
- Combine `@ResultEntity` + `@NestedResultEntity` for nested structures; omit attributes when VO fields match entity fields.
- Recommend using Lombok `@FieldNameConstants` and referencing `X.Fields.xxx` in annotations to avoid string literals.

### Using `connect`
`connect` exposes the chain itself for correlated subqueries:
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

### Recommended function invocation style
Static-import `db.sql.api.impl.cmd.Methods.*` for concise chains:
```java
Integer id = QueryChain.of(sysUserMapper)
        .select(SysUser::getId, c -> sum(add(c, 1)))
        .returnType(Integer.TYPE)
        .get();
```

These convenience patterns greatly enhance productivity and should be default assumptions when designing AI generation strategies.

---

Bundle this knowledge base with project source code and dependency API docs to build a vector store so AI agents can generate Xbatis-compliant code and configuration accurately.***
