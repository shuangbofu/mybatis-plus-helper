# mybatis-plus-helper使用文档

为了方便开发，对mybatis-plus的mapper部分进行了封装，主要用作于减少重复的代码和可以自定义的全局查询条件

主要是对mapper包装了一下，结合spring提供的接口，接口代理方式实现了通用dao自动注入使用。

**使用方式**

> 1、引入依赖
>
> 2、增加``@DaoScan``注解指定包路径
>
> 3、定义接口xxxDao和mybatis的xxxMapper
>
> 4、``@Autowired``引入使用xxxDao的通用方法

## 使用方式及代码对比

1、重复代码包括不限于每次都要```new QueryWrapper()（还要指定泛型类型），环境变量条件，逻辑删除条件，更新时间字段，更新用户字段……

拿查询单个值举例

### 查询单个值

因为查询单个值的情况下除了以上情况，还存在queryMapper.select和对结果的map一定是一致的情况，所以可以减少部分重复代码。

```java
// userMapper
String userId=Optional.ofNullable(
        userMapper.selectOne(
        new QueryWrapper<User>()
        .eq("deleted",0)
        .eq("env","dev")
        .eq("region","AY")
        .lambda()
        .select(User::getUserId)
        .like(User::getName,"%john%")))
        .map(Task::getUserId).orElse("");

// 包装后的userDao
        String userId=userDao.selectValueBy(
				User::getUserId, i->i.lambda()
        .like(Task::getName,"%john%",() => ""));
```

2、mybatis-plus全局参数和逻辑删除还都是全局的，不能每个表/entity自定义。

### 其他

```java
// 具体设置条件、值的逻辑依然是mybatis-plus，具体看接口通用方法
```

## 详细使用步骤

### 1、引入依赖

引入mybatisplus-helper

```xml
<dependencies>
    <dependency>
        <artifactId>mybatis-plus-helper</artifactId>
        <groupId>io.github.shuangbo</groupId>
    </dependency>
</dependencies>
```

### 2、开启dao扫描

不需要``@MapperScan``，直接使用``@DaoScan``分别指定dao和mapper包路径

```java

@SpringBootApplication
@DaoScan(
        // xxxDao包路径
        basePackages = "com.example.demo.dao",
        // xxxMapper包路径
        mapperBasePackages = "com.example.demo.dao.mapper")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

### 3、编写代码

除了xxxDO和xxxMapper，增加了xxxDao，增删改查就使用dao来操作

#### Do部分

##### 通用实现

只要继承对应基础类就没有其他配置。

- IdEntity 只有id
- BaseEntity 带gmt_create，gmt_modified

可以自己实现其他通用baseEntity，比如

- UserEntity 带gmt_create，gmt_modified，create_user，modified_user
- EnvEntity 带gmt_create，gmt_modified，create_user，modified_user，env，region

```java

@Data
// mybatis-plus注解
@TableName("demo_table")
public class DemoDO extends IdEntity {
    private String name;
}
```

###### IdEntity

最基础的基类，不需要其他通用字段

```java
public class IdEntity {
    private Long id;

    public IdEntity() {
    }
}
```

###### BaseEntity

除了id，增加了gmt_create和gmt_modified字段和逻辑删除注解 ``@LogicalDelete``

- 插入时更新gmt_create和gmt_modified
- 更新时的自动更新gmt_mnodified字段
- 逻辑删除注解，查询和更新时自动带上逻辑删除条件

```java

@LogicalDelete(
        name = "deleted",
        valid = "0",
        inValid = "1"
)
@LoadDaoHook(SetTimeHook.class)
public abstract class BaseEntity extends IdEntity {
    @TableField("gmt_create")
    private Long createTime;
    @TableField("gmt_modified")
    private Long updateTime;
}
```

###### UserEntity

可以通过写UserEntity和UserHook来通用

```java
/**
 * 除了BaseEntity的字段，增加了用户相关字段，
 */
@LoadDaoHook(UserHook.class)
@Data
public class UserEntity extends BaseEntity {
    private String createUser;
    private String modifiedUser;
}

```

```java
public interface UserHook extends DaoHook<UserEntity> {
}
```

可以通过实现UserHook接口并``@component``注册到spring中

实现UserHook

```java
/**
 *	实现逻辑
 */
@Component
public class UserHookImpl implements UserHook {
    @Override
    public void insert(UserEntity entity) {
        // 用户数据的动态值
        // 比如使用ThreadLocal，在登陆的切面set值，这里就可以使用了
        // entity.setCreateUser(UserThreadLocal.getValue());
        // entity.setModifiedUser(UserThreadLocal.getValue());
    }

    @Override
    public void defaultUpdate(UpdateWrapper<UserEntity> uw) {
        // uw.set("create_user", UserThreadLocal.getValue());
    }
}
```

