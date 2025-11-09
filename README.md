# English | [简体中文](README.zh-CN.md) 
# Xbatis AI Agent Knowledge Base

> Official site: https://xbatis.cn  
> DeepWiki documentation: https://deepwiki.xbatis.cn

A self-contained knowledge pack designed for AI assistants. It summarizes the official Chinese documentation and highlights the essential concepts, usage patterns, and APIs of the Xbatis framework so that agents can generate or maintain projects without additional references.

---

## 1. Framework Overview

- **Positioning**: Xbatis builds on top of MyBatis and delivers a highly ORM-driven developer experience. Its core promise is “write less SQL, use fluent DSL, keep database portability.”
- **Key Advantages**:
  - Powerful query builder: multi-table joins, subqueries, fluent pagination, and automatic SQL optimizations (removing redundant `LEFT JOIN`/`ORDER BY`, smarter `COUNT`).
  - Built-in `RETURNING` support, batch insert/update DSL, database function wrappers, and SQL templates.
  - Single-Mapper mode lets you manage every entity through one `BasicMapper`.
  - Extensive multi-database function library, database-specific `dbAdapt`, and dynamic data source routing.
  - Rich annotation ecosystem: logical delete, multi-tenancy, optimistic locking, result mapping, conditional mapping, dynamic default values, etc.
- **Core traits**:
  1. Lightweight: wraps MyBatis rather than modifying its internals.
  2. High performance: remains close to handwritten SQL.
  3. Flexible & intuitive: fluent API reads almost like natural language.
  4. Broad coverage: aims to satisfy 90%+ CRUD and reporting scenarios.
  5. Reliable and safe: simple design, extensive APIs, and comprehensive test coverage.
  6. Pagination optimizations: automatically simplifies counts, prunes useless joins, and handles ordering.

---

## 2. Core Modules & Package Layout

| Module | Typical Package | Representative Types | Purpose |
| --- | --- | --- | --- |
| Core Mapper | `cn.xbatis.core.mybatis.mapper` | `MybatisMapper<T>`, `BasicMapper` | Provides base CRUD and single-mapper capabilities |
| Fluent DSL | `cn.xbatis.core.chain` / `cn.xbatis.core.sql.executor.chain` | `QueryChain`, `InsertChain`, `UpdateChain`, `DeleteChain` | Construct complex SQL, batch operations, returning clauses |
| Global config | `cn.xbatis.core.config` | `XbatisGlobalConfig` | Centralize naming rules, interceptors, dynamic values, pagination |
| Annotation ecosystem | `cn.xbatis.db.annotations` | `@Table`, `@TableId`, `@TableField`, `@LogicDelete`, `@TenantId`, `@Version`, `@Condition`, `@Fetch`, etc. | Entity mapping, data injection, conditional/VO mapping |
| SQL functions | `db.sql.api.impl.cmd` | `Methods` | Cross-DB function wrappers and templates |
| Multi-tenancy | `cn.xbatis.core.tenant` | `TenantContext`, `TenantId` | Tenant registration and propagation |
| Dynamic datasource | `cn.xbatis.datasource.routing` | `@DS`, `JdbcConfigDecryptor` | Runtime datasource switching, encrypted DS configs |
| Logical delete | `cn.xbatis.core.logic` | `LogicDeleteSwitch`, `LogicDeleteUtil` | Toggle logic-delete behavior, fill delete metadata |
| Dynamic values | `cn.xbatis.core.dynamic` | `XbatisGlobalConfig#setDynamicValue` | Define tokens like `{NOW}`, `{TODAY}` |
| Code generator | `cn.xbatis.codegen` | `GeneratorConfig` and friends | Generate entity/mapper/service/controller boilerplate |

---

## 3. Getting Started & Dependencies

### 3.1 Maven Coordinates (example)
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>cn.xbatis</groupId>
      <artifactId>xbatis-bom</artifactId>
      <version>1.9.2-M1</version>
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

### 3.2 DataSource Configuration
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dbName
    username: dbusername
    password: dbpassword
```

### 3.3 Bootstrapping Skeleton
```java
@SpringBootApplication
@MapperScan("com.xx.xxx.mapper")
public class XbatisApplication {
    public static void main(String[] args) {
        SpringApplication.run(XbatisApplication.class, args);
    }
}
```

### 3.4 Spring Boot 2 Integration

- **Dependencies** (`xbatis-spring-boot-starter`):
  ```xml
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>cn.xbatis</groupId>
        <artifactId>xbatis-spring-boot-parent</artifactId>
        <version>1.9.2-M1</version>
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
- Data source properties and `@MapperScan` identical to the general example above.