###### EnvEntity

除了UserEntity的字段，如果还有环境和区域的字段的需求，查询时需要带上环境字段

```java
public interface EnvHook extends DaoHook<UserEntity> {
}
```

```java

@LoadDaoHook(EnvHook.class)
public class EnvEntity extends UserEntity {
    private Env env;
    private Region region;
}
```

实现EnvHook，比如可以从配置文件或者配置服务中心获取动态配置

```java

@Configuration
public class EnvHookImpl implements EnvHook {

    @Value("${env}")
    private Env env;

    @Value("${region}")
    private Region region;

    @Override
    public void insert(EnvEntity entity) {
        entity.setEnv(env);
        entity.setRegion(region);
    }

    @Override
    public void defaultQuery(QueryWrapper<EnvEntity> queryWrapper) {
        queryWrapper.eq("env", env);
        queryWrapper.eq("region", region);
    }
}
```

##### 自定义实现

如果逻辑删除含义变动，可以通过``@LogicalDelete``修改覆盖继承的类

```java
@LogicalDelete(name = "xxx", valid = "0", inValid = "1")
```

如果需要自动设置值，可以在DO上增加注解，实现自定义接口并``@component``注册到spring中，或者简单实现只是设置固定值不依赖其他类可以``newInstance = true``

```java
@LoadDaoHook(XXXDaoHook.class, newInstance = false)
```

DaoHook接口

```java
public interface DaoHook<ENTITY> {
    /**
     *	插入时设置值
     */
    default void insert(ENTITY entity) {
    }

    /**
     * 查询时增加条件
     */
    default void defaultQuery(QueryWrapper<ENTITY> queryWrapper) {
    }

    /**
     * 更新时更新值、增加条件
     */
    default void defaultUpdate(UpdateWrapper<ENTITY> updateWrapper) {
    }
}
```

比如BaseEntity更新值的实现

```java
public class SetTimeHook implements DaoHook<BaseEntity> {
    public static final String GMT_CREATE = "gmt_create";
    public static final String GMT_MODIFIED = "gmt_modified";

    public SetTimeHook() {
    }

    public void insert(BaseEntity entity) {
        long now = System.currentTimeMillis();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
    }

    public void defaultUpdate(UpdateWrapper<BaseEntity> updateWrapper) {
        updateWrapper.set(GMT_MODIFIED, System.currentTimeMillis());
    }

    public void defaultQuery(QueryWrapper<BaseEntity> queryWrapper) {
        queryWrapper.orderByDesc(GMT_CREATE);
    }
}
```

#### Mapper部分

``@DaoScan``指定包下定义接口

```java
public interface DemoMapper extends BaseMapper<DemoDO> {
}
```

#### Dao部分

``@DaoScan``指定包下定义接口

```java
public interface DemoDao extends BaseDao<DemoDO, DemoMapper> {
}
```

#### 其他

##### mybatis配置

mybatis及mybatis-plus配置方式，yml里各种配置或者bean（比如分页插件）

### 使用方式

直接``@Autowired``

```java
public class HelloController {
    @Autowired
    private DemoDao demoDao;

    @RequestMapping("/list")
    public List<DemoDo> hello() {
        return demoDao.selectAll();
    }
}
```

### Dao接口通用方法展示

```java
public interface BaseDao<ENTITY extends IdEntity, MAPPER extends BaseMapper<ENTITY>> {

    /**
     * 根据id集合查询多条记录
     *
     * @param ids ids
     * @return 多条记录
     */
    List<ENTITY> selectListInIds(List<Long> ids);

    /**
     * 根据条件查找单条记录
     *
     * @param queryHandler 查询条件处理
     * @return 单条记录
     */
    ENTITY selectOneBy(QueryHandler<ENTITY> queryHandler);

    /**
     * 根据id查找单条记录
     *
     * @param id id
     * @return 单条记录
     */
    ENTITY selectOneById(Long id);

    /**
     * 根据条件计算数量
     *
     * @param queryHandler 查询条件处理
     * @return 数量
     */
    long countBy(QueryHandler<ENTITY> queryHandler);

    /**
     * 计算总数
     *
     * @return 数量
     */
    long countAll();

    /**
     * 根据条件查找多条记录
     *
     * @param queryHandler 查询条件处理
     * @return 记录集合
     */
    List<ENTITY> selectListBy(QueryHandler<ENTITY> queryHandler);

    /**
     * 查找全部记录
     *
     * @return 记录集合
     */
    List<ENTITY> selectAll();


    /**
     * 根据条件查找值集合
     *
     * @param sFunction    支持序列化的lambda方法
     * @param queryHandler 查询条件处理
     * @param <VALUE>      值类型
     * @return 值的集合
     */
    <VALUE> List<VALUE> selectValueListBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler);

    /**
     * 根据条件查找值
     *
     * @param sFunction    支持序列化的lambda方法
     * @param queryHandler 查询条件处理
     * @param <VALUE>      值类型
     * @return 值
     */
    <VALUE> VALUE selectValueBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler, Supplier<VALUE> supplier);

    <VALUE> VALUE selectValueBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler);

    /**
     * 根据id查找值
     *
     * @param sFunction 支持序列化的lambda方法
     * @param id        id
     * @param <VALUE>   值类型
     * @return 值
     */
    <VALUE> VALUE selectValueById(SFunction<ENTITY, VALUE> sFunction, Long id, Supplier<VALUE> supplier);

    <VALUE> VALUE selectValueById(SFunction<ENTITY, VALUE> sFunction, Long id);

    /**
     * 根据条件更新
     *
     * @param updateHandler 更新条件及set值的处理
     * @return 更新影响条数
     */
    int updateBy(UpdateHandler<ENTITY> updateHandler);

    /**
     * 根据id更新
     *
     * @param id            id
     * @param updateHandler 更新条件及set值的处理
     * @return 更新影响条数
     */
    int updateById(Long id, UpdateHandler<ENTITY> updateHandler);

    /**
     * 根据条件更新实体
     *
     * @param entity        实体
     * @param updateHandler 更新条件及set值的处理
     * @return 更新影响条数
     */
    int updateEntityBy(ENTITY entity, UpdateHandler<ENTITY> updateHandler);

    /**
     * 根据id更新实体
     *
     * @param entity 实体
     * @param id     id
     * @return 更新影响条数
     */
    int updateEntityById(ENTITY entity, Long id);

    /**
     * 根据id更新值
     *
     * @param id        id
     * @param sFunction 支持序列化的lambda方法
     * @param value     值
     * @param <VALUE>   值类型
     * @return 更新影响条数
     */
    <VALUE> int updateValueById(Long id, SFunction<ENTITY, VALUE> sFunction, VALUE value);

    /**
     * 根据条件删除
     *
     * @param handler 条件处理（使用update handler是为了逻辑删除可以用同一个接口，只要handler里不set值就没问题）
     * @return 删除影响条数
     */
    int deleteBy(UpdateHandler<ENTITY> handler);

    /**
     * 根据id删除
     *
     * @param id id
     * @return 删除影响条数
     */
    int deleteById(Long id);

    /**
     * 根据id批量删除
     *
     * @param ids id集合
     * @return 删除影响条数
     */
    int deleteBatchIds(List<Long> ids);

    /**
     * 获取dao对应的mybatis mapper（非特殊情况不建议使用，不带有默认条件）
     *
     * @return mybatis-plus的baseMapper
     */
    MAPPER getMapper();

    /**
     * 根据条件查询任意格式单条记录
     *
     * @param handler 查询条件处理
     * @return 单条原始sql行列数据
     */
    Map<String, Object> selectMap(QueryHandler<ENTITY> handler);

    /**
     * 根据条件查询任意格式多条记录
     *
     * @param handler 查询条件处理
     * @return 多条原始sql行列数据
     */
    List<Map<String, Object>> selectMapList(QueryHandler<ENTITY> handler);

    /**
     * 插入实体对象
     *
     * @param entity 实体
     * @return 插入影响条数
     */
    int insert(ENTITY entity);

    /**
     * 批量插入实体对象
     *
     * @param entities 实体列表
     * @return 插入影响条数
     */
    @Transactional
    int batchInsert(List<ENTITY> entities);

    /**
     * 根据条件分页查询
     *
     * @param pageNum  第n页
     * @param pageSize 页数
     * @param handler  查询条件处理
     * @return 分页对象
     */
    Page<ENTITY> selectPage(int pageNum, int pageSize, QueryHandler<ENTITY> handler);

    /**
     * 根据条件查询任意格式分页记录
     *
     * @param pageNum  第n页
     * @param pageSize 页数
     * @param handler  查询条件处理
     * @return 分页对象
     */
    Page<Map<String, Object>> selectMapPage(int pageNum, int pageSize, QueryHandler<ENTITY> handler);
}

```