### 3.5 Solon Integration

- Dependencies:
  ```xml
  <!-- Important: register Xbatis plugin first, MyBatis plugin second -->
  <dependency>
    <groupId>cn.xbatis</groupId>
    <artifactId>xbatis-solon-plugin</artifactId>
    <version>1.9.2-M1</version>
  </dependency>
  <dependency>
    <groupId>org.noear</groupId>
    <artifactId>mybatis-solon-plugin</artifactId>
    <version>${mybatis.solon.version}</version>
  </dependency>
  ```
- Configuration snippet:
  ```yaml
  # solon.yml
  ds:
    schema: demo
    jdbcUrl: jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456

  mybatis.master:
    mappers:
      - "com.demo.mapper"
      - "classpath:mapper/**/*.xml"
  ```
- Data source bean example:
  ```java
  @Configuration
  public class MybatisConfig {

      @Bean(name = "master", typed = true)
      public DataSource dataSource(@Inject("${ds}") HikariDataSource ds) {
          return ds;
      }
  }
  ```
- Usage:
  ```java
  @Controller
  public class DemoController {

      @Db
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

---

## 4. Global Configuration (`XbatisGlobalConfig`)

Configure globally during startup (via `ConfigurationCustomizer` or `@PostConstruct`). Notable options:
- `setSingleMapperClass(MybatisBasicMapper.class)`: enable single-mapper mode.
- `setTableUnderline(boolean)` / `setColumnUnderline(boolean)`: control naming conventions.
- `setDatabaseCaseRule(DatabaseCaseRule rule)`: define case rules per DB.
- `setDynamicValue("{KEY}", (clazz, type) -> ...)`: register reusable dynamic placeholders.
- `setLogicDeleteInterceptor(...)`, `setLogicDeleteSwitch(boolean)`: customize logical delete metadata.
- `addMapperMethodInterceptor(...)`: register method interceptors.
- `setPagingProcessor(DbType, PagingProcessor)`: override pagination strategy per DB.

---

## 5. Annotation Ecosystem (Highlights)

### 5.1 `@Table`
- Maps an entity to a table. Attributes include `value`, `schema`, `columnNameRule`, `databaseCaseRule`.

### 5.2 `@TableId`
- Controls primary-key strategy per DB: `AUTO`, `NONE`, `SQL`, `GENERATOR`.

### 5.3 `@TableField`
- Controls column mapping, insert/update/select participation, default values, type handlers, etc.

### 5.4 `@LogicDelete`
- Declares logical delete values and optional delete timestamp field.

### 5.5 `@TenantId`
- Marks tenant column for automatic fill and condition injection.

### 5.6 `@Version`
- Enables optimistic locking by maintaining a version column.

### 5.7 Lifecycle Hooks: `@OnInsert`, `@OnUpdate`
- Register entity-level hooks or complement with global listeners via `XbatisGlobalConfig`.

### 5.8 Result-mapping Annotations (`@ResultEntity`, `@ResultField`, `@Fetch`, ...)
- Build VO structures with automatic column selection, nested mapping, enum conversion, etc.

### 5.9 Sharding Annotations (`@SplitTable`, `@SplitTableKey`, `TableSplitter`)
- Declaratively support table sharding; implement `TableSplitter` to resolve real table names.

### 5.10 Condition & Sorting Annotations (`@ConditionTarget`, `@Condition`, `@Conditions`, `@ConditionGroup`)
- Drive query conditions from DTOs. Combine with `ObjectConditionLifeCycle` to preprocess inputs.

---

## 6. Mapper Capabilities

`MybatisMapper<T>` ships with extensive CRUD APIs:
- **Read**: `get`, `list`, `listAll`, `listByIds`, `cursor`, `exists`, `count`, `mapWithKey`, `paging`, etc.
- **Create**: `save`, `saveOrUpdate`, `saveBatch`, `saveModel`, etc.
- **Update**: `update`, `updateBatch`, `saveOrUpdate`, force-update fields, batch `CASE WHEN` updates.
- **Delete**: `deleteById`, `deleteByIds`, `delete(entity)`, `delete(where)`, `deleteAll`, `truncate`, `DeleteChain`.

### 6.1 Single-Mapper Mode (Xbatis-specific)
1. **Define a Basic Mapper interface**
   ```java
   public interface MybatisBasicMapper extends BasicMapper {
   }
   ```
2. **Mapper scan** (Spring Boot example)
   ```java
   @MapperScan(basePackageClasses = MybatisBasicMapper.class,
               markerInterface = BasicMapper.class)
   ```
   This registers a single Mapper bean for all entities.
3. **Register the global single Mapper**
   ```java
   XbatisGlobalConfig.setSingleMapperClass(MybatisBasicMapper.class);
   ```
4. **Usage**
   ```java
   @Autowired
   private MybatisBasicMapper basicMapper;

   public void demo() {
       basicMapper.save(new SysUser());
       basicMapper.deleteById(SysUser.class, 1);

       QueryChain.of(basicMapper, SysUser.class)
               .eq(SysUser::getId, 1)
               .list();
   }
   ```
5. **Custom SQL**
   Use `withSqlSession` with the VO/entity `Class` to execute mapped statements inside `BasicMapper` namespace.
6. **Caveats**
   - Fluent DSL requires explicit entity class when using `BasicMapper`.
   - You can mix single-mapper and traditional dedicated mapper interfaces.
   - XML statement IDs follow the `EntityName:method` convention for `withSqlSession`.

### 6.2 Insert / Batch Insert Conflict Handling
- `save`/`saveBatch` provide `.onConflict` strategies: ignore or update on duplicates.
- Works across MySQL, MariaDB, H2, PostgreSQL, etc.
- `InsertChain` exposes the same `onConflict` fluent API.

---

## 7. Fluent DSL Overview

### 7.1 `QueryChain`
- Supports `select`, `from`, `join`, `where`, `groupBy`, `having`, `orderBy`, pagination, nested conditions, subqueries.
- Convenience methods: `.forSearch()` (ignore null/blank & trim), `.ignoreNullValueInCondition`, etc.
- Terminal operations: `get`, `list`, `count`, `exists`, `paging`, `cursor`, `mapWithKey`.
- Example:
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

### 7.2 `InsertChain`
- Build `INSERT ... VALUES` or `INSERT ... SELECT` statements in fluent style.

### 7.3 `UpdateChain`
- Fluent updates with `set`, arithmetic functions (`plus`, etc.), `RETURNING`, nested conditions.

### 7.4 `DeleteChain`
- Construct delete conditions and optionally return affected rows/objects.

---

## 8. Object-driven Conditions & Sorting

- Annotate DTOs with `@ConditionTarget`, `@Condition`, `@Conditions`, `@ConditionGroup`.
- Implement `ObjectConditionLifeCycle` to pre-process request parameters.
- Combine with order-by helpers to auto-generate `ORDER BY` clauses.

---

## 9. SQL Functions & Templates

- Import `db.sql.api.impl.cmd.Methods.*` for cross-DB functions (`sum`, `concat`, `upper`, `dateAdd`, `groupConcat`, etc.).
- Template variants: `tpl`, `fTpl`, `cTpl` for dynamic SQL snippets.
- Example:
  ```java
  QueryChain.of(sysUserMapper)
      .select(SysUser::getRoleId, c -> Methods.tpl("count({0})+{1}", c, "1"))
      .and(GetterFields.of(SysUser::getId, SysUser::getId),
           cs -> Methods.cTpl("{0}+{1}={2}", cs[0], cs[1], 2));
  ```

---

## 10. Multi-Database & Dynamic Datasource

### 10.1 Database-specific Logic (`dbAdapt`)
- Adjust SQL or conditions per DB inside `QueryChain`/`UpdateChain`/`DeleteChain`/`InsertChain`.
- Example:
  ```java
  QueryChain.of(sysUserMapper)
      .select(SysUser::getId)
      .dbAdapt((query, selector) -> selector
          .when(DbType.H2, db -> query.eq(SysUser::getId, 3))
          .when(DbType.MYSQL, db -> query.eq(SysUser::getId, 2))
          .otherwise(db -> query.eq(SysUser::getId, 1)))
      .get();
  ```

### 10.2 Dynamic Datasource Routing
- Use `cn.xbatis:xbatis-datasource-routing` with `@DS` to switch datasources.
- Supports encrypted configs, Seata integration, per-method routing.
- Mind transaction propagation when switching data sources.

---

## 11. Multi-Tenancy, Logical Delete, Optimistic Lock

### 11.1 Multi-Tenancy
- Mark tenant columns with `@TenantId` and register a supplier via `TenantContext.registerTenantGetter`.
- Temporary disable via `TenantContext` returning `null`.

### 11.2 Logical Delete
- Configure `@LogicDelete` on entities, fill extra metadata with global interceptors, toggle per-thread using `LogicDeleteSwitch`.

### 11.3 Optimistic Lock
- `@Version` auto-increments versions and enforces safe updates/deletes.

### 11.4 Table Sharding (Split Table)
- Declare `@SplitTable` and provide a `TableSplitter` to compute actual table names. Ensure conditions include the shard key.

---

## 12. Dynamic Defaults & Event Hooks

- Built-in tokens: `{BLANK}`, `{NOW}`, `{TODAY}` (with type-specific conversions).
- Define custom tokens via `XbatisGlobalConfig.setDynamicValue`.
- Register global insert/update listeners for auditing fields.

---

## 13. SQL Templates & XML Integration

- Xbatis remains compatible with traditional MyBatis XML.
- In single-mapper mode, XML namespaces typically use `xxx.MybatisBasicMapper` and method IDs follow `EntityName:method` naming.
- Invoke via `basicMapper.withSqlSession(entityClass, statementId, params, callback)`.

---

## 14. Pagination & Performance Tuning

- Use `Pager.of(page, size)` and `QueryChain.paging(pager)`.
- Xbatis auto-optimizes pagination queries.
- Customize `PagingProcessor` or `customizePager` for advanced scenarios.
- Combine `.limit`, `.forSearch(true)` to trim heavy queries.

---

## 15. Code Generator Highlights

- `GeneratorConfig` can generate entities, mapper interfaces, XML, DAO, service, controller, DTO, etc.
- Supports single-mapper scaffolding (`mapperConfig.enable(false).superClass(MybatisBasicMapper.class)`).
- Configurable naming, annotations, base classes, XML generation, type mapping.

---

## 16. Startup Safety Checks (Highly Recommended for AI Projects)

- Enable POJO safety checks to validate VO/DTO/model definitions at startup.
- Example (Spring Boot):
  ```java
  @SpringBootApplication
  @XbatisPojoCheckScan(basePackages = "com.example.project.pojo")
  public class ApiApplication {
      public static void main(String[] args) {
          SpringApplication.run(ApiApplication.class, args);
      }
  }
  ```
- Development-only configuration:
  ```java
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
- `@XbatisPojoCheckScan` attributes mirror those in the YAML setup for Solon.

---

## 17. Practical Tips & Troubleshooting

- **SQL logging**: set `<logger name="cn.xbatis" level="trace"/>` to inspect generated SQL.
- **Condition trimming**: use `.forSearch(true)` or `.ignoreNullValueInCondition(true)` to avoid manual null checks.
- **Safe updates**: `UpdateChain.set` accepts lambdas like `c -> c.plus(1)` for counters.
- **Batch operations**: leverage `saveBatch`, `updateBatch`, or the dedicated `batchOpt` module.
- **Enum & dictionary mapping**: `@PutEnumValue`, `@ResultField`, `@Fetch` ease conversions.
- **SQL templates**: when dealing with quotes, switch to template modes that auto-escape.
- **Datasource switching & transactions**: watch propagation behavior, especially for self-invocations.

---

## 18. Guidance for AI Agents

- Prefer fluent DSL (`QueryChain`, `InsertChain`, etc.) and annotation-driven DTOs.
- Use method references (`SysUser::getId`) to avoid hard-coded columns.
- Default to `.forSearch(true)` for search endpoints.
- For multi-DB projects, ensure `dbAdapt` is in place; for multi-tenant apps, wire `TenantContext` automatically.
- When generating updates/deletes that return entity data, call `.executeAndReturning()` / `.executeAndReturningList()`.
- Combine the code generator for initial scaffolding with AI-generated business-specific chains.
- Enable audit fields via listeners (`@OnInsert`, `@OnUpdate`, logic delete interceptors, tenant context).
- Keep this knowledge base and project code in your embedding store to improve accuracy when generating patches.

---

## Appendix: Quick Reference for Fast Development

- Explicitly set `databaseId` (e.g., `MYSQL`) in `mybatis.configuration.databaseId` to skip auto detection.
- Shorthand query pattern:
  ```java
  SysUser user = QueryChain.of(sysUserMapper)
          .eq(SysUser::getId, 1)
          .get();
  ```
- VO auto mapping: combine `@ResultEntity` with `@NestedResultEntity`, and use Lombok `@FieldNameConstants` to avoid magic strings.
- `connect` allows complex subqueries:
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
- Import `db.sql.api.impl.cmd.Methods.*` for readable expressions (`sum`, `add`, etc.).

---

This English edition mirrors the official Chinese documentation so AI agents can build, refactor, and reason about Xbatis-based projects effectively.
